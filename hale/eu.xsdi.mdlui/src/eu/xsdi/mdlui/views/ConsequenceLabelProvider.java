/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
 */
package eu.xsdi.mdlui.views;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;

import eu.xsdi.mdl.model.Consequence;
import eu.xsdi.mdl.model.consequence.DataQualityElement;
import eu.xsdi.mdl.model.consequence.MismatchContext;

/**
 * Provides text labels based on a {@link Consequence} model for a {@link TreeViewer}.
 * 
 * @author Thorsten Reitz, thor@xsdi.eu
 * @version $Id$ 
 * @since 0.1.0
 */
public class ConsequenceLabelProvider extends LabelProvider {
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getText(Object element) {
		if (element instanceof Set) {
			return "Consequences: ";
		}
		else if (element instanceof Consequence) {
			Consequence c = (Consequence) element;
			return "Consequence " + c.hashCode();
		}
		else if (element instanceof MismatchContext) {
			MismatchContext mc = (MismatchContext) element;
			return "Context: " + mc.toString();
		}
		else if (element instanceof DataQualityElement) {
			DataQualityElement dqe = (DataQualityElement) element;
			return dqe.getType();
		}
		else if (element instanceof List) {
			return "Impacts: ";
		}
		else {
			return super.getText(element);
		}
	}

}
