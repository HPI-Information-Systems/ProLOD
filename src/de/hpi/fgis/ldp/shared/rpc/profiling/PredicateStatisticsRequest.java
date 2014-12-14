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

package de.hpi.fgis.ldp.shared.rpc.profiling;

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.rpc.AbstractClusterRequest;
import de.hpi.fgis.ldp.shared.rpc.CachableAction;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;

public class PredicateStatisticsRequest extends AbstractClusterRequest implements
    CachableAction<DataTableResult> {
  private static final long serialVersionUID = 1717776736560672699L;
  private int fromRow;
  private int toRow;

  protected PredicateStatisticsRequest() {
    // hide default constructor
  }

  public PredicateStatisticsRequest(final Cluster cluster, int fromRow, int toRow) {
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + fromRow;
    result = prime * result + toRow;
    result = prime * result + super.hashCode();
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PredicateStatisticsRequest other = (PredicateStatisticsRequest) obj;
    if (fromRow != other.fromRow) {
      return false;
    }
    if (toRow != other.toRow) {
      return false;
    }

    return super.equals(obj);
  }
}
