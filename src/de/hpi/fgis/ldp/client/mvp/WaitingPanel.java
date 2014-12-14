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

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import de.hpi.fgis.ldp.client.resource.image.ImageResources;

public class WaitingPanel extends DockPanel {
  public WaitingPanel() {
    // this(600,300);
    this(-1, -1);
  }

  public WaitingPanel(final int width, final int height) {
    if (width > 0 && height > 0) {
      this.setPixelSize(width, height);
    }
    this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

    this.add(AbstractImagePrototype.create(ImageResources.INSTANCE.getLoading()).createImage(),
        DockPanel.CENTER);
  }
}
