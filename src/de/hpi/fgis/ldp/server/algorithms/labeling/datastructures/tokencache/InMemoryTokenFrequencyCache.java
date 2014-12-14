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

package de.hpi.fgis.ldp.server.algorithms.labeling.datastructures.tokencache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import de.hpi.fgis.ldp.server.algorithms.labeling.datastructures.ITokenFrequencyCache;
import de.hpi.fgis.ldp.server.algorithms.labeling.datastructures.Token;

/**
 * The InMemoryTokenFrequencyCache is used to efficiently determine the weight of a token. For this,
 * all informations are stored in in memory datastructures. So - the size of the cache is limited by
 * the java heap space.
 * 
 * @author david.sonnabend
 * 
 */
@SuppressWarnings("boxing")
public class InMemoryTokenFrequencyCache implements ITokenFrequencyCache {
  private static Logger logger = Logger.getLogger(InMemoryTokenFrequencyCache.class.getPackage()
      .getName());

  /**
   * Structure that maps a token to its number of occurrence *
   */
  class ClusterTokenCache extends HashMap<String, Integer> {
    static final long serialVersionUID = 12341234;
  }

  /**
   * Structure that maps a token to its weight
   */
  class ClusterWeightsCache extends HashMap<String, Double> {
    static final long serialVersionUID = 23412341;
  }

  /**
   * HashMap to store a ClusterTokenCache for a specified clusters (maps clusterId to
   * ClusterTokenCache)
   */
  private HashMap<Integer, ClusterTokenCache> tokenCache;

  /**
   * HashMap to store a ClusterWeightsCache for a specified clusters (maps clusterId to
   * ClusterWeightsCache)
   */
  private HashMap<Integer, ClusterWeightsCache> weightsCache;

  /**
   * Determines whether the token weights will be automatically computed
   */
  private final boolean autoComputeWeights;

  /**
   * Determines whether the weights of the current state of the cache are computed
   */
  private boolean weightsComputed;

  /**
   * A simple comparator to be able to sort a list of cluster cache entries (mapping from token to
   * its weight).
   */
  class ClusterEntryComparator implements Comparator<Entry<String, Double>> {
    @Override
    public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
      return Double.compare(o1.getValue(), o2.getValue());
    }
  }

  /**
   * Constructor initializes the cache.
   */
  public InMemoryTokenFrequencyCache() {
    this(false);
  }

  /**
   * Constructor initializes the cache.
   * 
   * @param autoUpdateWeights Determines whether the token weights will be automatically computed.
   */
  public InMemoryTokenFrequencyCache(boolean autoComputeWeights) {
    this.autoComputeWeights = autoComputeWeights;
    this.weightsComputed = false;
    this.tokenCache = new HashMap<Integer, ClusterTokenCache>();
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures. ITokenFrequencyCache
   * #addToken(de.hpi.fgis.ldp.server.algorithms.labeling.datastructures .Token, int)
   */
  @Override
  public void addToken(Token token, int clusterID) {
    this.addToken(token.toString(), clusterID);
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures.
   * ITokenFrequencyCache#addToken(java.lang.String, int)
   */
  @Override
  public void addToken(String token, int clusterID) {
    ClusterTokenCache clusterCache = this.tokenCache.get(clusterID);
    if (clusterCache == null) {
      clusterCache = new ClusterTokenCache();
      this.tokenCache.put(clusterID, clusterCache);
    }

    Integer tokenCount = clusterCache.get(token);
    tokenCount = (tokenCount == null) ? 1 : tokenCount + 1;
    clusterCache.put(token, tokenCount);
    this.weightsComputed = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures. ITokenFrequencyCache
   * #getTokenFrequency(de.hpi.fgis.ldp.server.algorithms.labeling .datastructures.Token, int)
   */
  @Override
  public int getTokenFrequency(Token token, int clusterID) {
    return this.getTokenFrequency(token.toString(), clusterID);
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures.
   * ITokenFrequencyCache#getTokenFrequency(java.lang.String, int)
   */
  @Override
  public int getTokenFrequency(String token, int clusterID) {
    ClusterTokenCache clusterCache = this.tokenCache.get(clusterID);
    if (clusterCache == null) {
      return 0;
    }

    Integer tokenCount = clusterCache.get(token);
    return (tokenCount == null) ? 0 : tokenCount;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures. ITokenFrequencyCache
   * #getDocumentFrequency(de.hpi.fgis.ldp.server.algorithms .labeling.datastructures.Token)
   */
  @Override
  public int getDocumentFrequency(Token token) {
    return this.getDocumentFrequency(token.toString());
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures.
   * ITokenFrequencyCache#getDocumentFrequency(java.lang.String)
   */
  @Override
  public int getDocumentFrequency(String token) {
    int df = 0;

    for (Entry<Integer, ClusterTokenCache> entry : this.tokenCache.entrySet()) {
      if (entry.getValue().containsKey(token)) {
        df++;
      }
    }

    return df;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures.
   * ITokenFrequencyCache#getAllClusterIDs()
   */
  @Override
  public Set<Integer> getAllClusterIDs() {
    return this.tokenCache.keySet();
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures.
   * ITokenFrequencyCache#computeTFIDFWeights()
   */
  @Override
  public void computeTFIDFWeights() {
    InMemoryTokenFrequencyCache.logger.info("Computung TF-IDF token weights...");
    this.weightsCache = new HashMap<Integer, ClusterWeightsCache>();
    Set<Integer> clusterIDs = this.getAllClusterIDs();
    int corpusSize = clusterIDs.size();

    for (Integer clusterID : clusterIDs) {
      ClusterWeightsCache clusterWeightsCache = new ClusterWeightsCache();
      ClusterTokenCache clusterTokenCache = this.tokenCache.get(clusterID);
      Set<String> tokens = clusterTokenCache.keySet();
      int clusterTokenCount = 0;

      for (String token : tokens) {
        clusterTokenCount += this.getTokenFrequency(token, clusterID);
      }

      for (String token : tokens) {
        double tf = (double) this.getTokenFrequency(token, clusterID) / (double) clusterTokenCount;
        int df = this.getDocumentFrequency(token);
        double idf = 0.0;
        if (df != 0) {
          idf = Math.log10((double) corpusSize / (double) df);
        }

        clusterWeightsCache.put(token, tf * idf);
      }

      this.weightsCache.put(clusterID, clusterWeightsCache);
    }
    this.weightsComputed = true;
    InMemoryTokenFrequencyCache.logger.info("TF-IDF token weights successfully computed.");
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures.
   * ITokenFrequencyCache#getMostImportantToken(int)
   */
  @Override
  public Token getMostImportantToken(int clusterID) {
    if (this.autoComputeWeights && !this.weightsComputed) {
      this.computeTFIDFWeights();
    }

    if (!this.weightsComputed) {
      InMemoryTokenFrequencyCache.logger
          .warning("Trying to get the most important token without computing the real token weights!");
    }

    ClusterWeightsCache clusterWeightsCache = this.weightsCache.get(clusterID);
    if (clusterWeightsCache == null) {
      return null;
    }

    double max = 0.0;
    Token mostImportantToken = null;
    for (Entry<String, Double> entry : clusterWeightsCache.entrySet()) {
      if (entry.getValue() > max) {
        max = entry.getValue();
        mostImportantToken = new Token(entry.getKey(), 0);
      }
    }

    return mostImportantToken;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures.
   * ITokenFrequencyCache#getMostImportantTokens(int, int)
   */
  @Override
  public List<Token> getMostImportantTokens(int clusterID, int n) {
    if (this.autoComputeWeights && !this.weightsComputed) {
      this.computeTFIDFWeights();
    }

    if (!this.weightsComputed) {
      InMemoryTokenFrequencyCache.logger.warning("Trying to get the " + n
          + " most important token without computing the real token weights!");
    }

    ClusterWeightsCache clusterWeightsCache = this.weightsCache.get(clusterID);
    if (clusterWeightsCache == null) {
      return null;
    }

    List<Entry<String, Double>> clusterEntryList =
        new ArrayList<Entry<String, Double>>(clusterWeightsCache.entrySet());
    Collections.sort(clusterEntryList, new ClusterEntryComparator());
    Collections.reverse(clusterEntryList);

    List<Token> tokens = new ArrayList<Token>();

    int i = 0;
    for (Iterator<Entry<String, Double>> iter = clusterEntryList.iterator(); iter.hasNext()
        && i < n; ++i) {
      tokens.add(new Token(iter.next().getKey(), 0));
    }

    return tokens;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.labeling.datastructures.
   * ITokenFrequencyCache#destroyCache()
   */
  @Override
  public void destroyCache() {
    this.tokenCache = null;
    Runtime.getRuntime().gc();
  }
}
