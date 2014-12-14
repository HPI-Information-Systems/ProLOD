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

package de.hpi.fgis.ldp.exec.test;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import net.customware.gwt.dispatch.server.Dispatch;

import org.apache.commons.logging.Log;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.googlecode.concurrentlinkedhashmap.CapacityLimiter;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import de.hpi.fgis.ldp.exec.guice.DefaultModule;
import de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.rpc.CachableAction;
import de.hpi.fgis.ldp.shared.rpc.CachableResult;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.profiling.PredicateStatisticsRequest;

public class TestCacheSize {
  /**
   * @param args
   */
  public static void main(String[] args) {
    final Injector injector = Guice.createInjector(new DefaultModule());
    final TestCacheSize main = injector.getInstance(TestCacheSize.class);
    // TODO
    // DUMP_DIR = "./";

    if (args == null || args.length <= 0) {
      main.runForRandom();
      // args = new String[] {"BLUB", "DBPEDIA3", "DBPEDIA4", "DBPEDIA5",
      // "DBPEDIA6", "DBPEDIA7", "DRUGBANK", "LINKEDMDB",
      // "SEMANTIC_BIBLE", "NEW_TEST", "TEST" };
    }
  }

  private final ISchemaLoader schemaLoader;
  protected final Dispatch dispatch;
  protected final ConcurrentLinkedHashMap<CachableAction<?>, CachableResult> cache;
  protected final int maxCacheCapacity;
  private final Provider<DebugProgress> debugProcess;

  private Log logger;

  @Inject
  private TestCacheSize(ISchemaLoader schemaLoader, Dispatch dispatch,
      @Named("server.cache.maxCapacity") final int maxCacheCapacity,
      Provider<DebugProgress> debugProcess, Log logger) {
    this.schemaLoader = schemaLoader;
    this.dispatch = dispatch;
    this.logger = logger;
    this.maxCacheCapacity = maxCacheCapacity;
    this.debugProcess = debugProcess;

    ConcurrentLinkedHashMap.Builder<CachableAction<?>, CachableResult> cacheBuilder =
        new ConcurrentLinkedHashMap.Builder<CachableAction<?>, CachableResult>();

    cacheBuilder.initialCapacity(maxCacheCapacity / 5);
    cacheBuilder.maximumWeightedCapacity(maxCacheCapacity);
    cacheBuilder.concurrencyLevel(4);
    // no weighed entities now
    // cacheBuilder.weigher(new Weigher<CachableResult>() {
    // public int weightOf(CachableResult instance) {
    // // idea: the bigger e.g. the result table weight it has
    // return 1;
    // }
    // });
    cacheBuilder.capacityLimiter(new CapacityLimiter() {
      @Override
      public boolean hasExceededCapacity(ConcurrentLinkedHashMap<?, ?> cache) {
        return cache.size() >= maxCacheCapacity;
      }

    });

    this.cache = cacheBuilder.build();
  }

  private void runForRandom() {
    List<Cluster> allCluster = null;
    try {
      allCluster = schemaLoader.getRootClusters(debugProcess.get());
    } catch (SQLException e) {
      logger.error("Fehler!", e);
    }
    this.runFor(allCluster);
  }

  private Cluster cluster;

  private void runFor(List<Cluster> allCluster) {
    Random rnd = new Random();
    try {
      for (int i = 0; i < maxCacheCapacity; i++) {
        if (i % 20 == 0) {
          this.cluster = allCluster.get(rnd.nextInt(allCluster.size()));
        }
        cacheNStatistics(10);
        if (i % 20 == 0) {
          logger.info("caching the " + i + "th element (size: " + this.cache.size() + "|"
              + this.cache.weightedSize() + ")");
        }
      }
    } catch (Throwable e) {
      logger.error("Error during property profiling!", e);
    }
  }

  private void cacheNStatistics(int n) {
    for (int i = 0; i < n - 1; i++) {
      new Waiter().start();
    }
    new Waiter().run();
  }

  private static final class DummyKey implements CachableAction<DataTableResult> {
    private static final long serialVersionUID = -8771727253025180617L;
    private final CachableAction<DataTableResult> actualKey;

    public DummyKey(CachableAction<DataTableResult> actualKey) {
      super();
      this.actualKey = actualKey;
    }

    public CachableAction<DataTableResult> getActualKey() {
      return this.actualKey;
    }
  }

  private class Waiter extends Thread {
    @Override
    public void run() {
      try {
        final PredicateStatisticsRequest action = new PredicateStatisticsRequest(cluster, 0, 30);
        final DataTableResult result = dispatch.execute(action);
        synchronized (cache) {
          cache.put(new DummyKey(action), result);
        }
      } catch (Throwable e) {
        logger.error("Error during property profiling!", e);
      }
    }
  }
}
