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

import de.hpi.fgis.ldp.server.persistency.loading.IUniquenessLoader;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.UniquenessModel;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.rules.UniquenessRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.UniquenessResult;

public class UniquenessRequestHandler implements ActionHandler<UniquenessRequest, UniquenessResult> {

  @Inject
  private Log logger;
  @Inject
  private IUniquenessLoader loader;
  @Inject
  private Provider<DebugProgress> debugProcess;

  @Override
  public UniquenessResult execute(final UniquenessRequest action, final ExecutionContext context)
      throws RPCException {
    Cluster cluster = action.getCluster();
    final double minSupport = 0.00005;

    logger.debug("Managing uniqueness request for cluster " + cluster.getId() + " to "
        + cluster.getLabel());

    try {
      ArrayList<UniquenessModel> result =
          new ArrayList<UniquenessModel>(this.loader.getUniqueness(cluster, minSupport,
              debugProcess.get()));

      return new UniquenessResult(result);
    } catch (Exception cause) {
      logger.error("Unable to compile uniqueness for cluster " + cluster.getId(), cause);

      throw new RPCException(cause);
    }
  }

  @Override
  public void rollback(final UniquenessRequest action, final UniquenessResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<UniquenessRequest> getActionType() {
    return UniquenessRequest.class;
  }

}
