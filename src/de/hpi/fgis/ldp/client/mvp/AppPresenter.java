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
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

import de.hpi.fgis.ldp.client.mvp.clustertree.ClusterTreePresenter;
import de.hpi.fgis.ldp.client.mvp.main.MainWidgetManagerPresenter;

public class AppPresenter {
  @Inject
  private ClusterTreePresenter clusterTreePresenter;
  @Inject
  private MainWidgetManagerPresenter mainWidgetPresenter;

  public void go(final HasWidgets container) {
    Viewport viewport = new Viewport();
    viewport.setLayout(new RowLayout());
    viewport.setBorders(false);

    ContentPanel mainWidget = this.getWidget();
    mainWidget.setHeaderVisible(false);
    mainWidget.setBorders(false);
    viewport.add(mainWidget, new RowData(1, 1));

    container.clear();
    container.add(viewport);
  }

  private ContentPanel getWidget() {
    // create the main pane
    final ContentPanel mainPane = new ContentPanel();

    mainPane.setLayout(new BorderLayout());
    mainPane.setSize(1, 1);
    mainPane.setBorders(false);
    mainPane.setHeaderVisible(false);

    this.addNavigationPane(mainPane);
    this.addContentPane(mainPane);

    return mainPane;
  }

  private void addNavigationPane(ContentPanel mainPane) {
    // the navigation panel contains the treeview to navigate through the
    // cluster tree and a settings pane
    // the navigation tree view pane
    // add nav view with 30% of width
    BorderLayoutData navigationLayoutData = new BorderLayoutData(LayoutRegion.WEST);
    navigationLayoutData.setSplit(true);
    navigationLayoutData.setCollapsible(true);
    navigationLayoutData.setFloatable(true);
    navigationLayoutData.setMargins(new Margins(0));
    navigationLayoutData.setSize(0.3f);

    mainPane.add(this.clusterTreePresenter.getDisplay().asWidget(), navigationLayoutData);
  }

  private void addContentPane(ContentPanel mainPane) {
    // add stats view with 70% of width
    BorderLayoutData statsViewData = new BorderLayoutData(LayoutRegion.CENTER);
    statsViewData.setMargins(new Margins(0));
    statsViewData.setSize(0.7f);

    mainPane.add(this.mainWidgetPresenter.getDisplay().asWidget(), statsViewData);
  }
}
