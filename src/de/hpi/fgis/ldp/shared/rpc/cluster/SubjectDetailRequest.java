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
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.rpc.AbstractClusterRequest;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;

public class SubjectDetailRequest extends AbstractClusterRequest implements Action<DataTableResult> {
  private static final long serialVersionUID = -6358187669012439297L;
  private Subject subject;

  protected SubjectDetailRequest() {
    // hide default constructor
  }

  public SubjectDetailRequest(final Cluster cluster, final Subject subject) {
    super(cluster);
    this.subject = subject;
  }

  public Subject getSubject() {
    return subject;
  }
}
