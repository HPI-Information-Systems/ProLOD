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

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.schema.SchemaDropRequest;
import de.hpi.fgis.ldp.shared.rpc.schema.SchemaDropResult;

public class SchemaDropHandler implements ActionHandler<SchemaDropRequest, SchemaDropResult> {

  @Inject
  private Log logger;
  @Inject
  private ISchemaStorage schemaStorage;
  @Inject
  private Provider<DebugProgress> debugProcess;

  @Override
  public SchemaDropResult execute(final SchemaDropRequest action, final ExecutionContext context)
      throws RPCException {
    String schemaName = action.getSchema().getLabel().toUpperCase();

    try {
      // TODO inject
      schemaStorage.dropSchema(schemaName, debugProcess.get());

      return new SchemaDropResult(action.getSchema());
    } catch (SQLException cause) {
      logger.error("Unable to drop schema named \"" + schemaName + "\"!", cause);

      throw new RPCException(cause);
    }
  }

  @Override
  public void rollback(final SchemaDropRequest action, final SchemaDropResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<SchemaDropRequest> getActionType() {
    return SchemaDropRequest.class;
  }

}
