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

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ImportProcessResult implements Result, IsSerializable {
  private static final long serialVersionUID = -3866631343189347596L;

  private ImportProcessRequest request = null;

  protected ImportProcessResult() {
    // hide default constructor
  }

  public ImportProcessResult(final ImportProcessRequest request) {

    this.request = request;
  }

  public ImportProcessRequest getRequest() {
    return this.request;
  }
}
