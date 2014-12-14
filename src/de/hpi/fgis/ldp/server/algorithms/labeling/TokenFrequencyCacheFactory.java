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

package de.hpi.fgis.ldp.server.algorithms.labeling;

import java.util.logging.Logger;

import de.hpi.fgis.ldp.server.algorithms.labeling.datastructures.ITokenFrequencyCache;
import de.hpi.fgis.ldp.server.algorithms.labeling.datastructures.tokencache.InMemoryTokenFrequencyCache;

/**
 * 
 * This factory class is used to create an instance of a @see ITokenFrequencyCache based on the
 * available heap space. It uses a simple heuristic based on the number of texts to analyze and its
 * average number of token.
 * 
 * @author david.sonnabend
 */
public class TokenFrequencyCacheFactory {
  private static Logger logger = Logger.getLogger(TokenFrequencyCacheFactory.class.getPackage()
      .getName());

  /**
   * The approximative number of chars in a word contained in a text
   */
  public static final int APPROX_WORD_LENGTH = 5;

  /**
   * This factory method determines whether to use a memory- or a database-based representation of
   * the @see ITokenFrequencyCache implementation.
   * 
   * @param textCount The number of texts which will be analyzed.
   * @return ITokenFrequencyCache implementation based on the currently free heap space.
   */
  public static ITokenFrequencyCache getTokenFrequencyCache(long textCount) {
    TokenFrequencyCacheFactory.logger.info("Using 'InMemoryTokenFrequencyCache' with " + textCount
        + "texts.");
    return new InMemoryTokenFrequencyCache();
  }
}
