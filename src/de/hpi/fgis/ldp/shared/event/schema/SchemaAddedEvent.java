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

import de.hpi.fgis.ldp.shared.data.Cluster;

public class SchemaAddedEvent extends GwtEvent<SchemaAddedEventHandler> {
  public static Type<SchemaAddedEventHandler> TYPE = new Type<SchemaAddedEventHandler>();

  private final Cluster cluster;

  public SchemaAddedEvent(final Cluster cluster) {
    this.cluster = cluster;
  }

  public Cluster getCluster() {
    return this.cluster;
  }

  @Override
  public Type<SchemaAddedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(final SchemaAddedEventHandler handler) {
    handler.onSchemaImport(this);
  }

}
