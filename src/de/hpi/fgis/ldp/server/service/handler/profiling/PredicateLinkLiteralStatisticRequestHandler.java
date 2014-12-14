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

package de.hpi.fgis.ldp.server.service.handler.profiling;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.table.impl.DataColumn;
import de.hpi.fgis.ldp.shared.data.table.impl.DataTable;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.profiling.PredicateLinkLiteralStatisticsRequest;

public class PredicateLinkLiteralStatisticRequestHandler implements
    ActionHandler<PredicateLinkLiteralStatisticsRequest, DataTableResult> {
  @Inject
  private Log logger;

  // @Inject
  // private Provider<IProfilingLoader> loaderProvider;

  @Override
  public DataTableResult execute(PredicateLinkLiteralStatisticsRequest action,
      ExecutionContext context) throws RPCException {
    final Cluster cluster = action.getCluster();
    logger.debug("managing link literal statistic request for cluster " + cluster.getId());
    logger.error("PredicateLinkLiteralStatisticRequestHandler not yet implemented");

    try {
      // FIXME implement loading algorithm
      return new DataTableResult(new DataTable(new DataColumn<String>("datetype", false)));
    } catch (Exception cause) {
      logger.error("Unable to get link literal statistics for cluster " + cluster.getId(), cause);

      throw new RPCException(cause);
    }

  }

  @Override
  public Class<PredicateLinkLiteralStatisticsRequest> getActionType() {
    return PredicateLinkLiteralStatisticsRequest.class;
  }

  @Override
  public void rollback(PredicateLinkLiteralStatisticsRequest arg0, DataTableResult arg1,
      ExecutionContext arg2) throws RPCException {
    // Nothing to do here actually
  }

}
