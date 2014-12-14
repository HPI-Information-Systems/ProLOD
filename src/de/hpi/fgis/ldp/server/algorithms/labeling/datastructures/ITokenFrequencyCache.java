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

package de.hpi.fgis.ldp.server.algorithms.labeling.datastructures;

import java.util.List;
import java.util.Set;

/**
 * The ITokenFrequencyCache defines the set set of methods which are used to determine the weights
 * of tokens in a set of clusters.
 * 
 * @author david.sonnabend
 */
public interface ITokenFrequencyCache {

  /**
   * Adds a token to the cache.
   * 
   * @param token The token to add to the cache.
   * @param clusterID The ID of the cluster the token came from.
   */
  public abstract void addToken(Token token, int clusterID);

  /**
   * Adds a token to the cache.
   * 
   * @param token The token to add to the cache.
   * @param clusterID The ID of the cluster the token came from.
   */
  public abstract void addToken(String token, int clusterID);

  /**
   * Gets the number of occurrence of the specified token in the cluster with the given ID.
   * 
   * @param token The token to check the frequency for.
   * @param clusterID The ID of the cluster of interest.
   * @return The number of occurrence of the token in the cluster.
   */
  public abstract int getTokenFrequency(Token token, int clusterID);

  /**
   * Gets the number of occurrence of the specified token in the cluster with the given ID.
   * 
   * @param token The token to check the frequency for.
   * @param clusterID The ID of the cluster of interest.
   * @return The number of occurrence of the token in the cluster.
   */
  public abstract int getTokenFrequency(String token, int clusterID);

  /**
   * Gets the document frequency of the given token. Document frequency = number of cluster
   * containing the token.
   * 
   * @param token The token to compute the document frequency for.
   * @return Document frequency of the token
   */
  public abstract int getDocumentFrequency(Token token);

  /**
   * Gets the document frequency of the given token. Document frequency = number of cluster
   * containing the token.
   * 
   * @param token The token to compute the document frequency for.
   * @return Document frequency of the token
   */
  public abstract int getDocumentFrequency(String token);

  /**
   * Gets the IDs of all known cluster.
   * 
   * @return List of cluster IDs
   */
  public abstract Set<Integer> getAllClusterIDs();

  /**
   * Computes the TF-IDF weights of each token.
   */
  public abstract void computeTFIDFWeights();

  /**
   * Gets the token with the highest weight in the specified cluster.
   * 
   * @param clusterID The ID of the cluster to search the most important token.
   * @return The @see Token with the highest TF-IDF weight.
   */
  public abstract Token getMostImportantToken(int clusterID);

  /**
   * Gets the n token with the highest weight in the specified cluster.
   * 
   * @param clusterID The ID of the cluster to search the most important tokens.
   * @param n The number of token to get.
   * @return List of @see Token with the highest TF-IDF weights.
   */
  public abstract List<Token> getMostImportantTokens(int clusterID, int n);

  /**
   * Destroys the cache.
   */
  public abstract void destroyCache();

}
