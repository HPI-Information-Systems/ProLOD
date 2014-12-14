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
import net.customware.gwt.dispatch.shared.ActionException;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.cluster.SubjectDetailRequest;

public class SubjectDetailsRequestHandler implements
    ActionHandler<SubjectDetailRequest, DataTableResult> {

  @Inject
  private Log logger;
  @Inject
  private IClusterLoader loader;
  @Inject
  private Provider<DebugProgress> debugProcess;

  @Override
  public DataTableResult execute(final SubjectDetailRequest action, final ExecutionContext context)
      throws ActionException {
    final Subject subject = action.getSubject();
    final Cluster cluster = action.getCluster();
    logger.debug("managing cluster subjects request for subject " + subject.getId());

    try {
      final IDataTable triples = loader.getSubjectTriples(cluster, subject, debugProcess.get());

      return new DataTableResult(triples);
    } catch (Exception cause) {
      logger.error("Unable to get cluster triples for subject " + subject.getId(), cause);

      throw new ActionException(cause);
    }
  }

  @Override
  public Class<SubjectDetailRequest> getActionType() {
    return SubjectDetailRequest.class;
  }

  @Override
  public void rollback(SubjectDetailRequest arg0, DataTableResult arg1, ExecutionContext arg2)
      throws ActionException {
    // Nothing to do here actually
  }

}
