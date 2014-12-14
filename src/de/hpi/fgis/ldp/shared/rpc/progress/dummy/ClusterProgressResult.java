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

package de.hpi.fgis.ldp.shared.rpc.progress.dummy;

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.rpc.progress.ProgressResult;

/**
 * this is a dummy class to enable the serialization of {@link ProgressResult} instances for
 * {@link Cluster} elements (e.g. for client - server communication)
 * 
 * @author toni.gruetze
 * 
 */
public class ClusterProgressResult extends ProgressResult<Cluster> {
  private static final long serialVersionUID = -6262420016423663842L;

  protected ClusterProgressResult() {

  }
}
