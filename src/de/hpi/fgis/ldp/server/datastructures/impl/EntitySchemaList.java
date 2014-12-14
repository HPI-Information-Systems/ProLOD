/*-
 * Copyright 2012 by: Hasso Plattner Institute for Software Systems Engineering 
 * Prof.-Dr.-Helmert-Str. 2-3
 * 14482 Potsdam, Germany
 * Potsdam District Court, HRB 12184
 * Authorized Representative Managing Director: Prof. Dr. Christoph Meinel
 * http://www.hpi-web.de/
 * 
 * Information Systems Group, http://www.hpi-web.de/naumann/
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.server.datastructures.impl;

import java.util.Arrays;

import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;
import de.hpi.fgis.ldp.server.datastructures.array.IntArray2D;
import de.hpi.fgis.ldp.server.util.ArrayTools;

/**
 * Implementation of {@link IEntitySchemaList} which stores the data of all entities
 * 
 * @author toni.gruetze
 */
public class EntitySchemaList implements IEntitySchemaList {
  private static final long serialVersionUID = 1L;

  /**
   * Factory for {@link EntitySchemaList} instances.
   * 
   * @author toni.gruetze
   */
  public static class Factory {
    private static Factory INSTANCE = new Factory();

    /**
     * Get the singleton instance.
     * 
     * @return the singleton instance
     */
    public static Factory getInstance() {
      return Factory.INSTANCE;
    }

    /**
     * hide default constructor -> singleton instance
     */
    private Factory() {
      // nothing do do
    }

    /**
     * Create a new {@link EntitySchemaList} instance.
     * 
     * @param subjectIDs the subjects to be added to this instance (<b>!must!</b> have the same size
     *        as <code>predicateIDs</code>. the <code>subjectIDs[i]</code> is the subject of the
     *        tuple with <code>predicateIDs[i]</code>)
     * @param predicateIDs the predicates to be added to this instance (<b>!must!</b> have the same
     *        size as <code>subjectIDs</code>. the <code>subjectIDs[i]</code> is the subject of the
     *        tuple with <code>predicateIDs[i]</code>)
     * @return a new {@link EntitySchemaList} instance.
     */
    public EntitySchemaList newInstance(final int[] subjectIDs, final int[] predicateIDs) {
      if (subjectIDs.length != predicateIDs.length) {
        throw new IllegalArgumentException(
            "Unable to create EntitySchemaList for undefined subject-predicate relation");
      }

      final int entityColumn = 0;
      final int predicateIndexColumn = 1;

      final IntArray2D entityItems = new IntArray2D(subjectIDs.length, 2, -1);

      for (int i = 0; i < subjectIDs.length; i++) {
        entityItems.setValue(i, entityColumn, subjectIDs[i]);
        entityItems.setValue(i, predicateIndexColumn, predicateIDs[i]);
      }
      // sort by SubjectID
      entityItems.sortDim1(entityColumn);

      int entityCount = 0;
      int lastEntity = Integer.MIN_VALUE;
      for (int i = 0; i < entityItems.sizeOfDim1(); i++) {
        final int currentEntity = entityItems.getValue(i, entityColumn);
        if (lastEntity != currentEntity) {
          entityCount++;
          lastEntity = currentEntity;
        }
      }

      final IntArray2D itemMapper = new IntArray2D(entityCount, 2, -1);
      final int[] groupedPredicates = new int[predicateIDs.length];

      // fill the itemmapper which contains a map between subjectID and a
      // index to the predicate List
      lastEntity = Integer.MIN_VALUE;
      for (int i = 0, entityIndex = 0; i < entityItems.sizeOfDim1(); i++) {
        final int currentEntity = entityItems.getValue(i, entityColumn);
        if (lastEntity != currentEntity) {
          itemMapper.setValue(entityIndex, entityColumn, currentEntity);
          itemMapper.setValue(entityIndex, predicateIndexColumn, i);
          entityIndex++;
          lastEntity = currentEntity;
        }
        groupedPredicates[i] = entityItems.getValue(i, predicateIndexColumn);
      }

      return this.newInstance(itemMapper, groupedPredicates, entityColumn, predicateIndexColumn);
    }

    /**
     * Create a new {@link EntitySchemaList} instance.
     * 
     * @param itemMapper the item mapper with informations about the entityID's and the start index
     *        of their corresponding predicates in the <code>groupedPredicates</code>-parameter
     * @param groupedPredicates the list of predicates of all entities (grouped by entities)
     * @param entityColumn the column of the entity ids in the <code>itemMapper</code>.
     * @param predicateIndexColumn the column of the start index of the predicates in the
     *        <code>itemMapper</code>.
     * @return a new {@link EntitySchemaList} instance.
     */
    public EntitySchemaList newInstance(final IntArray2D itemMapper, int[] groupedPredicates,
        final int entityColumn, final int predicateIndexColumn) {

      final int maxEntityIndex = itemMapper.sizeOfDim1() - 2;
      // sort predicates foreach entity
      for (int entityIndex = 0; entityIndex <= maxEntityIndex; entityIndex++) {
        Arrays.sort(groupedPredicates, itemMapper.getValue(entityIndex, predicateIndexColumn),
            itemMapper.getValue(entityIndex + 1, predicateIndexColumn));
      }
      // sort predicates of last entity
      Arrays.sort(groupedPredicates, itemMapper.getValue(maxEntityIndex + 1, predicateIndexColumn),
          groupedPredicates.length);

      return new EntitySchemaList(itemMapper, groupedPredicates, entityColumn, predicateIndexColumn);
    }
  }

  private final int[] predicates;

  private final int entityColumn;
  private final int mappingColumn;

  /**
   * stores the first index of a item in the entity in the <code>predicates</code>-list
   */
  private final IntArray2D itemMapper;

  /**
   * Create a new {@link EntitySchemaList} instance.
   * 
   * @param itemMapper the item mapper with informations about the entityID's and the start index of
   *        their corresponding predicates in the <code>predicates</code>-parameter
   * @param predicates the list of predicates of all entities (groupeed by entities)
   * @param entityIndex the column of the entityids in the <code>itemMapper</code>.
   * @param mappingIndex the column of the start index of the predicates in the
   *        <code>itemMapper</code>.
   */
  protected EntitySchemaList(final IntArray2D itemMapper, int[] predicates, final int entityIndex,
      final int mappingIndex) {
    this.itemMapper = itemMapper;
    this.predicates = predicates;
    this.entityColumn = entityIndex;
    this.mappingColumn = mappingIndex;
  }

  /**
   * get the first index of the predicates of the given entity
   * 
   * @param entityIndex the index of the entity
   * @return get the first index of the predicates
   */
  private final int firstIndexOfItems(final int entityIndex) {
    return this.itemMapper.getValue(entityIndex, this.mappingColumn);
  }

  /**
   * get the first index of the predicates of the entity afterwards the given entity
   * 
   * @param entityIndex the index of the entity
   * @return get the first index of the predicates of the next entity
   */
  private final int firstIndexOfNextItems(final int entityIndex) {
    if (entityIndex < this.itemMapper.sizeOfDim1() - 1) {
      return this.itemMapper.getValue(entityIndex + 1, this.mappingColumn);
    }

    return this.predicates.length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.dto.IEntitySchemaList#containsAll (int, int[])
   */
  @Override
  public boolean containsAll(final int entityIndex, final int[] subset) {

    final int firstIndexOfItems = this.firstIndexOfItems(entityIndex);

    int lastIndexOfItems = this.firstIndexOfNextItems(entityIndex) - 1;

    // first or last element of subset is not in superset
    if (subset[0] < this.predicates[firstIndexOfItems]
        || subset[subset.length - 1] > this.predicates[lastIndexOfItems]) {
      return false;
    }

    return ArrayTools.isSubSet(this.predicates, firstIndexOfItems, lastIndexOfItems + 1, subset);
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.persistency.loading.dto.IEntitySchemaList# getEntityCount()
   */
  @Override
  public int getEntityCount() {
    return this.itemMapper.sizeOfDim1();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.dto.IEntitySchemaList#getEntityID (int)
   */
  @Override
  public int getEntityID(int entityIndex) {
    return this.itemMapper.getValue(entityIndex, this.entityColumn);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.dto.IEntitySchemaList#getSchema (int)
   */
  @Override
  public int[] getSchema(int entityIndex) {
    final int firstIndexOfItems = this.firstIndexOfItems(entityIndex);

    int firstIndexOfNextItems = this.firstIndexOfNextItems(entityIndex);

    int[] resultItems = new int[firstIndexOfNextItems - firstIndexOfItems];

    System.arraycopy(this.predicates, firstIndexOfItems, resultItems, 0, resultItems.length);

    return resultItems;
  }
}
