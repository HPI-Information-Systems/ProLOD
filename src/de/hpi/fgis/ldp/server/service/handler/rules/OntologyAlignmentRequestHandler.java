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

import de.hpi.fgis.ldp.server.algorithms.ontologyAligment.OntologyAlignmentJob;
import de.hpi.fgis.ldp.server.util.job.JobManager;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.rules.OntologyAlignmentRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.OntologyAlignmentResult;

public class OntologyAlignmentRequestHandler implements
    ActionHandler<OntologyAlignmentRequest, OntologyAlignmentResult> {

  @Inject
  private Provider<OntologyAlignmentJob> jobSource;
  @Inject
  private JobManager manager;

  @Override
  public OntologyAlignmentResult execute(final OntologyAlignmentRequest action,
      final ExecutionContext context) throws RPCException {
    Cluster cluster = action.getCluster();

    OntologyAlignmentJob clusterJob = this.jobSource.get();
    clusterJob.init(cluster);

    final long id = this.manager.executeJob(clusterJob);

    return new OntologyAlignmentResult(id);

  }

  @Override
  public void rollback(final OntologyAlignmentRequest action, final OntologyAlignmentResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<OntologyAlignmentRequest> getActionType() {
    return OntologyAlignmentRequest.class;
  }

}
