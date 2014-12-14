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

package de.hpi.fgis.ldp.client.service;

import net.customware.gwt.dispatch.client.DefaultDispatchAsync;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.Result;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Dispatcher which support google analytics statistics
 * 
 */
public class AnalyticDispatchAsync implements DispatchAsync {
  private final DispatchAsync actualDispatcher;
  private final static int PACKAGE_IGNORE_LENGHT = "de.hpi.fgis.ldp.shared.rpc.".length();

  @Inject
  public AnalyticDispatchAsync(final DefaultDispatchAsync actualDispatcher,
      @Named("gui.admin") boolean adminMode) {
    this.actualDispatcher = actualDispatcher;

    if (adminMode) {
      track("startProLOD_Admin");
    } else {
      track("startProLOD");
    }
    // throw new
    // IllegalStateException("Please uncomment '<script src=\"../ga.js\"/>' in ProLOD.gwt.xml, to use the AnalyticDispatchAsync. This is a dummy exception and can be removed!!!");
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.customware.gwt.dispatch.client.DispatchAsync#execute(A,
   * com.google.gwt.user.client.rpc.AsyncCallback)
   */
  @Override
  public <A extends Action<R>, R extends Result> void execute(final A action,
      final AsyncCallback<R> callback) {
    actualDispatcher.execute(action, callback);

    String pageName = "default_action";
    if (action != null) {
      pageName = action.getClass().getName().substring(PACKAGE_IGNORE_LENGHT);
    }

    Log.debug(pageName);
    track(pageName);
  }

  /**
   * trigger google analytic native js - included in the build CHECK - DemoGoogleAnalytics.gwt.xml
   * for -> <script src="../ga.js"/>
   * 
   * http://code.google.com/intl/en-US/apis/analytics/docs/gaJS/ gaJSApiEventTracking.html
   * 
   * @param historyToken
   */
  private static native void track(String token) /*-{
                                                 var pageTracker = $wnd._gat._getTracker("UA-22735709-1");
                                                 pageTracker._trackPageview(token);
                                                 }-*/;

}
