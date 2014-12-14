/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.DomQuery;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ColumnHeaderEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.util.Region;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.tips.QuickTip;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

/**
 * ColumnHeader Component.
 */
public class ColumnHeader extends BoxComponent {

  public class GridSplitBar extends BoxComponent {

    protected int colIndex;
    protected Draggable d;
    protected boolean dragging;
    protected DragListener listener = new DragListener() {

      @Override
      public void dragEnd(DragEvent de) {
        onDragEnd(de);
      }

      @Override
      public void dragStart(DragEvent de) {
        onDragStart(de);
      }

    };
    protected int startX;

    protected void onDragEnd(DragEvent e) {
      dragging = false;
      headerDisabled = false;
      setStyleAttribute("borderLeft", "none");
      el().setStyleAttribute("opacity", "0");
      el().setWidth(splitterWidth);
      bar.el().setVisibility(false);

      int endX = e.getX();
      int diff = endX - startX;
      onColumnSplitterMoved(colIndex, cm.getColumnWidth(colIndex) + diff);
    }

    protected void onDragStart(DragEvent e) {
      headerDisabled = true;
      dragging = true;
      setStyleAttribute("borderLeft", "1px solid black");
      setStyleAttribute("cursor", "default");
      el().setStyleAttribute("opacity", "1");
      el().setWidth(1);

      startX = e.getX();

      int cols = cm.getColumnCount();
      for (int i = 0, len = cols; i < len; i++) {
        if (cm.isHidden(i)) continue;
        Element hd = getHead(i).getElement();
        if (hd != null) {
          Region rr = El.fly(hd).getRegion();
          if (startX > rr.right - 5 && startX < rr.right + 5) {
            colIndex = heads.indexOf(getHead(i));
            if (colIndex != -1) break;
          }
        }
      }
      if (colIndex > -1) {
        Element c = getHead(colIndex).getElement();
        int x = startX;
        int minx = x - fly((com.google.gwt.user.client.Element) c).getX() - minColumnWidth;
        int maxx = (container.el().getX() + container.el().getWidth()) - e.getEvent().getClientX();
        d.setXConstraint(minx, maxx);
      }
    }

    protected void onMouseMove(Head header, ComponentEvent ce) {
      int activeHdIndex = heads.indexOf(header);

      if (dragging) {
        return;
      }

      // find the previous column which is not hidden
      int before = activeHdIndex - 1;
      for (int i = activeHdIndex; i >= 0; i--) {
        if (!cm.isHidden(i)) {
          before = i;
          break;
        }
      }
      Event event = ce.getEvent();
      int x = event.getClientX();
      Region r = header.el().getRegion();
      int hw = splitterWidth;

      el().setY(container.el().getY());
      el().setHeight(container.getHeight());

      Style ss = getElement().getStyle();

      if (x - r.left <= hw && cm.isResizable(activeHdIndex - before)) {
        bar.el().setVisibility(true);
        el().setX(r.left);
        ss.setProperty("cursor", GXT.isSafari ? "e-resize" : "col-resize");
      } else if (r.right - x <= hw && cm.isResizable(activeHdIndex)) {
        el().setX(r.right - (hw / 2));
        bar.el().setVisibility(true);
        ss.setProperty("cursor", GXT.isSafari ? "w-resize" : "col-resize");
      } else {
        bar.el().setVisibility(false);
        ss.setProperty("cursor", "");
      }
    }

    @Override
    protected void onRender(com.google.gwt.user.client.Element target, int index) {
      super.onRender(target, index);
      setElement(DOM.createDiv(), target, index);

      if (GXT.isOpera) {
        el().setStyleAttribute("cursor", "w-resize");
      } else {
        el().setStyleAttribute("cursor", "col-resize");
      }
      setStyleAttribute("position", "absolute");
      setWidth(5);

      el().setVisibility(false);
      el().setStyleAttribute("backgroundColor", "white");
      el().setStyleAttribute("opacity", "0");

      d = new Draggable(this);
      d.setUseProxy(false);
      d.setConstrainVertical(true);
      d.setStartDragDistance(0);
      d.addDragListener(listener);
    }
  }

  public class Group extends BoxComponent {

    private HeaderGroupConfig config;

    public Group(HeaderGroupConfig config) {
      this.config = config;
      config.group = this;
      groups.add(this);
    }

    public void setText(String text) {
      el().setInnerHtml(text);
    }

    @Override
    protected void doAttachChildren() {
      ComponentHelper.doAttach(config.getWidget());
    }

    @Override
    protected void doDetachChildren() {
      ComponentHelper.doDetach(config.getWidget());
    }

    @Override
    protected void onRender(Element target, int index) {
      setElement(DOM.createDiv(), target, index);
      setStyleName("x-grid3-hd-inner");

      if (config.getWidget() != null) {
        el().appendChild(config.getWidget().getElement());
      } else {
        el().setInnerHtml(config.getHtml());
      }
    }
  }

  public class Head extends BoxComponent {

    private AnchorElement btn;
    private int column;
    private ImageElement img;
    private Html text;
    private Widget widget;
    protected ColumnConfig config;

    public Head(ColumnConfig column) {
      this.config = column;
      this.column = cm.indexOf(column);
      baseStyle = "x-grid3-hd-inner x-grid3-hd-" + column.getId();
      heads.add(this);
    }

    public void activateTrigger(boolean activate) {
      El e = el().findParent("td", 3);
      if (e != null) {
        e.setStyleName("x-grid3-hd-menu-open", activate);
      }
    }

    public Element getTrigger() {
      return (Element) btn.cast();
    }

    @Override
    public void onComponentEvent(ComponentEvent ce) {
      super.onComponentEvent(ce);

      int type = ce.getEventTypeInt();
      switch (type) {
        case Event.ONMOUSEOVER:
          onMouseOver(ce);
          break;
        case Event.ONMOUSEOUT:
          onMouseOut(ce);
          break;
        case Event.ONMOUSEMOVE:
          onMouseMove(ce);
          break;
        case Event.ONMOUSEDOWN:
          onHeaderMouseDown(ce, cm.indexOf(config));
          break;
        case Event.ONCLICK:
          onClick(ce);
          break;
        case Event.ONDBLCLICK:
          onDoubleClick(ce);
          break;
      }
    }

    public void setHeader(String header) {
      if (text != null) text.setHtml(header);
    }

    public void updateWidth(int width) {
      if (!cm.isHidden(cm.indexOf(config))) {
        El td = el().findParent("td", 3);
        td.setWidth(width);
        el().setWidth(width - td.getFrameWidth("lr"), true);
      }
    }

    private void onClick(ComponentEvent ce) {
      ce.preventDefault();
      if (ce.getTarget() == (Element) btn.cast()) {
        onDropDownClick(ce, column);
      } else {
        onHeaderClick(ce, column);
      }
    }

    private void onDoubleClick(ComponentEvent ce) {
      onHeaderDoubleClick(ce, column);
    }

    private void onMouseMove(ComponentEvent ce) {
      if (bar != null) bar.onMouseMove(this, ce);
    }

    private void onMouseOut(ComponentEvent ce) {
      if (!ce.within(getElement(), true)) {
        el().findParent("td", 3).removeStyleName("x-grid3-hd-over");
      }
    }

    private void onMouseOver(ComponentEvent ce) {
      if (headerDisabled) {
        return;
      }
      if (!cm.isMenuDisabled(indexOf(this))) {
        El td = el().findParent("td", 3);
        td.addStyleName("x-grid3-hd-over");
        int h = td.getHeight(true);
        el().setHeight(h, true);
        if (btn != null) {
          El.fly(btn).setHeight(h, true);
        }
      }
    }

    @Override
    protected void doAttachChildren() {
      super.doAttachChildren();
      ComponentHelper.doAttach(widget);
    }

    @Override
    protected void doDetachChildren() {
      super.doDetachChildren();
      ComponentHelper.doDetach(widget);
    }

    @Override
    protected void onRender(Element target, int index) {
      setElement(DOM.createDiv(), target, index);

      btn = Document.get().createAnchorElement();
      btn.setHref("#");
      btn.setClassName("x-grid3-hd-btn");

      img = Document.get().createImageElement();
      img.setSrc(GXT.BLANK_IMAGE_URL);
      img.setClassName("x-grid3-sort-icon");

      el().dom.appendChild(btn);

      if (config.getWidget() != null) {
        Element span = Document.get().createSpanElement().cast();
        getElement().appendChild(span);

        widget = config.getWidget();
        if (widget instanceof Component) {
          Component c = (Component) widget;
          if (!c.isRendered()) {
            c.render(span);
          } else {
            span.appendChild(c.getElement());
          }

        } else {
          el().dom.appendChild(widget.getElement());
        }
      } else {
        text = new Html(config.getHeader());
        text.setTagName("span");
        text.render(el().dom);
      }

      el().dom.appendChild(img);

      String tip = config.getToolTip();
      if (tip != null) {
        getElement().setAttribute("qtip", tip);
      }

      if (config.getToolTip() != null) new QuickTip(this);

      sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
    }
  }

  private Menu menu;
  protected GridSplitBar bar;
  protected ColumnModel cm;

  protected BoxComponent container;
  protected List<Group> groups = new ArrayList<Group>();
  protected boolean headerDisabled;
  protected List<Head> heads = new ArrayList<Head>();
  protected int minColumnWidth = 10;
  protected int rows;
  protected int splitterWidth = 5;
  protected FlexTable table;

  public ColumnHeader(BoxComponent container, ColumnModel cm) {
    this.container = container;
    this.cm = cm;
    disableTextSelection(true);
  }
  
  /**
   * Returns the header's container component.
   * ì
   * @return the container component
   */
  public BoxComponent getContainer() {
    return container;
  }

  public void enableColumnResizing() {
    if (bar != null) {
      ComponentHelper.doDetach(bar);
      bar.el().remove();
    }
    bar = new GridSplitBar();
    bar.render(container.getElement());
    if (isAttached()) {
      ComponentHelper.doAttach(bar);
    }
  }

  @Override
  public Element getElement() {
    // we need this because of lazy rendering
    return table.getElement();
  }

  public int getMinColumnWidth() {
    return minColumnWidth;
  }

  public int getSplitterWidth() {
    return splitterWidth;
  }

  public int indexOf(Head head) {
    return heads.indexOf(head);
  }

  @Override
  public boolean isAttached() {
    if (table != null) {
      return table.isAttached();
    }
    return false;
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);

    // Delegate events to the widget.
    table.onBrowserEvent(event);
  }

  @SuppressWarnings("unchecked")
  public void refresh() {
    groups.clear();
    heads.clear();

    int cnt = table.getRowCount();
    for (int i = 0; i < cnt; i++) {
      table.removeRow(0);
    }

    table.setWidth(cm.getTotalWidth() + "px");

    List<HeaderGroupConfig> configs = cm.getHeaderGroups();

    FlexCellFormatter cf = table.getFlexCellFormatter();
    RowFormatter rf = table.getRowFormatter();

    rows = 0;
    for (HeaderGroupConfig config : configs) {
      rows = Math.max(rows, config.getRow() + 1);
    }
    rows += 1;

    for (int i = 0; i < rows; i++) {
      rf.setStyleName(i, "x-grid3-hd-row");
    }

    int cols = cm.getColumnCount();

    for (HeaderGroupConfig config : cm.getHeaderGroups()) {
      int col = config.getColumn();
      int row = config.getRow();
      int rs = config.getRowspan();
      int cs = config.getColspan();

      Group group = createNewGroup(config);
      group.render(DOM.createDiv());

      boolean hide = true;
      if (rows > 1) {
        for (int i = col; i < (col + cs); i++) {
          if (!cm.isHidden(i)) {
            hide = false;
          }
        }
      }
      if (hide) {
        continue;
      }

      table.setWidget(row, col, group);
      cf.setStyleName(row, col, "x-grid3-header x-grid3-hd x-grid3-cell");

      HorizontalAlignmentConstant halign = HasHorizontalAlignment.ALIGN_CENTER;
      cf.setHorizontalAlignment(row, col, halign);

      int ncs = cs;
      if (cs > 1) {
        for (int i = col; i < (col + cs); i++) {
          if (cm.isHidden(i)) {
            ncs -= 1;
          }
        }
      }

      cf.setRowSpan(row, col, rs);
      cf.setColSpan(row, col, ncs);
    }

    for (int i = 0; i < cols; i++) {
      Head h = createNewHead(cm.getColumn(i));
      if (cm.isHidden(i)) {
        continue;
      }
      int rowspan = 1;
      if (rows > 1) {
        for (int j = rows - 2; j >= 0; j--) {
          if (!cm.hasGroup(j, i)) {
            rowspan += 1;
          }
        }
      }

      h.render(DOM.createDiv());

      if (rowspan > 1) {
        int r = (rows - 1) - (rowspan - 1);
        table.setWidget(r, i, h);
        table.getFlexCellFormatter().setRowSpan(r, i, rowspan);
        cf.setStyleName(r, i, "x-grid3-header x-grid3-hd x-grid3-cell x-grid3-td-" + cm.getColumnId(i));
      } else {
        table.setWidget(rows - 1, i, h);
        cf.setStyleName(rows - 1, i, "x-grid3-header x-grid3-hd x-grid3-cell x-grid3-td-" + cm.getColumnId(i));
      }
      updateColumnWidth(i, cm.getColumnWidth(i));
    }
    if (container instanceof Grid) {
      Grid grid = (Grid)container;
      SortInfo sortInfo = grid.getStore().getSortState();
      if (sortInfo != null && sortInfo.getSortField() != null) {
        ColumnModel cm = grid.getColumnModel();
        ColumnConfig column = cm.getColumnById(sortInfo.getSortField());
        updateSortIcon(cm.indexOf(column), sortInfo.getSortDir());
      }
    }
    cleanCells();
    if (isAttached()) {
      adjustHeights();
    }
  }

  public void release() {
    ComponentHelper.doDetach(this);
    if (bar != null && bar.isRendered()) {
      bar.el().remove();
    }
  }

  public void setHeader(int column, String header) {
    getHead(column).setHeader(header);
  }

  public void setMenu(Menu menu) {
    this.menu = menu;
  }

  public void setMinColumnWidth(int minColumnWidth) {
    this.minColumnWidth = minColumnWidth;
  }

  public void setSplitterWidth(int splitterWidth) {
    this.splitterWidth = splitterWidth;
  }

  public void updateColumnHidden(int index, boolean hidden) {
    refresh();
    cleanCells();
  }

  public void updateColumnWidth(int column, int width) {
    Head h = getHead(column);
    if (h != null) {
      h.updateWidth(width);
    }
  }

  public void updateSortIcon(int colIndex, SortDir dir) {
    for (int i = 0; i < heads.size(); i++) {
      Head h = heads.get(i);
      if (h.isRendered()) {
        if (i == colIndex) {
          El parent = h.el().findParent("td", 3);
          parent.addStyleName(dir == SortDir.DESC ? "sort-desc" : "sort-asc");
          parent.removeStyleName(dir != SortDir.DESC ? "sort-desc" : "sort-asc");
          // fixes issue with IE initially hiding sort icon on change
          h.el().repaint();
        } else {
          h.el().findParent("td", 3).removeStyleName("sort-asc", "sort-desc");
        }
      }
    }
  }

  public void updateTotalWidth(int offset, int width) {
    if (offset != -1) table.getElement().getParentElement().getStyle().setPropertyPx("width", ++offset);
    table.getElement().getStyle().setProperty("width", (++width) + "px");
  }

  protected void adjustHeights() {
    for (Head head : heads) {
      if (head.isRendered()) {
        int h = head.el().getParent().getHeight();
        if (h > 0) {
          head.setHeight(h);
        }
      }
    }
  }

  protected void cleanCells() {
    NodeList<Element> tds = DomQuery.select("tr.x-grid3-hd-row > td", table.getElement());
    for (int i = 0; i < tds.getLength(); i++) {
      Element td = tds.getItem(i);
      if (!td.hasChildNodes()) {
        El.fly(td).removeFromParent();
      }
    }
  }

  protected ComponentEvent createColumnEvent(ColumnHeader header, int column, Menu menu) {
    return new ColumnHeaderEvent(header, container, column, menu);
  }

  protected Group createNewGroup(HeaderGroupConfig config) {
    return new Group(config);
  }

  protected Head createNewHead(ColumnConfig config) {
    return new Head(config);
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(bar);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(bar);
  }

  protected int getColumnWidths(int start, int end) {
    int w = 0;
    for (int i = start; i < end; i++) {
      if (!cm.isHidden(i)) {
        w += cm.getColumnWidth(i);
      }
    }
    return w;
  }

  protected Menu getContextMenu(int column) {
    return menu;
  }

  protected Head getHead(int column) {
    return column < heads.size() ? heads.get(column) : null;
  }

  @Override
  protected void onAttach() {
    ComponentHelper.doAttach(table);
    DOM.setEventListener(getElement(), this);
    doAttachChildren();
    onLoad();
    adjustHeights();
  }

  protected void onColumnSplitterMoved(int colIndex, int width) {

  }

  @Override
  protected void onDetach() {
    try {
      onUnload();
    } finally {
      ComponentHelper.doDetach(table);
      doDetachChildren();
    }
    onDetachHelper();
  }

  protected void onDropDownClick(ComponentEvent ce, int column) {
    ce.cancelBubble();
    ce.preventDefault();

    menu = getContextMenu(column);

    ComponentEvent ge = createColumnEvent(this, column, menu);
    if (!container.fireEvent(Events.HeaderContextMenu, ge)) {
      return;
    }

    final Head h = ce.getComponent();

    if (menu != null) {
      h.activateTrigger(true);
      menu.addListener(Events.Hide, new Listener<BaseEvent>() {
        public void handleEvent(BaseEvent be) {
          h.activateTrigger(false);
        }
      });
      menu.show(h.getTrigger(), "tl-bl?");
    }
  }

  protected void onHeaderClick(ComponentEvent ce, int column) {
    ComponentEvent evt = createColumnEvent(this, column, menu);
    evt.setEvent(ce.getEvent());
    container.fireEvent(Events.HeaderClick, evt);
  }

  protected void onHeaderDoubleClick(ComponentEvent ce, int column) {
    ComponentEvent evt = createColumnEvent(this, column, menu);
    evt.setEvent(ce.getEvent());
    container.fireEvent(Events.HeaderDoubleClick, evt);
  }

  protected void onHeaderMouseDown(ComponentEvent ce, int column) {
    ComponentEvent evt = createColumnEvent(this, column, menu);
    evt.setEvent(ce.getEvent());
    container.fireEvent(Events.HeaderMouseDown, evt);
  }

  @Override
  protected void onRender(Element target, int index) {
    table = new FlexTable();
    table.setCellPadding(0);
    table.setCellSpacing(0);
    setElement(table.getElement(), target, index);

    List<HeaderGroupConfig> configs = cm.getHeaderGroups();
    rows = 0;
    for (HeaderGroupConfig config : configs) {
      rows = Math.max(rows, config.getRow() + 1);
    }
    rows++;

    new QuickTip(this);

    refresh();
    sinkEvents(Event.ONMOUSEMOVE | Event.ONMOUSEDOWN | Event.ONCLICK);
  }
}
