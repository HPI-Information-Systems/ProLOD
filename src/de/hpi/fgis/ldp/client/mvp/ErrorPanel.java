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

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.reveregroup.gwt.imagepreloader.FitImage;

import de.hpi.fgis.ldp.client.resource.image.ImageResources;

public class ErrorPanel extends ContentPanel {
  public ErrorPanel() {
    this(-1, -1);
  }

  public ErrorPanel(final int width, final int height) {
    super(new BorderLayout());
    if (width > 0 && height > 0) {
      this.setPixelSize(width, height);
    }

    this.setHeaderVisible(false);
    this.setBorders(false);
    this.setBodyBorder(false);
    this.setHeaderVisible(false);

    ContentPanel innerPanel = new ContentPanel(new BorderLayout());
    innerPanel.setHeaderVisible(false);
    innerPanel.setBorders(false);
    innerPanel.setBodyBorder(false);
    innerPanel.setHeaderVisible(false);

    Image logo =
        AbstractImagePrototype.create(ImageResources.INSTANCE.getErrorSign()).createImage();
    FitImage backgroundImage1 = new FitImage(ImageResources.INSTANCE.getErrorBackground().getURL());
    FitImage backgroundImage2 = new FitImage(ImageResources.INSTANCE.getErrorBackground().getURL());

    BorderLayoutData southWestData = new BorderLayoutData(LayoutRegion.CENTER);
    southWestData.setMargins(new Margins(0));

    BorderLayoutData southEastData = new BorderLayoutData(LayoutRegion.EAST);
    southEastData.setMargins(new Margins(0));
    southEastData.setSize(logo.getWidth());
    southEastData.setMaxSize(logo.getWidth());
    southEastData.setMinSize(logo.getWidth());

    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.CENTER);
    northData.setMargins(new Margins(0));

    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH);
    southData.setMargins(new Margins(0));
    southData.setSize(logo.getHeight());
    southData.setMaxSize(logo.getHeight());
    southData.setMinSize(logo.getHeight());

    innerPanel.add(backgroundImage1, southWestData);
    innerPanel.add(logo, southEastData);

    this.add(backgroundImage2, northData);
    this.add(innerPanel, southData);

  }

  // private LayoutContainer getBackground() {
  // LayoutContainer c = new LayoutContainer();
  // HBoxLayout layout = new HBoxLayout();
  // layout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
  // c.setLayout(layout);
  //
  // HBoxLayoutData flex = new HBoxLayoutData(new Margins(0));
  // flex.setFlex(1);
  //
  // Image errorBackgroundImage =
  // AbstractImagePrototype.create(ImageResources.INSTANCE.getErrorSign()).createImage();
  //
  // c.add(errorBackgroundImage, flex);
  //
  // return c;
  // }
  // private LayoutContainer getProLODImage() {
  // LayoutContainer c = new LayoutContainer();
  // HBoxLayout layout = new HBoxLayout();
  // layout.setHBoxLayoutAlign(HBoxLayoutAlign.BOTTOM);
  // layout.setPack(BoxLayoutPack.END);
  // c.setLayout(layout);
  //
  // HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0));
  //
  // Image image =
  // AbstractImagePrototype.create(ImageResources.INSTANCE.getErrorSign()).createImage();
  //
  // c.add(image, layoutData);
  // return c;
  // }
}
