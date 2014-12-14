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

package de.hpi.fgis.ldp.server.service.handler.progress;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.util.job.JobManager;
import de.hpi.fgis.ldp.server.util.progress.IPersistentProgress;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.progress.ProgressRequest;
import de.hpi.fgis.ldp.shared.rpc.progress.ProgressResult;

public class ProgressHandler<ResultType> implements
    ActionHandler<ProgressRequest<ResultType>, ProgressResult<ResultType>> {

  @Inject
  private JobManager manager;

  @Override
  public ProgressResult<ResultType> execute(final ProgressRequest<ResultType> action,
      final ExecutionContext context) throws RPCException {
    long identifier = action.getProgressIdentifier();

    final ProgressResult<ResultType> result = this.read(manager.getProgressInstance(identifier));

    result.setProgressIdentifier(identifier);

    return result;
  }

  private ProgressResult<ResultType> read(IPersistentProgress<?> progress) {
    if (progress == null) {
      return null;
    }
    ProgressResult<ResultType> result = new ProgressResult<ResultType>();

    result.setProgress(progress.getProgress());
    result.setMessage(progress.getMessage());
    result.setException(progress.getException());
    result.setFinished(progress.isFinished());
    // TODO if(progress.getResult() instanceof ResultType)
    result.setResult(this.uncheckedCast(progress.getResult()));
    result.setDetailMessage(progress.getDetailMessage());
    result.setChild(this.read(progress.getSubProgress()));

    return result;
  }

  @SuppressWarnings("unchecked")
  private ResultType uncheckedCast(Object instance) {
    return (ResultType) instance;
  }

  @Override
  public void rollback(final ProgressRequest<ResultType> action,
      final ProgressResult<ResultType> result, final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Class<ProgressRequest<ResultType>> getActionType() {
    return (Class) ProgressRequest.class;
  }

  // protected abstract Class<? extends ProgressRequest<ResultType>>
  // getActionTypeImpl();
  //
  // // public Class<Act> getActionType() {
  // // return ProgressRequest<?>.class;
  // // }
  //
  // /**
  // * this class represents a progress request for result type
  // * {@link de.hpi.fgis.ldp.shared.data.Cluster}
  // *
  // * @author toni.gruetze
  // *
  // */
  // public final static class ClusterProgressHandler extends
  // ProgressHandler<de.hpi.fgis.ldp.shared.data.Cluster> {
  // @Override
  // protected Class<? extends
  // ProgressRequest<de.hpi.fgis.ldp.shared.data.Cluster>> getActionTypeImpl()
  // {
  // return ProgressRequest.Cluster.class;
  // }
  // }
  // /**
  // * this class represents a progress request for result type
  // * {@link de.hpi.fgis.ldp.shared.data.AssociationRuleModel}
  // *
  // * @author toni.gruetze
  // *
  // */
  // public final static class AssociationRuleProgressHandler extends
  // ProgressHandler<de.hpi.fgis.ldp.shared.data.ARSetModel> {
  // @Override
  // protected Class<? extends
  // ProgressRequest<de.hpi.fgis.ldp.shared.data.ARSetModel>>
  // getActionTypeImpl() {
  // return ProgressRequest.AssociationRule.class;
  // }
  // }
}
