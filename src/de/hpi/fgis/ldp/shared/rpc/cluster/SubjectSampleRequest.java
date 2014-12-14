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
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.rpc.AbstractClusterRequest;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;

public class SubjectSampleRequest extends AbstractClusterRequest implements Action<DataTableResult> {
  private static final long serialVersionUID = -1677362232354722544L;
  private int fromRow;
  private int toRow;

  protected SubjectSampleRequest() {
    // hide default constructor
  }

  public SubjectSampleRequest(final Cluster cluster, int fromRow, int toRow) {
    super(cluster);
    this.fromRow = fromRow;
    this.toRow = toRow;
  }

  public int getFromRow() {
    return fromRow;
  }

  public int getToRow() {
    return toRow;
  }
}
