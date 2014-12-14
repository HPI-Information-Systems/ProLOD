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

public class ChangeUserViewRequest implements Action<ChangeUserViewResult>, IsSerializable {
  private static final long serialVersionUID = -680262376316490001L;
  private DataSource parent = null;
  private String targetView = null;
  private boolean copyIfNotExist = true;

  protected ChangeUserViewRequest() {
    // hide default constructor
  }

  public ChangeUserViewRequest(final DataSource parent, final String targetView) {
    this.parent = parent;
    this.targetView = targetView;
  }

  public ChangeUserViewRequest(final DataSource parent, final String targetView,
      boolean copyIfNotExist) {
    this.parent = parent;
    this.targetView = targetView;
    this.copyIfNotExist = copyIfNotExist;
  }

  public DataSource getSchema() {
    return this.parent;
  }

  public String getTargetView() {
    return this.targetView;
  }

  public boolean copyIfNotExist() {
    return this.copyIfNotExist;
  }
}
