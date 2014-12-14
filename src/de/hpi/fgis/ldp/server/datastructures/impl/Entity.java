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

import com.google.inject.internal.Objects;

import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.ISchema;

public class Entity implements IEntity {
  private final int id;
  private final int index;
  protected int[] attributes;

  public Entity(int id, int index, int[] attr) {
    this.id = id;
    this.index = index;
    this.attributes = attr;
  }

  @Override
  public ISchema schema() {
    return new AbstractSchema() {

      @Override
      public Integer get(int index) {
        return Integer.valueOf(Entity.this.attributes[index]);
      }

      @Override
      public int size() {
        return Entity.this.attributes.length;
      }

    };
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public int getIndex() {
    return this.index;
  }

  @Override
  public IEntity copy(int index) {
    return new Entity(this.id, index, this.attributes);
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
