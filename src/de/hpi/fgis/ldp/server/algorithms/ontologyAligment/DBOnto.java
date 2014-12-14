package de.hpi.fgis.ldp.server.algorithms.ontologyAligment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

public class DBOnto {

  private static Property domainProperty = ResourceFactory
      .createProperty("http://www.w3.org/2000/01/rdf-schema#range");
  private static Property subClassOfProperty = ResourceFactory
      .createProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf");
  private static Property typeProperty = ResourceFactory
      .createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
  private static Resource classResource = ResourceFactory
      .createResource("http://www.w3.org/2002/07/owl#Class");
  // private static Resource datatypeResource =
  // ResourceFactory.createResource("http://www.w3.org/2002/07/owl#DatatypeProperty");
  // private static Resource objectResource =
  // ResourceFactory.createResource("http://www.w3.org/2002/07/owl#ObjectProperty");

  // mapping parent class -> subclasses
  private final HashMap<Resource, HashSet<Resource>> subClasses =
      new HashMap<Resource, HashSet<Resource>>();
  // mapping property -> main class -> subclasses (using domain attribute)
  private final HashMap<Resource, HashMap<Resource, Resource>> properties =
      new HashMap<Resource, HashMap<Resource, Resource>>();

  public String readFile(String fileName) {
    if (!fileName.endsWith(".owl")) {
      fileName = fileName + ".owl";
    }
    InputStream in = FileManager.get().open("data/" + fileName);

    Model model = ModelFactory.createDefaultModel();
    model.read(in, null);

    StmtIterator stmtIt = model.listStatements();
    while (stmtIt.hasNext()) {
      Statement st = stmtIt.next();
      // ... rdf:type owl:Class
      if (st.getPredicate().equals(typeProperty) && st.getObject().equals(classResource)) {
        Resource clazz = st.getSubject();
        if (clazz.isAnon()) {
          continue;
        }
        if (!subClasses.containsKey(clazz)) {
          subClasses.put(clazz, new HashSet<Resource>());
        }
      }
      // ... rdf:subClassOf ...
      if (st.getPredicate().equals(subClassOfProperty)) {
        Resource subclass = st.getSubject();
        Resource parentclass = st.getObject().asResource();
        if (subclass.isAnon() || parentclass.isAnon()) {
          continue;
        }
        if (subClasses.containsKey(parentclass)) {
          subClasses.get(parentclass).add(subclass);
        } else {
          HashSet<Resource> subClz = new HashSet<Resource>();
          subClz.add(subclass);
          subClasses.put(parentclass, subClz);
        }
      }
      // ... rdf:domain ...
      if (st.getPredicate().equals(domainProperty)) {
        Resource property = st.getSubject();
        Resource clazz = st.getObject().asResource();
        if (clazz.isAnon()) {
          continue;
        }
        if (properties.containsKey(property)) {
          properties.get(property).put(clazz, clazz);
        } else {
          HashMap<Resource, Resource> clz = new HashMap<Resource, Resource>();
          clz.put(clazz, clazz);
          properties.put(property, clz);
        }
      }
    }
    boolean changed = true;
    while (changed) {
      changed = false;
      for (Resource clz : subClasses.keySet()) {
        if (subClasses.get(clz).isEmpty()) {
          continue;
        }
        for (Entry<Resource, HashMap<Resource, Resource>> entry : properties.entrySet()) {
          if (entry.getValue().keySet().contains(clz)) {
            // cl is subclass of clz
            for (Resource cl : subClasses.get(clz)) {
              if (entry.getValue().put(cl, clz) == null) {
                changed = true;
              }
            }
          }
        }
      }
    }
    return fileName;
  }

  public void writeToFile(String fileName) {
    String propFileName = fileName.replaceAll(".owl", "-properties_ranges.nq");
    String classFileName = fileName.replace(".owl", "-subClasses.nq");
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(propFileName));
      for (Resource prop : properties.keySet()) {
        for (Resource clz : properties.get(prop).keySet()) {
          if (subClasses.get(properties.get(prop).get(clz)) != null
              && subClasses.get(properties.get(prop).get(clz)).contains(clz)) {
            bw.write("<" + prop + "> <" + domainProperty + "> <" + clz + "> ");
          } else {
            bw.write("<" + prop + "> <" + domainProperty + "> <" + clz + "> ");
          }
          HashSet<Resource> parentClasses = getParentClassWithProperty(clz, prop);
          for (Resource r : parentClasses) {
            bw.write("<" + r + "> ");
          }
          bw.write(" . \n");
          bw.flush();
        }
      }
      bw.close();

      bw = new BufferedWriter(new FileWriter(classFileName));
      HashSet<Resource> allSubClasses = new HashSet<Resource>();
      for (HashSet<Resource> sC : subClasses.values()) {
        allSubClasses.addAll(sC);
      }
      for (Resource clazz : allSubClasses) {
        HashMap<Resource, Integer> parentClasses = getParentClasses(clazz);
        for (Resource parentClass : parentClasses.keySet()) {
          bw.write("<" + clazz + "> <" + subClassOfProperty + "> <" + parentClass + "> "
              + parentClasses.get(parentClass) + " . \n");
          bw.flush();
        }
      }
      bw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the immediate parent class of the passed clazz parameter for which the property has
   * been defined for. Note that this parent class might be the clazz itself. Returns null if this
   * property has not been defined for the clazz.
   * 
   * @param property an RDF property
   * @param clazz an RDF class. It will be examined if the property has been defined for this class
   *        (or any of its parent classes) or not.
   * @return the immediate parent class for which this property has been defined for (could be the
   *         clazz itself) or null if the property has not been defined for this class (and any of
   *         its parent classes)
   */
  public Resource hasProperty(Property property, Resource clazz) {
    if (this.properties.containsKey(property)) {
      return this.properties.get(property).get(clazz);
    }
    return null;
  }

  /**
   * Returns the parent class of the passed clazz parameter for which the property has been defined
   * for. Note that this parent class might be the clazz itself. Returns null if this property has
   * not been defined for the clazz.
   * 
   * @param property an RDF property
   * @param clazz an RDF class. It will be examined if the property has been defined for this class
   *        (or any of its parent classes) or not.
   * @return the parent class for which this property has been defined for (could be the clazz
   *         itself) or null if the property has not been defined for this class (and any of its
   *         parent classes)
   */
  public Resource hasProperty(String property, String clazz) {
    if (clazz.startsWith("<") && clazz.endsWith(">")) {
      clazz = clazz.substring(1, clazz.length() - 1);
    }
    if (property.startsWith("<") && property.endsWith(">")) {
      property = property.substring(1, property.length() - 1);
    }
    Property prop = ResourceFactory.createProperty(property);
    Resource clz = ResourceFactory.createResource(clazz);
    return hasProperty(prop, clz);
  }

  private HashMap<Resource, Integer> getParentClasses(Resource clazz, int i) {
    HashMap<Resource, Integer> result = new HashMap<Resource, Integer>();
    result.put(clazz, i++);
    for (Resource parentClass : this.subClasses.keySet()) {
      if (this.subClasses.get(parentClass).contains(clazz)) {
        result.put(parentClass, i);
        result.putAll(getParentClasses(parentClass, i));
      }
    }
    return result;
  }

  private HashSet<Resource> getParentClassWithProperty(Resource clazz, Resource property) {
    HashSet<Resource> result = new HashSet<Resource>();
    if (properties.get(property).containsKey(clazz)) {
      result.add(clazz);
    } else {
      return result;
    }
    for (Resource parentClass : subClasses.keySet()) {
      if (subClasses.get(parentClass).contains(clazz)
          && properties.get(property).containsKey(parentClass)) {
        if (result.addAll(getParentClassWithProperty(parentClass, property))) {
          result.remove(clazz);
        }
      }
    }
    return result;
  }

  /**
   * Returns all parent classes of the clazz parameter alongside with their level of ancestry. Here,
   * level 0 is the clazz itself, level 1 are direct parent classes, level 2 are parents of parent
   * classes, etc.
   * 
   * @param clazz an RDF clazz
   * @return a HashMap containing all parent classes and their "level" of ancestry.
   */
  public HashMap<Resource, Integer> getParentClasses(Resource clazz) {
    return getParentClasses(clazz, 0);
  }

  /**
   * Returns all parent classes of the clazz parameter alongside with their level of ancestry. Here,
   * level 0 is the clazz itself, level 1 are direct parent classes, level 2 are parents of parent
   * classes, etc.
   * 
   * @param clazz an RDF clazz
   * @return a HashMap containing all parent classes and their "level" of ancestry.
   */
  public HashMap<Resource, Integer> getParentClasses(String clazz) {
    if (clazz.startsWith("<") && clazz.endsWith(">")) {
      clazz = clazz.substring(1, clazz.length() - 1);
    }
    return getParentClasses(ResourceFactory.createResource(clazz));
  }

  public static void main(String[] args) {
    String[] filelist;
    if (args.length == 0) {
      filelist = new File("data/").list();
    } else {
      filelist = args;
    }
    for (String file : filelist) {
      if (file.endsWith(".owl")) {
        DBOnto dbont = new DBOnto();
        dbont.readFile(file);
        dbont.writeToFile(file);
      }
    }
  }
}
