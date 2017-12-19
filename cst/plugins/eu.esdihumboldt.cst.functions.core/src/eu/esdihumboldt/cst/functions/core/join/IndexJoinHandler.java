/*
 * Copyright (c) 2017 wetransform GmbH
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.functions.core.join;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.cst.functions.core.join.JoinUtils.JoinDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.core.service.ServiceProviderAware;
import eu.esdihumboldt.hale.common.instance.index.DeepIterableKey;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndex;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexService;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.IdentifiableInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.InstanceDecorator;

/**
 * Join based on equal properties using an {@link InstanceIndex}
 * 
 * @author Florian Esser
 */
public class IndexJoinHandler implements InstanceHandler<TransformationEngine>, JoinFunction,
		ServiceProviderAware, JoinIndexValueProcessor {

	private ServiceProvider serviceProvider;

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler#partitionInstances(eu.esdihumboldt.hale.common.instance.model.InstanceCollection,
	 *      java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine,
	 *      com.google.common.collect.ListMultimap, java.util.Map,
	 *      eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog)
	 */
	@Override
	public ResourceIterator<FamilyInstance> partitionInstances(InstanceCollection instances,
			String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, ParameterValue> transformationParameters,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException {

		if (transformationParameters == null
				|| !transformationParameters.containsKey(PARAMETER_JOIN)
				|| transformationParameters.get(PARAMETER_JOIN).isEmpty()) {
			throw new TransformationException("No join parameter defined");
		}

		JoinHandler fallbackHandler = new JoinHandler();

		InstanceIndexService indexService = serviceProvider.getService(InstanceIndexService.class);
		if (indexService == null) {
			// Fall back to JoinHandler

			log.warn(MessageFormat.format(
					"Index service not available, falling back to join handler {0}",
					fallbackHandler.getClass().getCanonicalName()));
			return fallbackHandler.partitionInstances(instances, transformationIdentifier, engine,
					transformationParameters, executionParameters, log);
		}

		JoinParameter joinParameter = transformationParameters.get(PARAMETER_JOIN).get(0)
				.as(JoinParameter.class);

		String validation = joinParameter.validate();
		if (validation != null) {
			throw new TransformationException("Join parameter invalid: " + validation);
		}

		List<TypeEntityDefinition> types = joinParameter.types;

		JoinDefinition joinDefinition = JoinUtils.getJoinDefinition(joinParameter);

		// remember instances of first type to start join afterwards
		Collection<InstanceReference> startInstances = new LinkedList<InstanceReference>();

		// TODO How to get rid of this performance sink?
		List<Object> inputInstanceIds = new ArrayList<>();
		try (ResourceIterator<Instance> it = instances.iterator()) {
			while (it.hasNext()) {
				Instance i = InstanceDecorator.getRoot(it.next());

				// remember instances of first type
				if (i.getDefinition().equals(types.get(0).getDefinition())) {
					startInstances.add(instances.getReference(i));
				}

				if (!(i instanceof IdentifiableInstance) || !((IdentifiableInstance) i).hasId()) {
					log.warn(MessageFormat.format(
							"At least one instance does not have an ID, falling back to join handler {0}",
							fallbackHandler.getClass().getCanonicalName()));
					return fallbackHandler.partitionInstances(instances, transformationIdentifier,
							engine, transformationParameters, executionParameters, log);
				}
				inputInstanceIds.add(((IdentifiableInstance) i).getId());
			}
		}

		// JoinProperty -> (Value -> Collection<Reference>)
		Map<PropertyEntityDefinition, Multimap<Object, InstanceReference>> index = indexService
				.subIndex(joinDefinition.properties);

		return new JoinIterator(instances, startInstances, joinDefinition.directParent, index,
				joinDefinition.joinTable, this);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.service.ServiceProviderAware#setServiceProvider(eu.esdihumboldt.hale.common.core.service.ServiceProvider)
	 */
	@Override
	public void setServiceProvider(ServiceProvider services) {
		this.serviceProvider = services;
	}

	/**
	 * @see eu.esdihumboldt.cst.functions.core.join.JoinIndexValueProcessor#processValue(java.lang.Object,
	 *      eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition)
	 */
	@Override
	public Object processValue(Object value, PropertyEntityDefinition property) {
		return new DeepIterableKey(value);
	}

}
