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

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.hpi.fgis.ldp.shared.data.Cluster;

public class ChangeUserViewResult implements Result, IsSerializable {
  private static final long serialVersionUID = 2702741030190360213L;
  private Cluster root = null;
  private ArrayList<Cluster> topLevelClusters = null;

  protected ChangeUserViewResult() {
    // hide default constructor
  }

  public ChangeUserViewResult(final Cluster root, final ArrayList<Cluster> topLevelClusters) {
    this.root = root;
    this.topLevelClusters = topLevelClusters;
  }

  public Cluster getRootCluster() {
    return this.root;
  }

  public ArrayList<Cluster> getTopLevelClusters() {
    return this.topLevelClusters;
  }
}
