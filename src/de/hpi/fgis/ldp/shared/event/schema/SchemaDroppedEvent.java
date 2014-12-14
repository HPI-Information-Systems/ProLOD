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

package de.hpi.fgis.ldp.shared.event.schema;

import com.google.gwt.event.shared.GwtEvent;

import de.hpi.fgis.ldp.shared.data.DataSource;

public class SchemaDroppedEvent extends GwtEvent<SchemaDroppedEventHandler> {
  public static Type<SchemaDroppedEventHandler> TYPE = new Type<SchemaDroppedEventHandler>();

  private final DataSource schema;

  public SchemaDroppedEvent(final DataSource schema) {
    this.schema = schema;
  }

  public DataSource getDataSource() {
    return this.schema;
  }

  @Override
  public Type<SchemaDroppedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final SchemaDroppedEventHandler handler) {
    handler.onSchemaDrop(this);
  }

}
