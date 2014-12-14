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

package de.hpi.fgis.ldp.client.mvp.main.content;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

import de.hpi.fgis.ldp.client.mvp.clustertree.ClusterTreeView;
import de.hpi.fgis.ldp.client.resource.image.ImageResources;

public abstract class AbstractMainContentView extends ContentPanel implements MainContentView {
  private final DockPanel waitingPanel;

  protected AbstractMainContentView() {
    this.waitingPanel = new DockPanel();

    this.waitingPanel.setPixelSize(700, 300);
    this.waitingPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    this.waitingPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

    this.waitingPanel.add(AbstractImagePrototype.create(ImageResources.INSTANCE.getLoading())
        .createImage(), DockPanel.CENTER);
  }

  /**
   * Returns this widget as the {@link ClusterTreeView#asWidget()} value.
   */
  @Override
  public Widget asWidget() {
    return this;
  }

  //
  @Override
  public void startProcessing() {
    // add sth like a progressbar
    // this.add(this.waitingPanel);
  }

  @Override
  public void stopProcessing() {
    // this.remove(this.waitingPanel);
  }
}
