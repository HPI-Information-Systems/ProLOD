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

import java.util.List;
import java.util.Set;

import de.hpi.fgis.ldp.server.algorithms.labeling.datastructures.ITokenFrequencyCache;
import de.hpi.fgis.ldp.server.algorithms.labeling.datastructures.Token;

/**
 * The DB2TokenFrequencyCache is used to efficiently determine the weight of a token. All tokens
 * will be stored in a db2 database to be able to handle a huge number of tokens.
 * 
 * @author david.sonnabend
 * 
 * @deprecated this implementation is too slow
 */
@Deprecated
public class DB2TokenFrequencyCache implements ITokenFrequencyCache {

  @Override
  public void addToken(Token token, int clusterID) {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // addToken(token.toString(), clusterID);
  }

  @Override
  public void addToken(String token, int clusterID) {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // DB2Connector con = DB2Connector.getInstance();
    // Statement stmt = con.getStatement();
    // try {
    // String updateQuery = "UPDATE "+ Constants.db2Schema + "." + tableName
    // + " SET CNT = CNT + 1 WHERE CLUSTER_ID = " + clusterID +
    // " AND TOKEN = '" + token + "'";
    // int affectedRows = stmt.executeUpdate(updateQuery);
    //
    // if(affectedRows == 0) {
    // String insertQuery = "INSERT INTO " + Constants.db2Schema + "." +
    // tableName + "( " +
    // "CLUSTER_ID, " +
    // "TOKEN, " +
    // "CNT " +
    // ") VALUES ( " +
    // clusterID + ", " +
    // "'" + token + "', " +
    // "1 " +
    // ")";
    //
    // stmt.execute(insertQuery);
    // weightsComputed = false;
    // }
    //
    // }catch (SQLException e) {
    // logger.severe("Unexpected SQLException occured!" + e.getMessage());
    // } finally {
    // con.releaseConnection();
    // try {
    // stmt.close();
    // } catch (SQLException e) {
    // logger.severe("SQL Exception while closing statement!" +
    // e.getMessage());
    // }
    // }
  }

  @Override
  public void computeTFIDFWeights() {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // logger.info("Computung TF-IDF token weights...");
    //
    // Set<Integer> clusterIDs = getAllClusterIDs();
    // int corpusSize = clusterIDs.size();
    //
    // String selectTokenQuery = "SELECT DISTINCT TOKEN " +
    // "FROM "+ Constants.db2Schema + "." + tableName + " " +
    // "WHERE CLUSTER_ID = ?";
    //
    // DB2Connector con = DB2Connector.getInstance();
    // PreparedStatement prepTokenStmt =
    // con.getPreparedStatement(selectTokenQuery);
    //
    // String updateWeightQuery = "UPDATE " + Constants.db2Schema + "." +
    // tableName + " " +
    // "SET WEIGHT = ? " +
    // "WHERE " +
    // "CLUSTER_ID = ? AND " +
    // "TOKEN = ?";
    //
    // DB2Connector con2 = DB2Connector.getInstance();
    // PreparedStatement prepUpdateWeightStmt =
    // con2.getPreparedStatement(updateWeightQuery);
    //
    // try {
    // for (Integer clusterID: clusterIDs) {
    // prepTokenStmt.setInt(1, clusterID);
    // prepUpdateWeightStmt.setInt(2, clusterID);
    //
    // ResultSet rsTokens = prepTokenStmt.executeQuery();
    //
    // int clusterTokenCount = getClusterTokenCount(clusterID);
    // while(rsTokens.next()) {
    // String token = rsTokens.getString("TOKEN");
    // prepUpdateWeightStmt.setString(3, token);
    //
    // double tf = (double) getTokenFrequency(token, clusterID) / (double)
    // clusterTokenCount;
    // int df = getDocumentFrequency(token);
    // double idf = 0.0;
    // if(df != 0)
    // idf= Math.log10((double) corpusSize / (double) df);
    //
    // prepUpdateWeightStmt.setDouble(1, tf*idf);
    // prepUpdateWeightStmt.executeUpdate();
    // }
    // }
    //
    // weightsComputed = true;
    // logger.info("TF-IDF token weights successfully computed.");
    // } catch (SQLException e) {
    // logger.severe("Unexpected SQLException occured!" + e.getMessage());
    // }finally{
    // con.releaseConnection();
    // con2.releaseConnection();
    // try {
    // prepTokenStmt.close();
    // prepUpdateWeightStmt.close();
    // } catch (SQLException e) {
    // logger.severe("SQL Exception while closing statement!" +
    // e.getMessage());
    // }
    // }
  }

  @Override
  public Set<Integer> getAllClusterIDs() {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // DB2Connector con = DB2Connector.getInstance();
    // Statement stmt = con.getStatement();
    // Set<Integer> clusterIDs = new HashSet<Integer>();
    // try {
    // ResultSet rs = stmt.executeQuery("SELECT DISTINCT CLUSTER_ID FROM " +
    // Constants.db2Schema + "." + tableName);
    // while(rs.next()) {
    // clusterIDs.add(rs.getInt(1));
    // }
    // rs.close();
    //
    // } catch (SQLException e) {
    // logger.severe("Unexpected SQLException occured!" + e.getMessage());
    // }finally {
    // con.releaseConnection();
    // try {
    // stmt.close();
    // } catch (SQLException e) {
    // logger.severe("SQL Exception while closing statement!" +
    // e.getMessage());
    // }
    // }
    // return clusterIDs;
  }

  @Override
  public int getDocumentFrequency(Token token) {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // return getDocumentFrequency(token.toString());
  }

  @Override
  public int getDocumentFrequency(String token) {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // DB2Connector con = DB2Connector.getInstance();
    // Statement stmt = con.getStatement();
    // int df = 0;
    //
    // try {
    // String countQuery = "SELECT COUNT(*) " +
    // "FROM " + Constants.db2Schema + "." + tableName + " " +
    // "WHERE TOKEN = '" + token + "'";
    //
    // ResultSet rs = stmt.executeQuery(countQuery);
    // rs.next();
    // df = rs.getInt(1) ;
    // rs.close() ;
    // } catch (SQLException e) {
    // logger.severe("Unexpected SQLException occured!" + e.getMessage());
    // }finally {
    // con.releaseConnection();
    // try {
    // stmt.close();
    // } catch (SQLException e) {
    // logger.severe("SQL Exception while closing statement!" +
    // e.getMessage());
    // }
    // }
    //
    // return df;
  }

  @Override
  public Token getMostImportantToken(int clusterID) {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // Token mostImportantToken = null;
    //
    // List<Token> tokens = getMostImportantTokens(clusterID, 1);
    // if(tokens != null)
    // mostImportantToken = tokens.get(0);
    //
    // return mostImportantToken;
  }

  @Override
  public List<Token> getMostImportantTokens(int clusterID, int n) {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // if(autoComputeWeights && !weightsComputed)
    // computeTFIDFWeights();
    //
    // if(!weightsComputed)
    // logger.warning("Trying to get the " + n +
    // " most important token without computing the real token weights!");
    //
    // DB2Connector con = DB2Connector.getInstance();
    // Statement stmt = con.getStatement();
    //
    // List<Token> tokens = new ArrayList<Token>();
    //
    // try {
    // String maxWeightQuery = "SELECT TOKEN " +
    // "FROM " + Constants.db2Schema + "." + tableName + " " +
    // "WHERE CLUSTER_ID = " + clusterID + " " +
    // "ORDER BY WEIGHT DESC " +
    // "FETCH FIRST " + n + " ROWS ONLY";
    //
    // ResultSet rs = stmt.executeQuery(maxWeightQuery);
    // while(rs.next()) {
    // tokens.add(new Token(rs.getString(1), 0));
    // }
    // rs.close() ;
    // } catch (SQLException e) {
    // logger.severe("Unexpected SQLException occured!" + e.getMessage());
    // }finally {
    // con.releaseConnection();
    // try {
    // stmt.close();
    // } catch (SQLException e) {
    // logger.severe("SQL Exception while closing statement!" +
    // e.getMessage());
    // }
    // }
    // return tokens;
  }

  @Override
  public int getTokenFrequency(Token token, int clusterID) {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // return getTokenFrequency(token, clusterID);
  }

  @Override
  public int getTokenFrequency(String token, int clusterID) {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // DB2Connector con = DB2Connector.getInstance();
    // Statement stmt = con.getStatement();
    // int tf = 0;
    //
    // try {
    // String countQuery = "SELECT COUNT(*) " +
    // "FROM " + Constants.db2Schema + "." + tableName + " " +
    // "WHERE TOKEN = '" + token + "' AND CLUSTER_ID = " + clusterID;
    //
    // ResultSet rs = stmt.executeQuery(countQuery);
    // rs.next();
    // tf = rs.getInt(1) ;
    // rs.close() ;
    // } catch (SQLException e) {
    // logger.severe("Unexpected SQLException occured!" + e.getMessage());
    // }finally {
    // con.releaseConnection();
    // try {
    // stmt.close();
    // } catch (SQLException e) {
    // logger.severe("SQL Exception while closing statement!" +
    // e.getMessage());
    // }
    // }
    //
    // return tf;
  }

  @Override
  public void destroyCache() {
    throw (new UnsupportedOperationException(
        "Unnsupported feature of slow ITokenFrequencyCache implementation is unable to load entity list with restricted properties!"));
    // DB2Connector con = DB2Connector.getInstance();
    // Statement stmt = con.getStatement();
    // try {
    // stmt.execute("DROP TABLE " + Constants.db2Schema + "." + tableName);
    // } catch (SQLException e) {
    // logger.severe("Unexpected SQLException occured!" + e.getMessage());
    // }finally {
    // con.releaseConnection();
    // try {
    // stmt.close();
    // } catch (SQLException e) {
    // logger.severe("SQL Exception while closing statement!" +
    // e.getMessage());
    // }
    // }
  }

  // private static Logger logger =
  // Logger.getLogger(DB2TokenFrequencyCache.class.getPackage().getName());
  //
  // /**
  // * Determines whether the token weights will be automatically computed
  // */
  // private boolean autoComputeWeights;
  //
  // /**
  // * Determines whether the weights of the current state of the cache are
  // computed
  // */
  // private boolean weightsComputed;
  //
  // /**
  // * Represents the name of the table uses as cache structure
  // */
  // private String tableName;
  //
  // /**
  // * Constructor initializes the cache.
  // */
  // public DB2TokenFrequencyCache() {
  // this(false, "TOKEN_FREQUENCY_CACHE", true);
  // }
  //
  // /**
  // * Constructor initializes the cache.
  // * @param autoUpdateWeights Determines whether the token weights will be
  // automatically computed.
  // * @param cacheName The name of the cache. Used as table name in the
  // db2-database.
  // * @param overwriteCache Determines whether an existing cache will be
  // deleted or reused.
  // */
  // public DB2TokenFrequencyCache(boolean autoComputeWeights, String
  // cacheName, boolean overwriteCache) {
  // this.autoComputeWeights = autoComputeWeights;
  // weightsComputed = false;
  // this.tableName = cacheName;
  // createCacheTable(overwriteCache);
  // }
  //
  // /**
  // * Creates the needed database table to be able to store tokens and its
  // weights and number of occurrence
  // * @param overwrite Determines whether an already existing table (with the
  // same name) will be deleted
  // */
  // private void createCacheTable(boolean overwrite) {
  // Statement stmt = DB2Connector.getInstance().getStatement();
  // try {
  // if (overwrite) {
  // logger.info("Drop token cache table '" + tableName +
  // "' if already exists...");
  //
  // try {
  // String dropTable = "DROP TABLE " + Constants.db2Schema + "." + tableName;
  // stmt.execute(dropTable);
  // }catch(SQLException e) {
  // if (e.getErrorCode() != -204)
  // throw e;
  //
  // }
  // }
  //
  // String createTableQuery = "CREATE TABLE " + Constants.db2Schema + "." +
  // tableName + "( " +
  // "CLUSTER_ID INT, " +
  // "TOKEN VARCHAR(" + Constants.lengthOfTextValues + "), " +
  // "CNT INT, " +
  // "WEIGHT DOUBLE)";
  //
  // stmt.execute(createTableQuery);
  // logger.info("Token cache table '" + tableName +
  // "' was created successfully.");
  // } catch (SQLException e) {
  // logger.severe("Unexpected SQLException occured!" + e.getMessage());
  // } finally {
  // try {
  // stmt.close();
  // } catch (SQLException e) {
  // logger.severe("SQL Exception while closing statement!" + e.getMessage());
  // }
  // }
  // }
  // /**
  // * Gets the number of tokens in a cluster.
  // * @param clusterID The ID of the cluster to get the number of tokens for.
  // * @return The number of tokens in the cluster.
  // */
  // private int getClusterTokenCount(int clusterID){
  // DB2Connector con = DB2Connector.getInstance();
  // Statement stmt = con.getStatement();
  // int tokenCount = 0;
  //
  // try {
  // String countQuery = "SELECT SUM(CNT) " +
  // "FROM " + Constants.db2Schema + "." + tableName + " " +
  // "WHERE CLUSTER_ID = " + clusterID;
  //
  // ResultSet rs = stmt.executeQuery(countQuery);
  // rs.next();
  // tokenCount = rs.getInt(1) ;
  // rs.close() ;
  // } catch (SQLException e) {
  // logger.severe("Unexpected SQLException occured!" + e.getMessage());
  // } finally {
  // con.releaseConnection();
  // try {
  // stmt.close();
  // } catch (SQLException e) {
  // logger.severe("SQL Exception while closing statement!" + e.getMessage());
  // }
  // }
  //
  // return tokenCount;
  // }
}
