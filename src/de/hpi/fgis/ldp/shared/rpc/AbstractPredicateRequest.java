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

package de.hpi.fgis.ldp.shared.rpc;

import java.util.ArrayList;

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Predicate;

public abstract class AbstractPredicateRequest extends AbstractClusterRequest {
  private static final long serialVersionUID = -6103173418192316856L;
  private ArrayList<Predicate> predicates;

  protected AbstractPredicateRequest() {
    super();
  }

  public AbstractPredicateRequest(final Cluster cluster, final ArrayList<Predicate> predicates) {
    super(cluster);
    this.predicates = predicates;
  }

  public ArrayList<Predicate> getPredicates() {
    return this.predicates;
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
    result = prime * result + ((predicates == null) ? 0 : predicates.hashCode());
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
    AbstractPredicateRequest other = (AbstractPredicateRequest) obj;
    if (predicates == null) {
      if (other.predicates != null) {
        return false;
      }
    } else if (!predicates.equals(other.predicates)) {
      return false;
    }
    return super.equals(obj);
  }
}
