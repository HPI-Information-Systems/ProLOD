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

import java.util.ArrayList;

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.NormalizedPattern;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.rpc.AbstractDatatypeRequest;
import de.hpi.fgis.ldp.shared.rpc.CachableAction;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;

public class PatternStatisticsRequest extends AbstractDatatypeRequest implements
    CachableAction<DataTableResult> {
  private static final long serialVersionUID = 7319983984104047596L;
  private NormalizedPattern normalizedPattern;
  private int fromRow;
  private int toRow;

  protected PatternStatisticsRequest() {
    // hide default constructor
  }

  public PatternStatisticsRequest(final Cluster cluster, final ArrayList<Predicate> predicates,
      final Datatype datatype, int fromRow, int toRow) {
    this(cluster, predicates, datatype, null, fromRow, toRow);
  }

  public PatternStatisticsRequest(final Cluster cluster, final ArrayList<Predicate> predicates,
      final Datatype datatype, final NormalizedPattern normalizedPattern, int fromRow, int toRow) {
    super(cluster, predicates, datatype);
    this.fromRow = fromRow;
    this.toRow = toRow;
    this.normalizedPattern = normalizedPattern;
  }

  public int getFromRow() {
    return fromRow;
  }

  public int getToRow() {
    return toRow;
  }

  public NormalizedPattern getNormalizedPattern() {
    return normalizedPattern;
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
    result = prime * result + ((normalizedPattern == null) ? 0 : normalizedPattern.hashCode());
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
    PatternStatisticsRequest other = (PatternStatisticsRequest) obj;
    if (fromRow != other.fromRow) {
      return false;
    }
    if (toRow != other.toRow) {
      return false;
    }
    if (normalizedPattern == null) {
      if (other.normalizedPattern != null) {
        return false;
      }
    } else if (!normalizedPattern.equals(other.normalizedPattern)) {
      return false;
    }

    return super.equals(obj);
  }
}
