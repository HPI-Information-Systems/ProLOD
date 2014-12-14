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
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.algorithms.factgeneration.SuggestionJob;
import de.hpi.fgis.ldp.server.util.job.JobManager;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.rpc.rules.SuggestionViewRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.SuggestionViewResult;

public class SuggestionViewRequestHandler implements
    ActionHandler<SuggestionViewRequest, SuggestionViewResult> {

  @Inject
  private Provider<SuggestionJob> jobSource;
  @Inject
  private JobManager manager;

  @Override
  public SuggestionViewResult execute(final SuggestionViewRequest action,
      final ExecutionContext context) throws ActionException {
    Cluster cluster = action.getCluster();

    SuggestionJob clusterJob = this.jobSource.get();
    clusterJob.init(cluster);

    final long id = this.manager.executeJob(clusterJob);

    return new SuggestionViewResult(id);
  }

  @Override
  public Class<SuggestionViewRequest> getActionType() {
    return SuggestionViewRequest.class;
  }

  @Override
  public void rollback(SuggestionViewRequest arg0, SuggestionViewResult arg1, ExecutionContext arg2)
      throws ActionException {
    // Nothing to do here actually
  }

}
