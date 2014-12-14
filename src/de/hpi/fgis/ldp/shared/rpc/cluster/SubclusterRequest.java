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

package de.hpi.fgis.ldp.shared.rpc.cluster;

import net.customware.gwt.dispatch.shared.Action;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig;
import de.hpi.fgis.ldp.shared.data.Cluster;

public class SubclusterRequest implements Action<SubclusterResult>, IsSerializable {
  private static final long serialVersionUID = 78536482763195406L;
  private Cluster parent = null;
  private IClusterConfig config;

  protected SubclusterRequest() {
    // hide default constructor
  }

  public SubclusterRequest(final Cluster parent, final IClusterConfig config) {
    this.parent = parent;
    this.config = config;
  }

  public Cluster getParent() {
    return this.parent;
  }

  public IClusterConfig getConfig() {
    return this.config;
  }
}
