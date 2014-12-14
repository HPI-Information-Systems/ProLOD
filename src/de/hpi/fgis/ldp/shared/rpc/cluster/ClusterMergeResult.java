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

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.hpi.fgis.ldp.shared.data.Cluster;

public class ClusterMergeResult implements Result, IsSerializable {
  private static final long serialVersionUID = 6774831024807075680L;
  private Cluster parent = null;
  private ArrayList<Cluster> children = null;

  protected ClusterMergeResult() {
    // hide default constructor
  }

  public ClusterMergeResult(final Cluster parent, ArrayList<Cluster> children) {
    this.parent = parent;
    this.children = children;
  }

  public Cluster getParent() {
    return this.parent;
  }

  public ArrayList<Cluster> getChildren() {
    return this.children;
  }
}
