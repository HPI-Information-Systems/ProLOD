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

package de.hpi.fgis.ldp.server.algorithms.clustering.datastructures;

import java.util.AbstractList;
import java.util.List;

import com.google.inject.internal.Objects;

import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;
import de.hpi.fgis.ldp.server.datastructures.ISchema;

/**
 * This class maps a {@link IEntitySchemaList} to the {@link List} interface for {@link IEntity}
 * instances
 * 
 * @author toni.gruetze
 * 
 */
public class InMemoryEntityList extends AbstractList<IEntity> {

  protected final IEntitySchemaList innerList;

  /**
   * Data container class to provide Access to {@link IEntity} data
   * 
   * @author toni.gruetze
   * 
   */
  private class InMemoryEntity implements IEntity {
    private final int innerIndex;
    private final int outerIndex;

    public InMemoryEntity(final int index) {
      this.innerIndex = index;
      this.outerIndex = index;
    }

    private InMemoryEntity(final int innerIndex, final int outerIndex) {
      this.innerIndex = innerIndex;
      this.outerIndex = outerIndex;
    }

    // FIXME remove in interface?
    @Override
    public IEntity copy(int index) {
      return new InMemoryEntity(this.innerIndex, index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hpi.fgis.ldp.server.datastructures.IEntity#getId()
     */
    @Override
    public int getId() {
      return InMemoryEntityList.this.innerList.getEntityID(this.innerIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hpi.fgis.ldp.server.datastructures.IEntity#getIndex()
     */
    @Override
    public int getIndex() {
      return this.outerIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hpi.fgis.ldp.server.datastructures.IEntity#schema()
     */
    @Override
    public ISchema schema() {
      return new InMemorySchema(this.innerIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
      if (obj != null && obj instanceof IEntity) {
        return this.getId() == ((IEntity) obj).getId();
      }
      return super.equals(obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return Objects.hashCode(this.getId());
    }
  }

  /**
   * Data container class to provide Access to {@link ISchema} information
   * 
   * @author toni.gruetze
   * 
   */
  private class InMemorySchema extends AbstractList<Integer> implements ISchema {
    private final int[] schema;

    public InMemorySchema(final int index) {
      this.schema = InMemoryEntityList.this.innerList.getSchema(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractList#get(int)
     */
    @Override
    public Integer get(int index) {
      return Integer.valueOf(this.schema[index]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
      return this.schema.length;
    }

  }

  public InMemoryEntityList(IEntitySchemaList innerList) {
    this.innerList = innerList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.AbstractList#get(int)
   */
  @Override
  public IEntity get(int index) {
    return new InMemoryEntity(index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.AbstractCollection#size()
   */
  @Override
  public int size() {
    return this.innerList.getEntityCount();
  }

}
