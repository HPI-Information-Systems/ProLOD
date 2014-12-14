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

package de.hpi.fgis.ldp.server.service.guice;

import java.util.Properties;

import net.customware.gwt.dispatch.server.guice.ActionHandlerModule;

import org.apache.commons.logging.Log;

import com.google.inject.Singleton;
import com.google.inject.name.Names;

import de.hpi.fgis.ldp.config.ConfigUtil;
import de.hpi.fgis.ldp.server.algorithms.associationrules.AssociationRuleJob;
import de.hpi.fgis.ldp.server.algorithms.clustering.ClusterAlgorithmRepository;
import de.hpi.fgis.ldp.server.algorithms.clustering.ClusterJob;
import de.hpi.fgis.ldp.server.algorithms.clustering.IClusterAlgorithm;
import de.hpi.fgis.ldp.server.algorithms.clustering.kmeans.KMeansClustering;
import de.hpi.fgis.ldp.server.algorithms.clustering.kmeans.RecursiveKMeansClustering;
import de.hpi.fgis.ldp.server.algorithms.clustering.ontology.OntologyClustering;
import de.hpi.fgis.ldp.server.algorithms.dataimport.ImportEntryIterable;
import de.hpi.fgis.ldp.server.algorithms.dataimport.ImportJob;
import de.hpi.fgis.ldp.server.algorithms.dataimport.file.Bz2FileReaderFactory;
import de.hpi.fgis.ldp.server.algorithms.dataimport.file.NTFileParser;
import de.hpi.fgis.ldp.server.algorithms.dataimport.patternstatistics.CharacterClassManager;
import de.hpi.fgis.ldp.server.algorithms.dataimport.patternstatistics.PatternAnalyzer;
import de.hpi.fgis.ldp.server.algorithms.emulation.DBUsageEmulation;
import de.hpi.fgis.ldp.server.algorithms.factgeneration.FactGenerationJob;
import de.hpi.fgis.ldp.server.algorithms.factgeneration.SuggestionJob;
import de.hpi.fgis.ldp.server.algorithms.ontologyAligment.OntologyAlignmentJob;
import de.hpi.fgis.ldp.server.algorithms.synonyms.SynonymDiscoveryJob;
import de.hpi.fgis.ldp.server.persistency.access.DataSourceProvider;
import de.hpi.fgis.ldp.server.persistency.access.IDataSource;
import de.hpi.fgis.ldp.server.persistency.access.ISQLDataSourceProvider;
import de.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool;
import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IFactGenerationLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IInversePredicateLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IItemSetLoader;
import de.hpi.fgis.ldp.server.persistency.loading.ILabelLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader;
import de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IUniquenessLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.cluster.ClusterLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.entityschema.MyEntityLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.factGeneration.FactGenerationLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.inversepredicates.InversePredicateLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.itemsets.MyItemSetLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.label.LabelLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.meta.MyMetaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.profiling.ProfilingLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.schema.SchemaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.uniqueness.UniquenessLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.persistency.storage.IDataImport;
import de.hpi.fgis.ldp.server.persistency.storage.IOntologyAlignmentStorage;
import de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage;
import de.hpi.fgis.ldp.server.persistency.storage.impl.cluster.ClusterStorage;
import de.hpi.fgis.ldp.server.persistency.storage.impl.dataimport.DataImport;
import de.hpi.fgis.ldp.server.persistency.storage.impl.ontologyalignment.OntologyAlignmentStorage;
import de.hpi.fgis.ldp.server.persistency.storage.impl.schemastorage.SchemaStorage;
import de.hpi.fgis.ldp.server.service.handler.cluster.ClusterInfoHandler;
import de.hpi.fgis.ldp.server.service.handler.cluster.ClusterMergeHandler;
import de.hpi.fgis.ldp.server.service.handler.cluster.ClusterRenameHandler;
import de.hpi.fgis.ldp.server.service.handler.cluster.RequestClustersHandler;
import de.hpi.fgis.ldp.server.service.handler.cluster.SubclusterHandler;
import de.hpi.fgis.ldp.server.service.handler.cluster.SubjectDetailsRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.cluster.SubjectSampleRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.profiling.DataTypeDistributionRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.profiling.LinkLiteralStatisticRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.profiling.NormalizedPatternStatisticsRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.profiling.ObjectStatisticsRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.profiling.PatternStatisticsRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.profiling.PredicateLinkLiteralStatisticRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.profiling.PredicateStatisticRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.profiling.UniquenessRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.progress.ProgressHandler;
import de.hpi.fgis.ldp.server.service.handler.rules.AntonymRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.rules.AssociationRuleRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.rules.FactGenerationRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.rules.OntologyAlignmentRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.rules.SuggestionViewRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.rules.SynonymsRequestHandler;
import de.hpi.fgis.ldp.server.service.handler.schema.ChangeUserViewHandler;
import de.hpi.fgis.ldp.server.service.handler.schema.ImportProcessHandler;
import de.hpi.fgis.ldp.server.service.handler.schema.SchemaDropHandler;
import de.hpi.fgis.ldp.server.util.SSHClient;
import de.hpi.fgis.ldp.server.util.exception.ExceptionFactory;
import de.hpi.fgis.ldp.server.util.job.JobManager;
import de.hpi.fgis.ldp.server.util.job.JobNameSource;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.server.util.progress.MonitoringProgress;
import de.hpi.fgis.ldp.server.util.progress.UnspecifiedJobProgress;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig.KMeansID;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig.OntologyID;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig.RecursiveKMeansID;

/**
 * Module which binds the handlers and configurations
 */
public class ServerModule extends ActionHandlerModule {

  @Deprecated
  public final static String MAIN_SCHEMA = "PROLOD_MAIN";

  public ServerModule() {
    System.out.println("-----------------------------------");
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  protected void configureHandlers() {
    bindHandler(RequestClustersHandler.class);
    bindHandler(ClusterRenameHandler.class);
    bindHandler(SubclusterHandler.class);
    bindHandler(ClusterMergeHandler.class);
    bindHandler(ClusterInfoHandler.class);

    bindHandler(PredicateStatisticRequestHandler.class);
    bindHandler(LinkLiteralStatisticRequestHandler.class);
    bindHandler(DataTypeDistributionRequestHandler.class);
    bindHandler(PredicateLinkLiteralStatisticRequestHandler.class);
    bindHandler(NormalizedPatternStatisticsRequestHandler.class);
    bindHandler(PatternStatisticsRequestHandler.class);
    bindHandler(ObjectStatisticsRequestHandler.class);

    bindHandler(AntonymRequestHandler.class);
    bindHandler(AssociationRuleRequestHandler.class);
    bindHandler(OntologyAlignmentRequestHandler.class);
    bindHandler(SynonymsRequestHandler.class);
    bindHandler(FactGenerationRequestHandler.class);
    bindHandler(UniquenessRequestHandler.class);
    bindHandler(SuggestionViewRequestHandler.class);

    bindHandler(SubjectSampleRequestHandler.class);
    bindHandler(SubjectDetailsRequestHandler.class);

    bindHandler(ImportProcessHandler.class);
    bindHandler(SchemaDropHandler.class);
    bindHandler(ChangeUserViewHandler.class);

    // bindHandler(ClusterProgressHandler.class);
    // bindHandler(AssociationRuleProgressHandler.class);
    bindHandler((Class) ProgressHandler.class);

    bind(Log.class).toProvider(LogProvider.class);

    bind(ConnectionPool.class);

    bind(IClusterLoader.class).to(ClusterLoader.class);
    bind(ISchemaLoader.class).to(SchemaLoader.class);
    // substituted LazyEntitySchemaLoader.class
    bind(IEntitySchemaLoader.class).to(MyEntityLoader.class);
    bind(IItemSetLoader.class).to(MyItemSetLoader.class);
    bind(IInversePredicateLoader.class).to(InversePredicateLoader.class);
    bind(IFactGenerationLoader.class).to(FactGenerationLoader.class);
    bind(IUniquenessLoader.class).to(UniquenessLoader.class);
    bind(IOntologyAlignmentStorage.class).to(OntologyAlignmentStorage.class);

    bind(IClusterStorage.class).to(ClusterStorage.class);
    bind(ISchemaStorage.class).to(SchemaStorage.class);

    // no singleton because this class is using members
    bind(IProfilingLoader.class).to(ProfilingLoader.class);

    bind(IDataImport.class).to(DataImport.class);
    bind(ILabelLoader.class).to(LabelLoader.class);
    // substituted bind(IMetaLoader.class).to(MetaLoader.class);
    bind(IMetaLoader.class).to(MyMetaLoader.class);

    bind(ISQLDataSourceProvider.class).to(DataSourceProvider.class).in(Singleton.class);

    bind(DBUsageEmulation.class);

    // Tools
    bind(CharacterClassManager.class);
    bind(PatternAnalyzer.class);

    bind(SSHClient.class);
    bind(ImportEntryIterable.class);
    bind(JobManager.class);
    bind(ImportJob.class);
    bind(ClusterJob.class);

    bind(AssociationRuleJob.class);
    bind(OntologyAlignmentJob.class);
    bind(SynonymDiscoveryJob.class);
    bind(FactGenerationJob.class);
    bind(SuggestionJob.class);

    bind(MonitoringProgress.class);
    bind(DebugProgress.class);
    bind(UnspecifiedJobProgress.class);

    bind(ClusterAlgorithmRepository.class).in(Singleton.class);
    bind(IClusterAlgorithm.class).annotatedWith(OntologyID.class).to(OntologyClustering.class);
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
