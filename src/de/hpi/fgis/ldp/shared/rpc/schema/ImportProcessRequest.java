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

package de.hpi.fgis.ldp.shared.rpc.schema;

import net.customware.gwt.dispatch.shared.Action;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * a request for a import procedure
 * 
 * @author toni.gruetze
 * 
 */
public class ImportProcessRequest implements Action<ImportProcessResult>, IsSerializable {
  private static final long serialVersionUID = 6383037668518671726L;

  private String schema = null;
  private String label = null;

  private Long processIdentifier = null;

  protected ImportProcessRequest() {
    // hide default constructor
  }

  /**
   * creates a request
   * 
   * @param schema the schema
   * @param label the label
   */
  public ImportProcessRequest(String label) {
    super();
    this.label = label;
  }

  /**
   * sets the schema name
   * 
   * @param schema the schema to set
   */
  public void setSchema(String schema) {
    this.schema = schema;
  }

  /**
   * gets the schema
   * 
   * @return the schema
   */
  public String getSchema() {
    return schema;
  }

  /**
   * gets the label
   * 
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * gets the process identifier
   * 
   * @return the process identifier
   */
  public Long getProcessIdentifier() {
    return processIdentifier;
  }

  /**
   * sets the process identifier
   * 
   * @param processIdentifier the process identifier
   */
  public void setProcessIdentifier(Long processIdentifier) {
    this.processIdentifier = processIdentifier;
  }
}
