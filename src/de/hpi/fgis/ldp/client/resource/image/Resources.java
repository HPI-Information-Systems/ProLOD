package de.hpi.fgis.ldp.client.resource.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface Resources extends ClientBundle {
  public static final ImageResources INSTANCE = GWT.create(ImageResources.class);

  // @Source("hpi_logo_gr.jpg")
  // ImageResource getHPILogo();

  @Source("prolodplusplus.jpg")
  DataResource getProLODLogo();

}
