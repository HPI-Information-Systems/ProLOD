package de.hpi.fgis.ldp.exec;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.logging.Log;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.algorithms.associationrules.AssociationRuleJob;
import de.hpi.fgis.ldp.server.persistency.loading.impl.entityschema.MyEntityLoader;
import de.hpi.fgis.ldp.server.util.RulesConfidenceComparator;
import de.hpi.fgis.ldp.server.util.progress.CMDProgress;
import de.hpi.fgis.ldp.shared.data.ARSetModel;
import de.hpi.fgis.ldp.shared.data.AssociationRuleModel;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class MyExec {
  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    final Injector injector = Guice.createInjector(new MyModule());
    final MyExec main = injector.getInstance(MyExec.class);

    // TODO select cluster
    DataSource schema = new DataSource("DBPEDIA7");
    Cluster cluster = new Cluster(schema);
    cluster.setId(2);
    main.execute(cluster);
  }

  private final Provider<AssociationRuleJob> jobSource;
  private final Log logger;

  @Inject
  private MyExec(Provider<AssociationRuleJob> jobSource, Log logger) {
    this.jobSource = jobSource;
    this.logger = logger;
  }

  public void execute(Cluster cluster) throws Exception {
    AssociationRuleJob arJob = this.jobSource.get();
    logger.info("initializing job");
    arJob.init(CMDProgress.getInstance());
    double support = 0.01;
    arJob.init(cluster, 3, true, support, 0.0, 0.0, Integer.MAX_VALUE, "os");

    logger.info("executing job");
    arJob.execute();

    logger.info("getting results job");
    ARSetModel result = arJob.getResult();
    ArrayList<AssociationRuleModel> ruleList = result.getModelSet();
    Collections.sort(ruleList, Collections.reverseOrder(new RulesConfidenceComparator()));
    // print results
    for (AssociationRuleModel currentModel : ruleList) {
      System.out.print(currentModel.getCondition());
      System.out.print(" -> ");
      System.out.print(currentModel.getConsequence());
      System.out.print(": ");
      System.out.print(currentModel.getConfidence());
      System.out.print(" Frequency/ Support: " + currentModel.getFrequency() + "/"
          + ((double) currentModel.getFrequency() / MyEntityLoader.getBasketCount()));
      System.out.println(" | TID Example: " + currentModel.getSubjects().get(0));
      System.out.println();

    }
  }
}
