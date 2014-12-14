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
import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.Predicate;

public abstract class AbstractDatatypeRequest extends AbstractPredicateRequest {
  private static final long serialVersionUID = -3930177336016727104L;
  private Datatype datatype;

  protected AbstractDatatypeRequest() {
    super();
  }

  public AbstractDatatypeRequest(final Cluster cluster, final ArrayList<Predicate> predicates,
      final Datatype datatype) {
    super(cluster, predicates);
    this.datatype = datatype;
  }

  public Datatype getDatatype() {
    return this.datatype;
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
    result = prime * result + ((datatype == null) ? 0 : datatype.hashCode());
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
    AbstractDatatypeRequest other = (AbstractDatatypeRequest) obj;
    if (datatype == null) {
      if (other.datatype != null) {
        return false;
      }
    } else if (!datatype.equals(other.datatype)) {
      return false;
    }
    return super.equals(obj);
  }
}
