/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import com.extjs.gxt.ui.client.core.DomQuery;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.RowExpanderEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.google.gwt.user.client.Element;

/**
 * A <code>ColumnConfig</li> subclass and a <code>ComponentPlgin</code> that
 * adds the ability for each row to be expanded, showing custom content that
 * spans all the rows columns.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>BeforeExpand</b> : RowExpanderEvent(rowExpander, model, rowIndex,
 * bodyElement)<br>
 * <div>Fires before a row is expanded. Listeners can cancel the action by
 * calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>rowExpander : this</li>
 * <li>model : the model</li>
 * <li>rowIndex : the row index</li>
 * <li>bodyElement : the body element</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Expand</b> : RowExpanderEvent(rowExpander, model, rowIndex,
 * bodyElement)<br>
 * <div>Fires after a row is expanded.</div>
 * <ul>
 * <li>rowExpander : this</li>
 * <li>model : the model</li>
 * <li>rowIndex : the row index</li>
 * <li>bodyElement : the body element</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>BeforeCollapse</b> : RowExpanderEvent(rowExpander, model, rowIndex,
 * bodyElement)<br>
 * <div>Fires before a row is collapsed. Listeners can cancel the action by
 * calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>rowExpander : this</li>
 * <li>model : the model</li>
 * <li>rowIndex : the row index</li>
 * <li>bodyElement : the body element</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Collapse</b> : RowExpanderEvent(rowExpander, model, rowIndex,
 * bodyElement)<br>
 * <div>Fires after a row is collapsed.</div>
 * <ul>
 * <li>rowExpander : this</li>
 * <li>model : the model</li>
 * <li>rowIndex : the row index</li>
 * <li>bodyElement : the body element</li>
 * </ul>
 * </dd>
 * 
 * </dl>
 */
public class RowExpander extends ColumnConfig implements ComponentPlugin {

  protected Grid<?> grid;
  private XTemplate template;

  /**
   * Creates a new row expander.
   */
  public RowExpander() {
    setHeader("");
    setWidth(20);
    setSortable(false);
    setResizable(false);
    setFixed(true);
    setMenuDisabled(true);
    setDataIndex("");
    setId("expander");

    setRenderer(new GridCellRenderer<ModelData>() {
      public String render(ModelData model, String property, ColumnData d, int rowIndex, int colIndex,
          ListStore<ModelData> store, Grid<ModelData> grid) {
        d.cellAttr = "rowspan='2'";
        return "<div class='x-grid3-row-expander'>&#160;</div>";
      }
    });

  }

  /**
   * Creates a new row expander with the given template.
   * 
   * @param template the template
   */
  public RowExpander(XTemplate template) {
    this();
    setTemplate(template);
  }

  /**
   * Collapses the given row.
   * 
   * @param rowIndex the rowIndex
   */
  public void collapseRow(int rowIndex) {
    El row = new El((Element)grid.getView().getRow(rowIndex));
    collapseRow(row);
  }

  /**
   * Expands the given row.
   * 
   * @param rowIndex the row index
   */
  public void expandRow(int rowIndex) {
    El row = new El((Element)grid.getView().getRow(rowIndex));
    expandRow(row);
  }
  
  /**
   * Returns the source grid.
   * 
   * @return the source grid
   */
  public Grid<?> getGrid() {
    return grid;
  }

  /**
   * Returns the template.
   * 
   * @return the template
   */
  public XTemplate getTemplate() {
    return template;
  }
  
  @SuppressWarnings("unchecked")
  public void init(Component component) {
    this.grid = (Grid) component;

    GridView view = grid.getView();

    final GridViewConfig config = view.getViewConfig();
    view.viewConfig = new GridViewConfig() {
      @Override
      public String getRowStyle(ModelData model, int rowIndex, ListStore ds) {
        String s = "x-grid3-row-collapsed";
        if (config != null) {
          return s + " " + config.getRowStyle(model, rowIndex, ds);
        } else {
          return s;
        }
      }
    };

    view.enableRowBody = true;

    grid.addListener(Events.RowClick, new Listener<GridEvent>() {
      public void handleEvent(GridEvent be) {
        onMouseDown(be);
      }

    });
  }
  
  /**
   * Sets the template.
   * 
   * @param template the template
   */
  public void setTemplate(XTemplate template) {
    this.template = template;
  }

  protected boolean beforeExpand(ModelData model, Element body, El row, int rowIndex) {
    RowExpanderEvent e = new RowExpanderEvent(this);
    e.setModel(model);
    e.setRowIndex(rowIndex);
    e.setBodyElement(body);

    if (fireEvent(Events.BeforeExpand, e)) {
      body.setInnerHTML(getBodyContent(model, rowIndex));
      return true;
    }
    return false;
  }

  protected void collapseRow(El row) {
    int idx = row.dom.getPropertyInt("rowIndex");
    ModelData model = grid.getStore().getAt(idx);
    Element body = DomQuery.selectNode("div.x-grid3-row-body", row.dom);
    
    RowExpanderEvent e = new RowExpanderEvent(this);
    e.setModel(model);
    e.setRowIndex(idx);
    e.setBodyElement(body);
    
    if (fireEvent(Events.BeforeCollapse, e)) {
      row.replaceStyleName("x-grid3-row-expanded", "x-grid3-row-collapsed");

      fireEvent(Events.Collapse, e);
    }
  }

  protected void expandRow(El row) {
    int idx = row.dom.getPropertyInt("rowIndex");
    ModelData model = grid.getStore().getAt(idx);
    Element body = DomQuery.selectNode("div.x-grid3-row-body", row.dom);
    if (beforeExpand(model, body, row, idx)) {
      row.replaceStyleName("x-grid3-row-collapsed", "x-grid3-row-expanded");
      RowExpanderEvent e = new RowExpanderEvent(this);
      e.setModel(model);
      e.setRowIndex(idx);
      e.setBodyElement(body);
      fireEvent(Events.Expand, e);
    }
  }

  protected String getBodyContent(ModelData model, int rowIndex) {
    return template.applyTemplate(Util.getJsObject(model, template.getMaxDepth()));
  }

  protected void onMouseDown(GridEvent<?> e) {
    if (e.getTarget().getClassName().equals("x-grid3-row-expander")) {
      e.stopEvent();
      El row = e.getTarget(".x-grid3-row", 15);
      toggleRow(row);
    }
  }

  protected void toggleRow(El row) {
    if (row.hasStyleName("x-grid3-row-collapsed")) {
      expandRow(row);
    } else {
      collapseRow(row);
    }
    grid.getView().calculateVBar(false);
  }

}
