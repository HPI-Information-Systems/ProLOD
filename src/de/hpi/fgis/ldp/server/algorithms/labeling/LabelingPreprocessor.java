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

import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.algorithms.labeling.datastructures.ITokenFrequencyCache;
import de.hpi.fgis.ldp.server.algorithms.labeling.datastructures.Token;
import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.persistency.loading.ILabelLoader;
import de.hpi.fgis.ldp.server.persistency.loading.ILabelLoader.IClusterDescriptions;
import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Pair;

/**
 * The LabelingPreprocessor is used to compute labels for all clusters of a cluster session. For
 * that, all possible descriptions for entities in a cluster will be analyzed to get a human
 * readable label. The n top weighted (using TF-IDF) token will be used as label. Alternatively (if
 * no textual description could be found) the top-n properties of the cluster mean schema will be
 * used as label. All labels will be stored in the clustertable of the database.
 * 
 * @author david.sonnabend
 * 
 */
public class LabelingPreprocessor {
  private final Log logger;
  // TODO inject via setters/constructor
  @Inject
  private IClusterLoader clusterLoader;
  @Inject
  private IClusterStorage storage;
  @Inject
  private IMetaLoader metaLoader;
  @Inject
  private Provider<DebugProgress> debugProcess;

  private final ILabelLoader labelLoader;
  private final int labelTokenCount;
  private final int labelMeanSchemaCount;
  private final int minTokenLength;

  // private final static LabelingPreprocessor INSTANCE = new
  // LabelingPreprocessor();
  //
  // /**
  // * gets a instance of the {@link LabelingPreprocessor}
  // *
  // * @return a instance of the {@link LabelingPreprocessor}
  // */
  // public static LabelingPreprocessor getInstance() {
  // return LabelingPreprocessor.INSTANCE;
  // }

  /**
   * The ITokenFrequencyCache implementation to determine token weights
   */
  private ITokenFrequencyCache tokenFrequencyCache;

  /**
   * A set of stopwords used to determine if a token is useful or not
   */
  private Set<String> stopToken = null;

  /**
   * initializes the set of stop words by fetching them from the database table.
   */
  @Inject
  protected LabelingPreprocessor(@Named("labeling.labelTokenCount") int labelTokenCount,
      @Named("labeling.labelMeanSchemaCount") int labelMeanSchemaCount,
      @Named("labeling.minTokenLength") int minTokenLength, ILabelLoader labelLoader, Log logger) {
    logger.debug("Reading stopwords from database...");

    this.labelTokenCount = labelTokenCount;
    this.labelMeanSchemaCount = labelMeanSchemaCount;
    this.minTokenLength = minTokenLength;

    this.labelLoader = labelLoader;
    this.logger = logger;
  }

  /**
   * Computes the labels for all clusters of a cluster session and writes them into the database.
   * 
   * @param parent the parent Cluster to compute cluster labels for
   */
  public void computeClusterLabel(final Cluster parent, final IProgress progress) {
    progress.startProgress("Determining cluster labels for session '" + parent.getChildSessionID()
        + "'...", 5);

    final UnicodeRewriter unicodeRewriter = new UnicodeRewriter();
    final StringTokenizer tokenizer = StringTokenizer.getInstance();

    try {
      {
        // fetch textual data about entities
        IClusterDescriptions clusterDescriptions =
            this.labelLoader.iterateClusterDescriptions(
                new Session(parent.getDataSource(), parent.getChildSessionID()),
                progress.continueWithSubProgress(1));

        // get token frequency cache implementation (according to number
        // texts to anaylse)
        this.tokenFrequencyCache =
            TokenFrequencyCacheFactory.getTokenFrequencyCache(clusterDescriptions.size());

        for (Pair<Integer, String> clusterTexts : clusterDescriptions) {
          final String text = unicodeRewriter.rewrite(clusterTexts.getSecondElem());
          final int clusterID = clusterTexts.getFirstElem().intValue();

          final List<Token> tokens =
              tokenizer.tokenize(text, true, "[&#\\s!.\\*?,;\\`'�\"()=\\-\\[\\]]");

          for (Token token : tokens) {
            if (this.isUseful(token)) {
              this.tokenFrequencyCache.addToken(token, clusterID);
            }
          }
        }
        clusterDescriptions.close();
      }

      // compute tf-idf weights of each token
      this.tokenFrequencyCache.computeTFIDFWeights();

      final List<Cluster> clusters =
          clusterLoader.getClusters(parent, progress.continueWithSubProgress(1));
      if (clusters == null || clusters.isEmpty()) {
        return;
      }
      final ArrayList<Cluster> unlabeledClusters = new ArrayList<Cluster>();

      for (final Cluster currentCluster : clusters) {
        // TODO inject
        List<Token> labelTokens =
            this.tokenFrequencyCache.getMostImportantTokens(currentCluster.getId(),
                this.labelTokenCount);

        String clusterLabel = null;
        if (labelTokens == null) {
          unlabeledClusters.add(currentCluster);
        } else {
          StringBuilder clusterLabelBuilder = new StringBuilder();
          for (Token token : labelTokens) {
            clusterLabelBuilder.append(token.toString()).append(" ");
          }
          clusterLabel = clusterLabelBuilder.toString().trim();

          if (clusterLabel.equals("")) {
            unlabeledClusters.add(currentCluster);
          } else {
            // TODO use a single prepared statement
            storage.renameCluster(currentCluster, clusterLabel);
          }
        }
      }
      progress.continueProgress();

      if (unlabeledClusters.size() > 0) {
        // create labels for unlabeled clusters
        final List<EntityCluster> unlabeledEntityClusters =
            this.clusterLoader.getEntityClusters(unlabeledClusters,
                progress.continueWithSubProgress(1));

        // get cluster label from mean schema
        for (final EntityCluster currentCluster : unlabeledEntityClusters) {
          // get property name for the each property id
          TIntObjectHashMap<String> idNameMap =
              new TIntObjectHashMap<String>(currentCluster.getMeanSchema().size());
          for (Integer key : currentCluster.getMeanSchema()) {
            idNameMap.put(key, null);
          }

          this.metaLoader.fillTransactionNameMap(idNameMap, "sp", parent.getDataSource());

          int i = 0;
          final int maxSchemaSize =
              Math.min(currentCluster.getMeanSchema().size(), this.labelMeanSchemaCount);
          final StringBuilder clusterLabelBuilder = new StringBuilder("[");

          for (int id : currentCluster.getMeanSchema()) {
            // TODO inject
            if (++i > maxSchemaSize) {
              break;
            }
            clusterLabelBuilder.append(idNameMap.get(Integer.valueOf(id)));

            if (i < maxSchemaSize) {
              clusterLabelBuilder.append(' ');
            }
          }

          final String clusterLabel = clusterLabelBuilder.append(']').toString();

          // TODO use a single prepared statement
          storage.renameCluster(currentCluster.getMetaData(), clusterLabel);
        }
        progress.continueProgress();

      }
    } catch (SQLException e) {
      logger.warn("Unexpected SQLException occurred!", e);
    } finally {
      progress.stopProgress();
    }
  }

  /**
   * Computes the labels for all clusters of a cluster session and writes them into the database.
   * 
   * @param parent the parent Cluster to compute cluster labels for
   */
  public void setClusterLabel(final Cluster parent, final IProgress progress, String label) {
    progress.startProgress("Determining cluster labels for session '" + parent.getChildSessionID()
        + "'...", 5);

    final UnicodeRewriter unicodeRewriter = new UnicodeRewriter();
    final StringTokenizer tokenizer = StringTokenizer.getInstance();

    try {
      {
        // fetch textual data about entities
        IClusterDescriptions clusterDescriptions =
            this.labelLoader.iterateClusterDescriptions(
                new Session(parent.getDataSource(), parent.getChildSessionID()),
                progress.continueWithSubProgress(1));

        // get token frequency cache implementation (according to number
        // texts to anaylse)
        this.tokenFrequencyCache =
            TokenFrequencyCacheFactory.getTokenFrequencyCache(clusterDescriptions.size());

        for (Pair<Integer, String> clusterTexts : clusterDescriptions) {
          final String text = unicodeRewriter.rewrite(clusterTexts.getSecondElem());
          final int clusterID = clusterTexts.getFirstElem().intValue();

          final List<Token> tokens =
              tokenizer.tokenize(text, true, "[&#\\s!.\\*?,;\\`'�\"()=\\-\\[\\]]");

          for (Token token : tokens) {
            if (this.isUseful(token)) {
              this.tokenFrequencyCache.addToken(token, clusterID);
            }
          }
        }
        clusterDescriptions.close();
      }

      // compute tf-idf weights of each token
      this.tokenFrequencyCache.computeTFIDFWeights();

      final List<Cluster> clusters =
          clusterLoader.getClusters(parent, progress.continueWithSubProgress(1));
      if (clusters == null || clusters.isEmpty()) {
        return;
      }
      final ArrayList<Cluster> unlabeledClusters = new ArrayList<Cluster>();

      for (final Cluster currentCluster : clusters) {
        // TODO inject
        List<Token> labelTokens =
            this.tokenFrequencyCache.getMostImportantTokens(currentCluster.getId(),
                this.labelTokenCount);

        String clusterLabel = null;
        if (labelTokens == null) {
          unlabeledClusters.add(currentCluster);
        } else {
          StringBuilder clusterLabelBuilder = new StringBuilder();
          for (Token token : labelTokens) {
            clusterLabelBuilder.append(token.toString()).append(" ");
          }
          clusterLabel = clusterLabelBuilder.toString().trim();

          if (clusterLabel.equals("")) {
            unlabeledClusters.add(currentCluster);
          } else {
            // TODO use a single prepared statement
            storage.renameCluster(currentCluster, label);
          }
        }
      }
      progress.continueProgress();

      if (unlabeledClusters.size() > 0) {
        // create labels for unlabeled clusters
        final List<EntityCluster> unlabeledEntityClusters =
            this.clusterLoader.getEntityClusters(unlabeledClusters,
                progress.continueWithSubProgress(1));

        // get cluster label from mean schema
        for (final EntityCluster currentCluster : unlabeledEntityClusters) {
          // get property name for the each property id
          TIntObjectHashMap<String> idNameMap =
              new TIntObjectHashMap<String>(currentCluster.getMeanSchema().size());
          for (Integer key : currentCluster.getMeanSchema()) {
            idNameMap.put(key, null);
          }

          this.metaLoader.fillTransactionNameMap(idNameMap, "sp", parent.getDataSource());

          int i = 0;
          final int maxSchemaSize =
              Math.min(currentCluster.getMeanSchema().size(), this.labelMeanSchemaCount);
          final StringBuilder clusterLabelBuilder = new StringBuilder("[");

          for (int id : currentCluster.getMeanSchema()) {
            // TODO inject
            if (++i > maxSchemaSize) {
              break;
            }
            clusterLabelBuilder.append(idNameMap.get(Integer.valueOf(id)));

            if (i < maxSchemaSize) {
              clusterLabelBuilder.append(' ');
            }
          }

          final String clusterLabel = clusterLabelBuilder.append(']').toString();

          // TODO use a single prepared statement
          storage.renameCluster(currentCluster.getMetaData(), clusterLabel);
        }
        progress.continueProgress();

      }
    } catch (SQLException e) {
      logger.warn("Unexpected SQLException occurred!", e);
    } finally {
      progress.stopProgress();
    }
  }

  /**
   * Determines whether a token is useful for the labeling process.
   * 
   * @param token The token to check.
   * @return True if token is useful, False otherwise
   */
  private boolean isUseful(Token token) {
    if (token.length() < this.minTokenLength) {
      return false;
    }

    // stop token not yet loaded
    if (this.stopToken == null) {
      try {
        this.stopToken = labelLoader.getStopWords(debugProcess.get());
      } catch (SQLException e) {
        logger.warn("Error reading stopwords from database.", e);
        return true;
      }
    }

    if (this.stopToken.contains(token.toString())) {
      return false;
    }

    return true;
  }
}
