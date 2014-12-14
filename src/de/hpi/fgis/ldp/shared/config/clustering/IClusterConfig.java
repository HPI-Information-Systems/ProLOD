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

package de.hpi.fgis.ldp.shared.config.clustering;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.inject.BindingAnnotation;

/**
 * interface for a configuration of a cluster run to be executed {@link KMeansID}
 * 
 * @author toni.gruetze
 * 
 */
public interface IClusterConfig extends Serializable, IsSerializable {
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.PARAMETER})
  @BindingAnnotation
  public @interface KMeansID {
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.PARAMETER})
  @BindingAnnotation
  public @interface RecursiveKMeansID {
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.PARAMETER})
  @BindingAnnotation
  public @interface OntologyID {
  }

  /**
   * gets the algorithm identifier interface class (e.g. {@link KMeansID})
   * 
   * @return algorithm identifier
   */
  public abstract Class<?> getAlgorithmID();
}
