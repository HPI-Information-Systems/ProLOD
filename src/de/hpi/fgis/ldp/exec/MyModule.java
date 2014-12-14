package de.hpi.fgis.ldp.exec;

import java.util.Properties;

import org.apache.commons.logging.Log;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import de.hpi.fgis.ldp.config.ConfigUtil;
import de.hpi.fgis.ldp.server.algorithms.clustering.ClusterAlgorithmRepository;
import de.hpi.fgis.ldp.server.algorithms.clustering.ClusterJob;
import de.hpi.fgis.ldp.server.algorithms.clustering.IClusterAlgorithm;
import de.hpi.fgis.ldp.server.algorithms.clustering.kmeans.KMeansClustering;
import de.hpi.fgis.ldp.server.algorithms.clustering.kmeans.RecursiveKMeansClustering;
import de.hpi.fgis.ldp.server.algorithms.dataimport.ImportEntryIterable;
import de.hpi.fgis.ldp.server.algorithms.dataimport.ImportJob;
import de.hpi.fgis.ldp.server.algorithms.dataimport.file.Bz2FileReaderFactory;
import de.hpi.fgis.ldp.server.algorithms.dataimport.file.NTFileParser;
import de.hpi.fgis.ldp.server.algorithms.dataimport.patternstatistics.CharacterClassManager;
import de.hpi.fgis.ldp.server.algorithms.dataimport.patternstatistics.PatternAnalyzer;
import de.hpi.fgis.ldp.server.persistency.access.DataSourceProvider;
import de.hpi.fgis.ldp.server.persistency.access.IDataSource;
import de.hpi.fgis.ldp.server.persistency.access.ISQLDataSourceProvider;
import de.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool;
import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IInversePredicateLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IItemSetLoader;
import de.hpi.fgis.ldp.server.persistency.loading.ILabelLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader;
import de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.cluster.ClusterLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.entityschema.MyEntityLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.inversepredicates.InversePredicateLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.itemsets.MyItemSetLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.label.LabelLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.meta.MyMetaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.profiling.ProfilingLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.schema.SchemaLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.persistency.storage.IDataImport;
import de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage;
import de.hpi.fgis.ldp.server.persistency.storage.impl.cluster.ClusterStorage;
import de.hpi.fgis.ldp.server.persistency.storage.impl.dataimport.DataImport;
import de.hpi.fgis.ldp.server.persistency.storage.impl.schemastorage.SchemaStorage;
import de.hpi.fgis.ldp.server.service.guice.LogProvider;
import de.hpi.fgis.ldp.server.util.SSHClient;
import de.hpi.fgis.ldp.server.util.exception.ExceptionFactory;
import de.hpi.fgis.ldp.server.util.job.JobManager;
import de.hpi.fgis.ldp.server.util.job.JobNameSource;
import de.hpi.fgis.ldp.server.util.progress.MonitoringProgress;
import de.hpi.fgis.ldp.server.util.progress.UnspecifiedJobProgress;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig.KMeansID;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig.RecursiveKMeansID;

public class MyModule extends AbstractModule {
  @SuppressWarnings("unchecked")
  @Override
  protected void configure() {

    // my classes
    bind(IEntitySchemaLoader.class).to(MyEntityLoader.class);

    bind(IMetaLoader.class).to(MyMetaLoader.class);

    // default
    bind(Log.class).toProvider(LogProvider.class);

    bind(ConnectionPool.class);

    bind(IClusterLoader.class).to(ClusterLoader.class);
    bind(ISchemaLoader.class).to(SchemaLoader.class);
    bind(IItemSetLoader.class).to(MyItemSetLoader.class);
    bind(IInversePredicateLoader.class).to(InversePredicateLoader.class);

    bind(IClusterStorage.class).to(ClusterStorage.class);
    bind(ISchemaStorage.class).to(SchemaStorage.class);

    // no singleton because this class is using members
    bind(IProfilingLoader.class).to(ProfilingLoader.class);

    bind(IDataImport.class).to(DataImport.class);
    bind(ILabelLoader.class).to(LabelLoader.class);

    // Tools
    bind(CharacterClassManager.class);
    bind(PatternAnalyzer.class);

    bind(SSHClient.class);
    bind(ImportEntryIterable.class);
    bind(JobManager.class);
    bind(ImportJob.class);
    bind(ClusterJob.class);

    bind(MonitoringProgress.class);
    bind(UnspecifiedJobProgress.class);

    bind(ISQLDataSourceProvider.class).to(DataSourceProvider.class).in(Singleton.class);

    bind(ClusterAlgorithmRepository.class).in(Singleton.class);
    bind(IClusterAlgorithm.class).annotatedWith(KMeansID.class).to(KMeansClustering.class);
    bind(IClusterAlgorithm.class).annotatedWith(RecursiveKMeansID.class).to(
        RecursiveKMeansClustering.class);

    bind(JobNameSource.class).in(Singleton.class);

    bind(ExceptionFactory.class).in(Singleton.class);

    // FIXME bind(ISQLDataSourceProvider.class).to(LabelLoader.class);

    bind(Bz2FileReaderFactory.class);
    bind(NTFileParser.Factory.class);

    // // TODO sth else?
    // bind(AdvancedARF.class);

    try {
      Properties props = ConfigUtil.loadProperties();
      Names.bindProperties(binder(), props);
      // bind(IDataSource.class).to(DB2DataSource.class);
      bind(IDataSource.class).to(
          (Class<IDataSource>) Class.forName(props.getProperty("db.dataSource")));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
