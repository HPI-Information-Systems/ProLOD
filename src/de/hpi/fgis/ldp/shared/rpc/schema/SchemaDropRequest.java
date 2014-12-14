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

package de.hpi.fgis.ldp.shared.rpc.schema;

import net.customware.gwt.dispatch.shared.Action;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.hpi.fgis.ldp.shared.data.DataSource;

public class SchemaDropRequest implements Action<SchemaDropResult>, IsSerializable {
  private static final long serialVersionUID = 3736405966323056330L;
  private DataSource parent = null;

  protected SchemaDropRequest() {
    // hide default constructor
  }

  public SchemaDropRequest(final DataSource parent) {
    this.parent = parent;
  }

  public DataSource getSchema() {
    return this.parent;
  }
}
