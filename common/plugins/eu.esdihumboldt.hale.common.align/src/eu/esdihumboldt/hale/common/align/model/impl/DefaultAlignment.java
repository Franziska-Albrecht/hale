/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default alignment implementation.
 * 
 * @author Simon Templer
 */
public class DefaultAlignment implements Alignment, MutableAlignment {

	/**
	 * List with all cells contained in the alignment. XXX use a LinkedHashSet
	 * instead?
	 */
	private final Collection<MutableCell> cells = new ArrayList<MutableCell>();

	/**
	 * List with all type cells contained in the alignment.
	 */
	private final Collection<MutableCell> typeCells = new ArrayList<MutableCell>();

	/**
	 * Entity definitions mapped to alignment cells.
	 */
	private final ListMultimap<EntityDefinition, Cell> cellsPerEntity = ArrayListMultimap.create();

	/**
	 * Source types mapped to alignment cells.
	 */
	private final ListMultimap<TypeDefinition, Cell> cellsPerSourceType = ArrayListMultimap
			.create();

	/**
	 * Target types mapped to alignment cells.
	 */
	private final ListMultimap<TypeDefinition, Cell> cellsPerTargetType = ArrayListMultimap
			.create();

	/**
	 * @see MutableAlignment#addCell(MutableCell)
	 */
	@Override
	public void addCell(MutableCell cell) {
		internalAdd(cell);
	}

	/**
	 * Add a cell to the various internal containers.
	 * 
	 * @param cell the cell to add
	 */
	private void internalAdd(MutableCell cell) {
		cells.add(cell);

		// check if cell is a type cell
		if (AlignmentUtil.isTypeCell(cell)) {
			typeCells.add(cell);
		}

		// add to maps
		internalAddToMaps(cell.getSource(), cell);
		internalAddToMaps(cell.getTarget(), cell);
	}

	/**
	 * Add a cell to the internal indexes, based on the given associated
	 * entities.
	 * 
	 * @param entities the cell entities (usually either source or target)
	 * @param cell the cell to add
	 */
	private void internalAddToMaps(ListMultimap<String, ? extends Entity> entities, Cell cell) {
		if (entities == null) {
			return;
		}

		for (Entity entity : entities.values()) {
			EntityDefinition entityDef = entity.getDefinition();
			cellsPerEntity.put(entityDef, cell);

			switch (entityDef.getSchemaSpace()) {
			case TARGET:
				cellsPerTargetType.put(entityDef.getType(), cell);
				break;
			case SOURCE:
				cellsPerSourceType.put(entityDef.getType(), cell);
				break;
			default:
				throw new IllegalStateException(
						"Entity definition with illegal schema space encountered");
			}
		}
	}

	/**
	 * @see Alignment#getCells(EntityDefinition)
	 */
	@Override
	public Collection<? extends Cell> getCells(EntityDefinition entityDefinition) {
		return Collections.unmodifiableCollection(cellsPerEntity.get(entityDefinition));
	}

	/**
	 * @see Alignment#getCells(TypeDefinition, SchemaSpaceID)
	 */
	@Override
	public Collection<? extends Cell> getCells(TypeDefinition type, SchemaSpaceID schemaSpace) {
		switch (schemaSpace) {
		case SOURCE:
			return Collections.unmodifiableCollection(cellsPerSourceType.get(type));
		case TARGET:
			return Collections.unmodifiableCollection(cellsPerTargetType.get(type));
		default:
			throw new IllegalArgumentException("Illegal schema space provided");
		}
	}

	/**
	 * @see Alignment#getPropertyCells(Iterable, TypeEntityDefinition)
	 */
	@Override
	public Collection<? extends Cell> getPropertyCells(Iterable<TypeEntityDefinition> sourceTypes,
			TypeEntityDefinition targetType) {
		List<Cell> result = new ArrayList<Cell>();

		for (Cell cell : cellsPerTargetType.get(targetType.getDefinition())) {
			// check all cells associated to the target type

			if (!AlignmentUtil.isTypeCell(cell)) {
				// cell is a property cell
				if (sourceTypes == null || AlignmentUtil.isAugmentation(cell)) {
					// cell is an augmentation or we accept any source type
					if (associatedWithType(cell.getTarget(), Collections.singleton(targetType))) {
						// cell is associated to the target entity
						result.add(cell);
					}
				}
				else {
					// cell is a property mapping
					if (associatedWithType(cell.getSource(), sourceTypes)
							&& associatedWithType(cell.getTarget(),
									Collections.singleton(targetType))) {
						// cell is associated to a relation between a source
						// type and the target type
						result.add(cell);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Determines if any of the given entities is associated to at least one of
	 * the given type entity definitions.
	 * 
	 * @param entities the entities
	 * @param types the type entity definitions
	 * @return if there is an entity associated to one of the types
	 */
	private boolean associatedWithType(ListMultimap<String, ? extends Entity> entities,
			Iterable<TypeEntityDefinition> types) {
		// FIXME this will not work correctly when filters for types are
		// implemented!
		for (Entity entity : entities.values()) {
			TypeDefinition entityType = entity.getDefinition().getType();
			for (TypeEntityDefinition type : types) {
				if (entityType.equals(type.getDefinition())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @see Alignment#getCells()
	 */
	@Override
	public Collection<MutableCell> getCells() {
		return Collections.unmodifiableCollection(cells);
	}

	/**
	 * @see MutableAlignment#removeCell(Cell)
	 */
	@Override
	public boolean removeCell(Cell cell) {
		boolean removed = cells.remove(cell);
		if (removed) {
			typeCells.remove(cell);

			// remove from maps
			internalRemoveFromMaps(cell.getSource(), cell);
			internalRemoveFromMaps(cell.getTarget(), cell);
		}

		return removed;
	}

	/**
	 * Removes a cell from the internal indexes, based on the given associated
	 * entities.
	 * 
	 * @param entities the cell entities (usually either source or target)
	 * @param cell the cell to remove
	 */
	private void internalRemoveFromMaps(ListMultimap<String, ? extends Entity> entities, Cell cell) {
		if (entities == null) {
			return;
		}

		for (Entity entity : entities.values()) {
			EntityDefinition entityDef = entity.getDefinition();
			cellsPerEntity.remove(entityDef, cell);

			switch (entityDef.getSchemaSpace()) {
			case TARGET:
				cellsPerTargetType.remove(entityDef.getType(), cell);
				break;
			case SOURCE:
				cellsPerSourceType.remove(entityDef.getType(), cell);
				break;
			default:
				throw new IllegalStateException(
						"Entity definition with illegal schema space encountered");
			}
		}
	}

	/**
	 * @see Alignment#getTypeCells()
	 */
	@Override
	public Collection<MutableCell> getTypeCells() {
		return Collections.unmodifiableCollection(typeCells);
	}

}
