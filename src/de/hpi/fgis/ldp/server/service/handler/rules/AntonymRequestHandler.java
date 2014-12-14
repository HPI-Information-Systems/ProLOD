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

package de.hpi.fgis.ldp.server.service.handler.rules;

import java.util.ArrayList;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.persistency.loading.IInversePredicateLoader;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.InversePredicateModel;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.rules.AntonymRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.AntonymResult;

public class AntonymRequestHandler implements ActionHandler<AntonymRequest, AntonymResult> {

  @Inject
  private Log logger;
  @Inject
  private IInversePredicateLoader loader;
  @Inject
  private Provider<DebugProgress> debugProcess;

  @Override
  public AntonymResult execute(final AntonymRequest action, final ExecutionContext context)
      throws RPCException {
    Cluster cluster = action.getCluster();
    final double minSupport = 0.00005;

    logger.debug("Managing antonym request for cluster " + cluster.getId() + " to "
        + cluster.getLabel());

    try {
      // TODO inject progress
      ArrayList<InversePredicateModel> result =
          new ArrayList<InversePredicateModel>(this.loader.getInversePredicates(cluster,
              minSupport, debugProcess.get()));

      return new AntonymResult(result);
    } catch (Exception cause) {
      logger.error("Unable to load inverse predicates for cluster " + cluster.getId(), cause);

      throw new RPCException(cause);
    }
  }

  @Override
  public void rollback(final AntonymRequest action, final AntonymResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<AntonymRequest> getActionType() {
    return AntonymRequest.class;
  }

}
