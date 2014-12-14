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

package de.hpi.fgis.ldp.server.datastructures;

import java.io.Serializable;

/**
 * Represents a list of entities and their schema
 * 
 * @author toni.gruetze
 * 
 */
public interface IEntitySchemaList extends Serializable {
  /**
   * Get the amount of entities in this List.
   * 
   * @return the amount of entities
   */
  public int getEntityCount();

  /**
   * Get the id of the entity
   * 
   * @param entityIndex the index of the entity
   * @return the id of the entity.
   */
  public int getEntityID(final int entityIndex);

  /**
   * sortet list of predicates in one entity (identified by <code>entityIndex</code>), which should
   * be distinct.<br/>
   * <br/>
   * 
   * @param entityIndex Index of entity to get items for.
   * @return sorted list of predicates (should be distinct).
   */
  // TODO remove or set deprecated?
  public int[] getSchema(final int entityIndex);

  // TODO instead:
  // public Set<Integer> or List<Integer> getSchema(final int entityIndex);

  /**
   * checks if specified entity contains all predicates of a given array
   * 
   * @param entityIndex the index of the entity to check
   * @param items the items to be potentially contained in the entity
   * @return <code>true</code>, if all elements of <code>items</code> are part of the subset of the
   *         entity with <code>entityIndex</code>, otherwise <code>false</code>.
   */
  public boolean containsAll(final int entityIndex, final int[] items);
}
