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

import java.util.List;

/**
 * A schema of an entity is a list of attribute ids having the following properties: - It is
 * unmodifiable. - Random access in constant time. - Memory efficiency: array-backed. - (Attribute
 * ids are sorted ascending).
 * 
 * @author daniel.hefenbrock
 * 
 */
public interface ISchema extends List<Integer> {
  // nothing to implement
}
