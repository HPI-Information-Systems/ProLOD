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

package de.hpi.fgis.ldp.server.service.handler.cluster;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.algorithms.clustering.ClusterJob;
import de.hpi.fgis.ldp.server.util.job.JobManager;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.cluster.SubclusterRequest;
import de.hpi.fgis.ldp.shared.rpc.cluster.SubclusterResult;

public class SubclusterHandler implements ActionHandler<SubclusterRequest, SubclusterResult> {

  @Inject
  private Provider<ClusterJob> jobSource;
  @Inject
  private JobManager manager;

  @Override
  public SubclusterResult execute(final SubclusterRequest action, final ExecutionContext context)
      throws RPCException {

    Cluster parent = action.getParent();
    IClusterConfig config = action.getConfig();

    ClusterJob clusterJob = this.jobSource.get();
    clusterJob.init(parent, config);

    final long id = this.manager.executeJob(clusterJob);

    parent.setProgressIdentifier(Long.valueOf(id));

    return new SubclusterResult(parent);
  }

  @Override
  public void rollback(final SubclusterRequest action, final SubclusterResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<SubclusterRequest> getActionType() {
    return SubclusterRequest.class;
  }
}
