package de.hpi.fgis.ldp.server.persistency.storage.impl.ontologyalignment;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.algorithms.ontologyAligment.TaxonomyManager;
import de.hpi.fgis.ldp.server.algorithms.ontologyAligment.TaxonomyNode;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.persistency.storage.IOntologyAlignmentStorage;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class OntologyAlignmentStorage extends LoaderBase implements IOntologyAlignmentStorage {
  private final Log logger;
  private final ResourceReader resourceReader = new ResourceReader();

  @Inject
  protected OntologyAlignmentStorage(Log logger) {
    this.logger = logger;
  }

  @Override
  public void createAlignment(DataSource dataSource, TaxonomyManager tm, IProgress progress)
      throws SQLException {

    Connection con = null;
    Statement createTables = null;
    int oldTransactionLevel = -1;
    try {
      con = super.newConnection(dataSource);

      con.setAutoCommit(false);
      oldTransactionLevel = con.getTransactionIsolation();
      con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      // create the table creation statement
      final List<String> createStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
              "sql/createOntologyAlignmentTables.sql");

      progress.startProgress("creating ontology table", createStmtStrings.size());

      for (final String currentStmt : createStmtStrings) {
        createTables = con.createStatement();
        createTables.execute(currentStmt, Statement.NO_GENERATED_KEYS);
        createTables.close();
        progress.continueProgress();
      }

    } catch (SQLException e) {
      logger.error("Unable to create ontology alignment tables!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.commit();
          con.setAutoCommit(true);
          if (oldTransactionLevel >= 0) {
            con.setTransactionIsolation(oldTransactionLevel);
          }
          con.close();
        }
        if (createTables != null) {
          createTables.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        logger.warn(this.getClass().getName() + "#createOntologyAlignmentTables()", e);
      }
    }

  }

  @Override
  public void writeOntologyChanges(DataSource source, TaxonomyManager tm, IProgress progress,
      TIntIntHashMap predicateToSourceMap) {
    Connection con = null;
    PreparedStatement ontologyRemovalstmt = null;
    PreparedStatement ontologyAdditionSatement = null;
    TIntObjectHashMap<TaxonomyNode> nodes = tm.gettTree().getNodePool();
    TIntIntHashMap sToCMap = tm.getSubjectToClusterMap();

    try {
      progress.startProgress("storing ontology Alignment");

      con = super.newConnection(source);

      // prepare the cluster creation statement
      final String ontologyRemovalString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?createPropertyRemoval.sql");
      ontologyRemovalstmt =
          con.prepareStatement(ontologyRemovalString, Statement.RETURN_GENERATED_KEYS);

      final String ontologyAdditionString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?createPropertyAddition.sql");
      ontologyAdditionSatement =
          con.prepareStatement(ontologyAdditionString, Statement.RETURN_GENERATED_KEYS);

      // store mean schema

      // write entities to db
      for (int s_id : nodes.keys()) {
        TaxonomyNode node = nodes.get(s_id);
        TIntSet candidates = node.getCandidates();
        int c_id = sToCMap.get(s_id);

        if (!candidates.isEmpty()) {
          for (int candidate : candidates.toArray()) {
            ontologyAdditionSatement.setInt(1, c_id);
            ontologyAdditionSatement.setInt(2, s_id);
            ontologyAdditionSatement.setInt(3, candidate);
            if (node.getPushedDownDandidates().containsKey(predicateToSourceMap.get(candidate))) {
              ontologyAdditionSatement.setInt(4,
                  node.getPushedDownDandidates().get(predicateToSourceMap.get(candidate)));
            } else {// if no source was defined
              ontologyAdditionSatement.setInt(4, -1);
            }
            ontologyAdditionSatement.addBatch();
          }
        }
        TIntSet overspecifications = node.getOverspecifications();
        if (!overspecifications.isEmpty()) {
          for (int removedP : overspecifications.toArray()) {
            ontologyRemovalstmt.setInt(1, c_id);
            ontologyRemovalstmt.setInt(2, s_id);
            ontologyRemovalstmt.setInt(3, removedP);
            ontologyRemovalstmt.addBatch();
          }
        }

      }
      ontologyAdditionSatement.executeBatch();
      ontologyRemovalstmt.executeBatch();
    } catch (SQLException e) {
      logger.error("Unable to store cluster data to DB!", e);
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }

        if (ontologyRemovalstmt != null) {
          ontologyRemovalstmt.close();
        }
        if (ontologyAdditionSatement != null) {
          ontologyAdditionSatement.close();
        }

      } catch (SQLException e) {
        // ignore Errors while closing
        logger.warn(this.getClass().getName() + "#storeSession()", e);
      }
    }

  }

}
