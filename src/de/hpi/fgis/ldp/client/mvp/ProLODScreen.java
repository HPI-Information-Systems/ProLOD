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

package de.hpi.fgis.ldp.client.mvp;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;

import de.hpi.fgis.ldp.client.resource.image.Resources;

public class ProLODScreen extends ContentPanel {

  public ProLODScreen() {
    final DockPanel contentPanel = new DockPanel();
    contentPanel.setBorderWidth(0);

    contentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    contentPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

    // contentPanel.add(AbstractImagePrototype.create(ImageResources.INSTANCE.getProLODLogo()).createImage(),
    // DockPanel.CENTER);
    Resources resource = GWT.create(Resources.class);
    Image image = new Image(resource.getProLODLogo().getUrl());
    image.setSize("934px", "361px");
    contentPanel.add(image, DockPanel.CENTER);

    this.setHeaderVisible(false);
    this.setBorders(false);
    this.add(contentPanel, new RowData(1D, 1D));
    this.setLayout(new RowLayout());
  }
}
