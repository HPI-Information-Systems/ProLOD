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

import java.util.ArrayList;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.Pattern;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.profiling.ObjectStatisticsRequest;

public class ObjectStatisticsRequestHandler implements
    ActionHandler<ObjectStatisticsRequest, DataTableResult> {

  @Inject
  private Log logger;
  @Inject
  private Provider<IProfilingLoader> loaderProvider;
  @Inject
  private Provider<DebugProgress> debugProcess;

  @Override
  public DataTableResult execute(final ObjectStatisticsRequest action,
      final ExecutionContext context) throws RPCException {
    final Cluster cluster = action.getCluster();
    logger.debug("managing object statistis request for cluster " + cluster.getId());
    final ArrayList<Predicate> predicates = action.getPredicates();
    final Datatype datatype = action.getDatatype();
    final Pattern pattern = action.getPattern();
    final int from = action.getFromRow();
    final int to = action.getToRow();

    try {
      final IProfilingLoader loader = this.loaderProvider.get();
      loader.setConstraints(cluster);
      final IDataTable dataTypeStats =
          loader.getObjects(predicates, datatype, pattern, from, to, debugProcess.get());

      return new DataTableResult(dataTypeStats);
    } catch (Exception cause) {
      logger.error("Unable to get property statistics for cluster " + cluster.getId(), cause);

      throw new RPCException(cause);
    }
  }

  @Override
  public Class<ObjectStatisticsRequest> getActionType() {
    return ObjectStatisticsRequest.class;
  }

  @Override
  public void rollback(ObjectStatisticsRequest arg0, DataTableResult arg1, ExecutionContext arg2)
      throws RPCException {
    // Nothing to do here actually
  }

}
