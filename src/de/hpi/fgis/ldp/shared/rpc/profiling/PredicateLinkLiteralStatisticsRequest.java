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
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.rpc.AbstractPredicateRequest;
import de.hpi.fgis.ldp.shared.rpc.CachableAction;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;

public class PredicateLinkLiteralStatisticsRequest extends AbstractPredicateRequest implements
    CachableAction<DataTableResult> {
  private static final long serialVersionUID = -1875400316236012832L;

  protected PredicateLinkLiteralStatisticsRequest() {
    // hide default constructor
  }

  public PredicateLinkLiteralStatisticsRequest(final Cluster cluster,
      final ArrayList<Predicate> predicates) {
    super(cluster, predicates);
  }
}
