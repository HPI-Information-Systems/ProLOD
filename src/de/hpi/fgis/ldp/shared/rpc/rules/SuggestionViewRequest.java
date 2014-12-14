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

package de.hpi.fgis.ldp.shared.rpc.rules;

import net.customware.gwt.dispatch.shared.Action;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.rpc.AbstractClusterRequest;

public class SuggestionViewRequest extends AbstractClusterRequest implements
    Action<SuggestionViewResult> {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1215039591981427851L;

  protected SuggestionViewRequest() {
    // hide default constructor
  }

  public SuggestionViewRequest(final Cluster cluster) {
    super(cluster);

  }

}
