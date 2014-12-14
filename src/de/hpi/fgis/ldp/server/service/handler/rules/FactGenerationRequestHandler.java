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

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.algorithms.factgeneration.FactGenerationJob;
import de.hpi.fgis.ldp.server.util.job.JobManager;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.rules.FactGenerationRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.FactGenerationResult;

public class FactGenerationRequestHandler implements
    ActionHandler<FactGenerationRequest, FactGenerationResult> {

  @Inject
  private Provider<FactGenerationJob> jobSource;
  @Inject
  private JobManager manager;

  @Override
  public FactGenerationResult execute(final FactGenerationRequest action,
      final ExecutionContext context) throws RPCException {
    Cluster cluster = action.getCluster();

    FactGenerationJob factGenerationJob = this.jobSource.get();
    factGenerationJob.init(cluster);

    final long id = this.manager.executeJob(factGenerationJob);

    return new FactGenerationResult(id);

  }

  @Override
  public void rollback(final FactGenerationRequest action, final FactGenerationResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<FactGenerationRequest> getActionType() {
    return FactGenerationRequest.class;
  }

}
