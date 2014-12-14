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
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.profiling.PredicateStatisticsRequest;

public class PredicateStatisticRequestHandler implements
    ActionHandler<PredicateStatisticsRequest, DataTableResult> {

  @Inject
  private Log logger;
  @Inject
  private Provider<IProfilingLoader> loaderProvider;
  @Inject
  private Provider<DebugProgress> debugProcess;

  @Override
  public DataTableResult execute(final PredicateStatisticsRequest action,
      final ExecutionContext context) throws RPCException {
    final Cluster cluster = action.getCluster();
    logger.debug("managing property statistic request for cluster " + cluster.getId());
    final int start = action.getFromRow();
    final int end = action.getToRow();

    try {
      final IProfilingLoader loader = this.loaderProvider.get();
      loader.setConstraints(cluster);
      final IDataTable propertyStats = loader.getClusterProperties(start, end, debugProcess.get());
      // DataColumn<String> col1 = new DataColumn<String>("name", true);
      // DataColumn<Integer> col2 = new DataColumn<Integer>("value",
      // true);
      // col1.setElement(0, "InternalLinks");
      // col1.setElement(1, "ExternalLinks");
      // col1.setElement(2, "Literals");
      // col2.setElement(0, 20);
      // col2.setElement(1, 30);
      // col2.setElement(2, 10);
      // final DataTable propertyStats = new DataTable(col1);
      // propertyStats.addColumn(col2);

      return new DataTableResult(propertyStats);
    } catch (Exception cause) {
      logger.error("Unable to get property statistics for cluster " + cluster.getId(), cause);

      throw new RPCException(cause);
    }
  }

  @Override
  public Class<PredicateStatisticsRequest> getActionType() {
    return PredicateStatisticsRequest.class;
  }

  @Override
  public void rollback(PredicateStatisticsRequest arg0, DataTableResult arg1, ExecutionContext arg2)
      throws RPCException {
    // Nothing to do here actually
  }

}
