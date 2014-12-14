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

package de.hpi.fgis.ldp.client.mvp.main.content.profiling;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.Widget;

import de.hpi.fgis.ldp.client.service.AbstractCallbackDisplay;
import de.hpi.fgis.ldp.client.service.CallbackDisplay;
import de.hpi.fgis.ldp.client.view.datatable.AbstractDataTableChartPanel;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel;

public class DatatypeView extends ContentPanel implements DatatypePresenter.Display {

  private final ContentPanel linkLiteralChartPanel;
  private final ContentPanel datatypeChartPanel;
  private final ContentPanel datatypeTablePanel;

  private DataTablePanel datatypeTableTablePanel;
  private AbstractDataTableChartPanel datatypeChartTablePanel;
  private AbstractDataTableChartPanel linkLiteralChartTablePanel;

  private final AbstractCallbackDisplay datatypeCallback;
  private final AbstractCallbackDisplay linkLiteralCallback;

  public DatatypeView() {
    this.datatypeTablePanel = new ContentPanel();
    this.linkLiteralChartPanel = new ContentPanel();
    this.datatypeChartPanel = new ContentPanel();

    datatypeCallback = new AbstractCallbackDisplay(datatypeTablePanel, datatypeChartPanel) {
      @Override
      public void resetToDefault() {
        this.setContent(datatypeTableTablePanel, datatypeChartTablePanel);
      }
    };

    linkLiteralCallback = new AbstractCallbackDisplay(linkLiteralChartPanel) {
      @Override
      public void resetToDefault() {
        this.setContent(linkLiteralChartTablePanel);
      }
    };
    this.init();
  }

  @Override
  public void setDataTable(DataTablePanel table) {
    synchronized (this.datatypeTablePanel) {
      this.datatypeTableTablePanel = table;
    }
  }

  @Override
  public void setDatatypeChart(AbstractDataTableChartPanel chart) {
    synchronized (this.datatypeChartPanel) {
      this.datatypeChartTablePanel = chart;
    }
  }

  @Override
  public void setLinkLiteralChart(AbstractDataTableChartPanel chart) {
    synchronized (this.linkLiteralChartTablePanel) {
      this.linkLiteralChartTablePanel = chart;
    }
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void startProcessing() {
    datatypeCallback.startProcessing();
    linkLiteralCallback.startProcessing();
  }

  @Override
  public void stopProcessing() {
    datatypeCallback.stopProcessing();
    linkLiteralCallback.stopProcessing();
  }

  @Override
  public void displayError() {
    datatypeCallback.displayError();
    linkLiteralCallback.displayError();
  }

  private void init() {
    this.datatypeTablePanel.setHeaderVisible(false);
    this.datatypeTablePanel.setBorders(false);
    this.datatypeTablePanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

    this.datatypeChartPanel.setHeaderVisible(false);
    this.datatypeChartPanel.setBorders(false);
    this.datatypeChartPanel.setLayout(new RowLayout());

    this.linkLiteralChartPanel.setHeaderVisible(false);
    this.linkLiteralChartPanel.setBorders(false);
    this.linkLiteralChartPanel.setLayout(new RowLayout());

    ContentPanel chartPanel = new ContentPanel(new RowLayout(Orientation.HORIZONTAL));
    chartPanel.setHeaderVisible(false);
    chartPanel.setBorders(false);

    // chartPanel.add(this.datatypeChartPanel, new RowData(0.5, 1));
    // chartPanel.add(this.linkLiteralChartPanel, new RowData(0.5, 1));
    chartPanel.add(this.datatypeChartPanel, new RowData(1.0, 1));

    this.setLayout(new BorderLayout());
    this.setSize(1, 1);
    this.setBorders(false);
    this.setBodyBorder(false);
    this.setHeaderVisible(false);

    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.CENTER);
    northData.setMargins(new Margins(0));
    northData.setSize(0.7f);
    northData.setFloatable(true);
    northData.setHideCollapseTool(true);

    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH);
    southData.setMargins(new Margins(0));
    southData.setSize(0.3f);
    southData.setFloatable(true);
    southData.setHideCollapseTool(true);
    southData.setSplit(true);

    this.add(this.datatypeTablePanel, northData);
    this.add(chartPanel, southData);

    // this.add(this.datatypeTablePanel, new RowData(1, 0.7));
    // this.add(chartPanel, new RowData(1, 0.3));
    //
    // this.setLayout(new RowLayout(Orientation.VERTICAL));
    // this.setHeaderVisible(false);
    // this.setBorders(false);
  }

  @Override
  public CallbackDisplay getDatatypeDisplay() {
    return this.datatypeCallback;
  }

  @Override
  public CallbackDisplay getLinkLiteralDisplay() {
    return this.linkLiteralCallback;
  }
}
