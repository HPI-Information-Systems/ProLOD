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

import de.hpi.fgis.ldp.shared.data.OntologyAlignmentModel;
import de.hpi.fgis.ldp.shared.data.SynonymSetModel;
import de.hpi.fgis.ldp.shared.rpc.progress.ProgressResult;

/**
 * this is a dummy class to enable the serialization of {@link ProgressResult} instances for
 * {@link SynonymSetModel} elements (e.g. for client - server communication)
 * 
 * @author ziawasch.abedjan
 * 
 */
public class OntologyAlignmentProgressResult extends ProgressResult<OntologyAlignmentModel> {

  /**
	 * 
	 */
  private static final long serialVersionUID = 3237850867605548701L;

  protected OntologyAlignmentProgressResult() {

  }
}
