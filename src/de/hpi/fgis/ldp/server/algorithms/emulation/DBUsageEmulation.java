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

package de.hpi.fgis.ldp.server.algorithms.emulation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.customware.gwt.dispatch.server.Dispatch;
import net.customware.gwt.dispatch.shared.ActionException;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.NormalizedPattern;
import de.hpi.fgis.ldp.shared.data.Pattern;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterChildrenRequest;
import de.hpi.fgis.ldp.shared.rpc.profiling.DataTypeDistributionRequest;
import de.hpi.fgis.ldp.shared.rpc.profiling.LinkLiteralStatisticsRequest;
import de.hpi.fgis.ldp.shared.rpc.profiling.NormalizedPatternStatisticsRequest;
import de.hpi.fgis.ldp.shared.rpc.profiling.ObjectStatisticsRequest;
import de.hpi.fgis.ldp.shared.rpc.profiling.PatternStatisticsRequest;
import de.hpi.fgis.ldp.shared.rpc.profiling.PredicateStatisticsRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.AntonymRequest;
import de.hpi.fgis.ldp.shared.rpc.schema.ChangeUserViewRequest;

/**
 * this is class emulates the usage of a selection or all schemas of the global datasource
 * 
 * @author toni.gruetze
 * 
 */
public class DBUsageEmulation {
  protected final Dispatch dispatch;
  private final Random rnd = new Random();
  private final Log logger;

  @Inject
  private DBUsageEmulation(Dispatch dispatch, Log logger) {
    this.dispatch = dispatch;
    this.logger = logger;
  }

  /**
   * runs the emulation for all available data sources
   */
  public void runForAll() {
    List<Cluster> allCluster = null;
    try {
      allCluster = this.getChildClusters(null);
    } catch (Throwable e) {
      logger.error("Fehler!", e);
    }
    this.runFor(allCluster);
  }

  /**
   * 
   * runs the emulation for a given set of data source names
   * 
   * @param sources the names of the sources
   */
  public void runFor(String... sources) {
    List<Cluster> rootClusters = new ArrayList<Cluster>(sources.length);
    try {
      for (String source : sources) {
        rootClusters.add(this.dispatch.execute(
            new ChangeUserViewRequest(new DataSource(source), null)).getRootCluster());
      }
    } catch (Throwable e) {
      logger.error("Fehler!", e);
    }
    this.runFor(rootClusters);
  }

  private void runFor(List<Cluster> rootClusters) {
    logger.debug("#######################");
    logger.debug("emulating usage for: ");
    for (final Cluster cluster : rootClusters) {
      logger.debug(cluster.getDataSource().getLabel() + " (" + cluster.getLabel() + ")");
    }
    logger.debug("#######################");
    for (final Cluster cluster : rootClusters) {
      logger.debug("===[initializing...]===");
      this.runFor(cluster);
      logger.debug("========[done!]========");
    }
  }

  private void runFor(Cluster rootCluster) {
    try {
      logger.debug(" ==> starting run for: " + rootCluster.getDataSource().getLabel() + " ("
          + rootCluster.getLabel() + ")");

      // profile root cluster
      profile(rootCluster);

      // profile 1st level child cluster
      List<Cluster> children = this.getChildClusters(rootCluster);

      int i = 0;
      for (Cluster currentCluster : children) {
        profile(currentCluster);
        i++;
        logger.debug("\t--> " + i + " --> " + currentCluster.getLabel() + " ("
            + ((100D * (i)) / children.size()) + "%)");

        // profile 2nd level child cluster
        List<Cluster> grandChildren = this.getChildClusters(currentCluster);

        for (Cluster currentSubcluster : grandChildren) {
          profile(currentSubcluster);
        }
      }

    } catch (Throwable e) {
      logger.error("Fehler!", e);
    } finally {
      logger.debug(" ==> finishing run for: " + rootCluster.getDataSource().getLabel() + " ("
          + rootCluster.getLabel() + ")");
    }
  }

  private List<Cluster> getChildClusters(Cluster parent) throws ActionException {
    return this.dispatch.execute(new ClusterChildrenRequest(parent)).getClusters();
  }

  private void profile(Cluster cluster) throws SQLException {
    this.profileLinkLiteralRatio(cluster);
    this.profileProperties(cluster);

    this.profileAntonyms(cluster);
  }

  private void profileProperties(Cluster cluster) {
    logger.debug("\t\t--> properties");
    try {
      IDataTable result =
          this.dispatch.execute(new PredicateStatisticsRequest(cluster, 0, 30)).getDataTable();

      // take x samples
      int sampleCount = 15;
      if (result.getRowCount() < sampleCount) {
        sampleCount = result.getRowCount();
      }
      for (int i = 0; i < sampleCount; i++) {
        //
        Predicate predicate =
            (Predicate) result.getColumn(0).getElement(rnd.nextInt(result.getRowCount()));

        // execute datatype query
        this.profileDatatypes(cluster, predicate);
      }
    } catch (Throwable e) {
      logger.error("Error during property profiling!", e);
    }
  }

  private void profileDatatypes(Cluster cluster, Predicate predicate) {
    // follow 50% of the requests
    if (rnd.nextBoolean()) {
      return;
    }
    logger.debug("\t\t\t--> datatypes");
    try {
      ArrayList<Predicate> predicates = new ArrayList<Predicate>(1);
      predicates.add(predicate);
      IDataTable result =
          this.dispatch.execute(new DataTypeDistributionRequest(cluster, predicates))
              .getDataTable();

      // take 1 sample
      int sampleCount = 3;
      if (result.getRowCount() < sampleCount) {
        sampleCount = result.getRowCount();
      }

      for (int i = 0; i < sampleCount; i++) {
        Datatype datatype =
            (Datatype) result.getColumn(0).getElement(rnd.nextInt(result.getRowCount()));

        // execute pattern/object query
        if (Datatype.STRING.equals(datatype)) {
          this.profileNormalizedPattern(cluster, predicates, datatype);
        } else if (Datatype.TEXT.equals(datatype) && rnd.nextBoolean()) {
          this.profileObjects(cluster, predicates, datatype, null);
        } else if (rnd.nextBoolean()) {
          // follow 50% of the requests
          this.profilePattern(cluster, predicates, datatype, null);
        }
      }
    } catch (Throwable e) {
      logger.error("Error during datatype profiling!", e);
    }
  }

  private void profileNormalizedPattern(Cluster cluster, ArrayList<Predicate> predicates,
      Datatype datatype) {
    // follow 50% of the requests
    if (rnd.nextBoolean()) {
      return;
    }
    logger.debug("\t\t\t--> normalized pattern");
    try {
      IDataTable result =
          this.dispatch.execute(
              new NormalizedPatternStatisticsRequest(cluster, predicates, datatype, 0, 30))
              .getDataTable();

      // take x samples
      int sampleCount = 5;
      if (result.getRowCount() < sampleCount) {
        sampleCount = result.getRowCount();
      }

      for (int i = 0; i < sampleCount; i++) {
        NormalizedPattern normalizedPattern =
            (NormalizedPattern) result.getColumn(0).getElement(rnd.nextInt(result.getRowCount()));

        this.profilePattern(cluster, predicates, datatype, normalizedPattern);
      }
    } catch (Throwable e) {
      logger.error("Error during normalized pattern profiling!", e);
    }
  }

  private void profilePattern(Cluster cluster, ArrayList<Predicate> predicates, Datatype datatype,
      NormalizedPattern normalizedPattern) {
    // follow 50% of the requests
    if (rnd.nextBoolean()) {
      return;
    }
    logger.debug("\t\t\t--> pattern");
    try {
      IDataTable result =
          this.dispatch
              .execute(
                  new PatternStatisticsRequest(cluster, predicates, datatype, normalizedPattern, 0,
                      30)).getDataTable();

      // take x samples
      int sampleCount = 1;
      if (result.getRowCount() < sampleCount) {
        sampleCount = result.getRowCount();
      }

      for (int i = 0; i < sampleCount; i++) {
        Pattern pattern =
            (Pattern) result.getColumn(0).getElement(rnd.nextInt(result.getRowCount()));

        // execute datatype query
        this.profileObjects(cluster, predicates, datatype, pattern);
      }
    } catch (Throwable e) {
      logger.error("Error during pattern profiling!", e);
    }
  }

  private void profileObjects(Cluster cluster, ArrayList<Predicate> predicates, Datatype datatype,
      Pattern pattern) {
    // follow 50% of the requests
    if (rnd.nextBoolean()) {
      return;
    }
    logger.debug("\t\t\t--> objects");
    try {
      // ignore results
      this.dispatch.execute(new ObjectStatisticsRequest(cluster, predicates, datatype, pattern, 0,
          30));
    } catch (Throwable e) {
      logger.error("Error during object determinition!", e);
    }
  }

  private void profileLinkLiteralRatio(Cluster cluster) {
    logger.debug("\t\t--> link literal ratio");
    try {
      // ignore results
      this.dispatch.execute(new LinkLiteralStatisticsRequest(cluster));
    } catch (Throwable e) {
      logger.error("Error during link literal ratio determinition!", e);
    }
  }

  private void profileAntonyms(Cluster cluster) {
    logger.debug("\t\t--> antonyms");
    try {
      // ignore results
      this.dispatch.execute(new AntonymRequest(cluster));
    } catch (Throwable e) {
      logger.error("Error during antonym determinition!", e);
    }
  }
}
