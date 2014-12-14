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

package de.hpi.fgis.ldp.shared.data.table.impl.dummy;

import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.table.impl.DataColumn;

/**
 * this is a dummy class to enable the serialization of {@link DataColumn} instances for
 * {@link Datatype} elements (e.g. for client - server communication)
 * 
 * @author toni.gruetze
 * 
 */
public class DatatypeColumn extends DataColumn<Datatype> {
  private static final long serialVersionUID = 6277182202665472693L;

  protected DatatypeColumn() {
    // hide default constructor
  }
}
