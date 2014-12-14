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

public class PatternView extends ContentPanel implements PatternPresenter.Display {

  private final ContentPanel chartPanel;
  private final ContentPanel tablePanel;

  private DataTablePanel tableTablePanel;
  private AbstractDataTableChartPanel chartTablePanel;

  private final AbstractCallbackDisplay dataCallback;

  public PatternView() {
    this.tablePanel = new ContentPanel();
    this.chartPanel = new ContentPanel();

    dataCallback = new AbstractCallbackDisplay(tablePanel, chartPanel) {
      @Override
      public void resetToDefault() {
        this.setContent(tableTablePanel, chartTablePanel);
      }
    };
    this.init();
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void startProcessing() {
    dataCallback.startProcessing();
  }

  @Override
  public void stopProcessing() {
    dataCallback.stopProcessing();
  }

  @Override
  public void displayError() {
    dataCallback.displayError();
  }

  @Override
  public void setDataTable(DataTablePanel table) {
    synchronized (this.tablePanel) {
      this.tableTablePanel = table;
    }
  }

  @Override
  public void setDataChart(AbstractDataTableChartPanel chart) {
    synchronized (this.chartPanel) {
      this.chartTablePanel = chart;
    }
  }

  private void init() {
    this.tablePanel.setHeaderVisible(false);
    this.tablePanel.setBorders(false);
    this.tablePanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

    this.chartPanel.setHeaderVisible(false);
    this.chartPanel.setBorders(false);
    this.chartPanel.setLayout(new RowLayout());

    ContentPanel chartPanel = new ContentPanel(new RowLayout(Orientation.HORIZONTAL));
    chartPanel.setHeaderVisible(false);
    chartPanel.setBorders(false);

    chartPanel.add(this.chartPanel, new RowData(1, 1));

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

    this.add(this.tablePanel, northData);
    this.add(chartPanel, southData);

    // this.add(this.tablePanel, new RowData(1, 0.7));
    // this.add(chartPanel, new RowData(1, 0.3));
    //
    // this.setLayout(new RowLayout(Orientation.VERTICAL));
    // this.setHeaderVisible(false);
    // this.setBorders(false);
  }

  @Override
  public CallbackDisplay getPatternDisplay() {
    return this.dataCallback;
  }
}
