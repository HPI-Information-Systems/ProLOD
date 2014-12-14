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

/**
 * The representation of an entity used for schema-based clustering.
 * 
 * @author daniel.hefenbrock
 * 
 */
public interface IEntity {

  /**
   * Id used in the database.
   */
  public int getId();

  /**
   * Local id (index in entity list).
   */
  public int getIndex();

  /**
   * The schema of this entity.
   */
  public ISchema schema();

  /**
   * Create a copy having a special index. (We need this to extract sub-lists from entity lists in
   * which each entity needs a different index than its index in the original list)
   */
  public IEntity copy(int index);
}
