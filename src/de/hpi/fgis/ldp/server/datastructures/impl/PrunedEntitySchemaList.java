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

import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;

/**
 * a {@link IEntitySchemaList} implementation which base upon another {@link IEntitySchemaList}
 * instance
 * 
 */
public class PrunedEntitySchemaList implements IEntitySchemaList {
  private static final long serialVersionUID = 6986073734792351332L;

  private final IEntitySchemaList internalList;
  private final int[] mapping;

  /**
   * create a new {@link PrunedEntitySchemaList}.
   * 
   * @param internalList the {@link IEntitySchemaList} on which this instance is based on
   * @param newEntityIndices the indices of the item sets of the new instance within the
   *        <code>internalList</code> to
   */
  public PrunedEntitySchemaList(final IEntitySchemaList internalList, final int[] newEntityIndices) {
    this.internalList = internalList;
    this.mapping = newEntityIndices;
    final int oldSize = internalList.getEntityCount();
    final int newSize = newEntityIndices.length;

    for (int entityIndex = 0; entityIndex < newSize; entityIndex++) {
      if (0 > newEntityIndices[entityIndex] || newEntityIndices[entityIndex] >= oldSize) {
        throw (new IllegalArgumentException("Unexpected entityIndex "
            + newEntityIndices[entityIndex] + "!"));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.dto.IEntitySchemaList#containsAll (int, int[])
   */
  @Override
  public boolean containsAll(final int basketIndex, final int[] items) {
    return this.internalList.containsAll(this.mapping[basketIndex], items);
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.persistency.loading.dto.IEntitySchemaList# getEntityCount()
   */
  @Override
  public int getEntityCount() {
    return this.mapping.length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.dto.IEntitySchemaList#getEntityID (int)
   */
  @Override
  public int getEntityID(int entityIndex) {
    return this.internalList.getEntityID(this.mapping[entityIndex]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.dto.IEntitySchemaList#getSchema (int)
   */
  @Override
  public int[] getSchema(int entityIndex) {
    return this.internalList.getSchema(this.mapping[entityIndex]);
  }
}
