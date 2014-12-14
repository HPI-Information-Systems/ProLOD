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

package de.hpi.fgis.ldp.server.service.handler.schema;

import java.sql.SQLException;
import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.schema.ImportProcessRequest;
import de.hpi.fgis.ldp.shared.rpc.schema.ImportProcessResult;

public class ImportProcessHandler implements
    ActionHandler<ImportProcessRequest, ImportProcessResult> {

  @Inject
  private Log logger;
  @Inject
  private ISchemaLoader schemaLoader;
  @Inject
  private Provider<DebugProgress> debugProcess;

  private boolean replace = false;

  @Inject
  void setReplaceSchema(@Named("import.replaceSchemas") boolean replace) {
    this.replace = replace;
  }

  private int maxSchemaNameLength = 30;

  @Inject
  void setReplaceSchema(@Named("db.maxLengthOfSchemaNames") int maxSchemaNameLength) {
    this.maxSchemaNameLength = maxSchemaNameLength;

  }

  @Override
  public ImportProcessResult execute(final ImportProcessRequest action,
      final ExecutionContext context) throws RPCException {

    final String label = action.getLabel();
    String schemaName = action.getSchema();

    // no schema given
    if (schemaName == null) {
      schemaName = label;
    }

    // schema contains illegal chars
    if (schemaName.replaceAll("[A-Za-z0-9_]", "").length() != 0) {

      // FIXME unify schema name central
      schemaName = schemaName.toUpperCase().replaceAll("[^A-Z0-9]+", "_");

      // cut of the end (long schema names)
      if (schemaName.length() > this.maxSchemaNameLength) {
        schemaName = schemaName.substring(0, this.maxSchemaNameLength);
      }
    }

    // update (unified) schema name
    action.setSchema(schemaName);

    try {
      if (!this.replace) {
        // TODO inject progress
        List<Cluster> schemas = schemaLoader.getRootClusters(debugProcess.get());
        for (final Cluster currentSchema : schemas) {
          if (schemaName.equalsIgnoreCase(currentSchema.getDataSource().getLabel())) {
            throw new RPCException(new IllegalArgumentException("Schema name \"" + schemaName
                + "\" already exists!"));
          }
          if (label.equalsIgnoreCase(currentSchema.getLabel())) {
            throw new RPCException(new IllegalArgumentException("Schema label \"" + label
                + "\" already exists!"));
          }
        }
      }

      return new ImportProcessResult(action);
    } catch (SQLException cause) {
      logger.error("Unable to determine list of schemata of the ProLOD system!", cause);

      throw new RPCException(cause);
    }
  }

  @Override
  public void rollback(final ImportProcessRequest action, final ImportProcessResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<ImportProcessRequest> getActionType() {
    return ImportProcessRequest.class;
  }

}
