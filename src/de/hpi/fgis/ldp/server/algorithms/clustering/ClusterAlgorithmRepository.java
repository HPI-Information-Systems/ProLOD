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

package de.hpi.fgis.ldp.server.algorithms.clustering;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig.KMeansID;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig.OntologyID;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig.RecursiveKMeansID;

public class ClusterAlgorithmRepository {
  private final Map<Class<?>, IClusterAlgorithm> algorithms = new HashMap<Class<?>, IClusterAlgorithm>();

  @Inject
  protected ClusterAlgorithmRepository() {}

  @Inject
  protected void setAlgorithms(@KMeansID IClusterAlgorithm kMeans,
      @RecursiveKMeansID IClusterAlgorithm recusriveKMeans, @OntologyID IClusterAlgorithm ontology) {
    algorithms.put(KMeansID.class, kMeans);
    algorithms.put(RecursiveKMeansID.class, recusriveKMeans);
    algorithms.put(OntologyID.class, ontology);
  }

  public IClusterAlgorithm getAlgorithm(IClusterConfig config) {
    return this.algorithms.get(config.getAlgorithmID());
  }
}
