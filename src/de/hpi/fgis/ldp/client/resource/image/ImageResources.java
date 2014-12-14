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

package de.hpi.fgis.ldp.client.resource.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * This interface provides a file-system abstraction for image resources
 */
public interface ImageResources extends ClientBundle {
  public static final ImageResources INSTANCE = GWT.create(ImageResources.class);

  // @Source("hpi_logo_gr.jpg")
  // ImageResource getHPILogo();

  @Source("prolodplusplus.jpg")
  ImageResource getProLODLogo();

  @Source("error.gif")
  ImageResource getErrorSign();

  @Source("error_back.gif")
  ImageResource getErrorBackground();

  @Source("ajax-loader.gif")
  ImageResource getLoading();
}
