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

package de.hpi.fgis.ldp.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.hpi.fgis.ldp.shared.data.table.impl.DataColumn;

/**
 * This class is a container for ontology alignment.
 * 
 * @author ziawasch.abedjan
 * 
 */
public class OntologyAlignmentModel implements IsSerializable, Serializable {

  private static final long serialVersionUID = 1L;

  private DataColumn<Predicate> properties;
  private DataColumn<Predicate> removedProperties;
  private DataColumn<Predicate> inclusionProperties;
  private DataColumn<String> souceClass;
  private DataColumn<String> sourceClassRemoved;

  public DataColumn<Predicate> getSubjects() {
    return properties;
  }

  public DataColumn<Predicate> getProperties() {
    return properties;
  }

  public void setProperties(DataColumn<Predicate> existingschema) {
    this.properties = existingschema;
  }

  public DataColumn<Predicate> getRemovedProperties() {
    return removedProperties;
  }

  public void setRemovedProperties(DataColumn<Predicate> removedSchema) {
    this.removedProperties = removedSchema;
  }

  public DataColumn<Predicate> getInclusionProperties() {
    return inclusionProperties;
  }

  public void setInclusionProperties(DataColumn<Predicate> inclusionProperties2) {
    this.inclusionProperties = inclusionProperties2;
  }

  /**
   * Enables the default constructor for RPCs
   */
  protected OntologyAlignmentModel() {
    // nothing to do
  }

  /**
   * Creates new Container with all synonym pairs and a map of statistics about how frequent large
   * itemsets of a certain size occur
   * 
   * @param sourceClass
   * @param sourceClassRemoved
   * 
   * @param modelSet
   */
  public OntologyAlignmentModel(DataColumn<Predicate> existingschema,
      DataColumn<Predicate> removedSchema, DataColumn<Predicate> inclusionProperties2,
      DataColumn<String> sourceClass, DataColumn<String> sourceClassRemoved) {
    setProperties(existingschema);
    setRemovedProperties(removedSchema);
    setInclusionProperties(inclusionProperties2);
    setSouceClass(sourceClass);
    this.setSourceClassRemoved(sourceClassRemoved);
  }

  public DataColumn<String> getSouceClass() {
    return souceClass;
  }

  public void setSouceClass(DataColumn<String> souceClass) {
    this.souceClass = souceClass;
  }

  public DataColumn<String> getSourceClassRemoved() {
    return sourceClassRemoved;
  }

  public void setSourceClassRemoved(DataColumn<String> sourceClassRemoved) {
    this.sourceClassRemoved = sourceClassRemoved;
  }

}
