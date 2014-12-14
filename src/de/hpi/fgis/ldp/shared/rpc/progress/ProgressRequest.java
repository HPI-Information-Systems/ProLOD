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

package de.hpi.fgis.ldp.shared.rpc.progress;

import net.customware.gwt.dispatch.shared.Action;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * a request for a process update
 * 
 * @author toni.gruetze
 * 
 * @param <ResultType> expected result type
 */
public class ProgressRequest<ResultType> implements Action<ProgressResult<ResultType>>,
    IsSerializable {
  private static final long serialVersionUID = -4149968292159433156L;
  private long progressIdentifier;

  protected ProgressRequest() {
    // hide default constructor
  }

  /**
   * creates a request
   * 
   * @param progressIdentifier the identifier
   */
  public ProgressRequest(long progressIdentifier) {
    super();
    this.progressIdentifier = progressIdentifier;
  }

  /**
   * gets the progress identifier
   * 
   * @return the progress identifier
   */
  public long getProgressIdentifier() {
    return progressIdentifier;
  }

  // /**
  // * this class represents a progress request for result type
  // * {@link de.hpi.fgis.ldp.shared.data.Cluster}
  // *
  // * @author toni.gruetze
  // *
  // */
  // public static class Cluster extends
  // ProgressRequest<de.hpi.fgis.ldp.shared.data.Cluster> {
  // private static final long serialVersionUID = 6157797725965086515L;
  // protected Cluster() {
  // // hide default constructor
  // }
  // /**
  // * creates a request
  // *
  // * @param progressIdentifier
  // * the identifier
  // */
  // public Cluster(long progressIdentifier) {
  // super(progressIdentifier);
  // }
  // }
  //
  // /**
  // * this class represents a progress request for result type
  // * {@link de.hpi.fgis.ldp.shared.data.AssociationRuleModel}
  // *
  // * @author toni.gruetze
  // *
  // */
  // public static class AssociationRule extends
  // ProgressRequest<de.hpi.fgis.ldp.shared.data.ARSetModel> {
  // private static final long serialVersionUID = 6545320288484349799L;
  // protected AssociationRule() {
  // // hide default constructor
  // }
  // /**
  // * creates a request
  // *
  // * @param progressIdentifier
  // * the identifier
  // */
  // public AssociationRule(long progressIdentifier) {
  // super(progressIdentifier);
  // }
  // }
  //
  // /**
  // * this class represents a progress request for result type
  // * {@link de.hpi.fgis.ldp.shared.data.AssociationRuleModel}
  // *
  // * @author toni.gruetze
  // *
  // */
  // public static class UnspecifiedProgressRequest<T> extends
  // ProgressRequest<T> {
  // protected UnspecifiedProgressRequest() {
  // // hide default constructor
  // }
  // /**
  // * creates a request
  // *
  // * @param progressIdentifier
  // * the identifier
  // */
  // public UnspecifiedProgressRequest(long progressIdentifier) {
  // super(progressIdentifier);
  // }
  // }

}
