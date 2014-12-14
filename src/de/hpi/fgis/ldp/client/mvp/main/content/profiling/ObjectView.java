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

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.Widget;

import de.hpi.fgis.ldp.client.service.AbstractCallbackDisplay;
import de.hpi.fgis.ldp.client.service.CallbackDisplay;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel;

public class ObjectView extends ContentPanel implements ObjectPresenter.Display {

  private final ContentPanel tablePanel;

  private DataTablePanel tableTablePanel;

  private final AbstractCallbackDisplay dataCallback;

  public ObjectView() {
    this.tablePanel = new ContentPanel();

    dataCallback = new AbstractCallbackDisplay(tablePanel) {
      @Override
      public void resetToDefault() {
        this.setContent(tableTablePanel);
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

  public void clear() {
    this.tablePanel.removeAll();
    // this.removeAll();
  }

  private void init() {
    this.tablePanel.setHeaderVisible(false);
    this.tablePanel.setBorders(false);
    this.tablePanel.setLayout(new RowLayout());

    this.add(this.tablePanel, new RowData(1D, 1D));

    this.setLayout(new RowLayout());
    this.setHeaderVisible(false);
    this.setBorders(false);
  }

  @Override
  public CallbackDisplay getObjectDisplay() {
    return this.dataCallback;
  }

}
