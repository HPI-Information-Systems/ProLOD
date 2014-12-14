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

package de.hpi.fgis.ldp.client.view.datatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

import de.hpi.fgis.ldp.shared.data.table.IDataColumn;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

@SuppressWarnings("boxing")
public class DataTablePanel extends ContentPanel {

  protected IDataTable table = null;
  protected String autoExpandColumnSelection = null;
  protected int visibleElementCount = -1;

  protected Map<String, ToolButton> buttons = new HashMap<String, ToolButton>();
  protected MultiSelectionListener multiSelectionListener = null;
  protected SingleSelectionListener singleSelectionListener = null;

  public interface SingleSelectionListener {
    public void onSelection(int selectedRow);
  }

  public interface MultiSelectionListener {
    public void onSelection(int[] selectedRows);
  }

  public void setSelectionListenter(MultiSelectionListener listener) {
    this.multiSelectionListener = listener;
  }

  public void setSelectionListenter(SingleSelectionListener listener) {
    this.singleSelectionListener = listener;
  }

  public DataTablePanel() {
    this.setHeaderVisible(true);
    this.setBorders(false);
    this.setBodyBorder(false);
    this.setButtonAlign(HorizontalAlignment.CENTER);
    this.setLayout(new RowLayout());

  }

  public DataTablePanel setData(IDataTable table) {
    return this.setData(table, -1);
  }

  public IDataTable getData() {
    return this.table;
  }

  public DataTablePanel setData(IDataTable table, int visibleElementCount) {
    this.table = table;

    if (visibleElementCount <= 0) {
      this.refreshTableView(0, table.getRowCount());
    } else {
      this.refreshTableView(0, visibleElementCount - 1);
    }

    return this;
  }

  public DataTablePanel setAutoExpandColumn(String autoExpandColumn) {
    this.autoExpandColumnSelection = autoExpandColumn;

    return this;
  }

  protected void refreshTableView(final int start, final int end) {
    // remove entries
    this.removeAll();

    List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

    ArrayList<Integer> maxCellSize = new ArrayList<Integer>();

    final ColumnConfig rowIndexColumn = new ColumnConfig();
    rowIndexColumn.setId("RowIDX");
    rowIndexColumn.setHidden(true);
    configs.add(rowIndexColumn);

    boolean containsAutoExpandColumnSelection = false;
    for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
      containsAutoExpandColumnSelection |=
          this.autoExpandColumnSelection != null
              && this.autoExpandColumnSelection.equals(table.getColumn(columnIndex).getLabel());
      ColumnConfig column = new ColumnConfig();
      column.setId(table.getColumn(columnIndex).getLabel());
      column.setHeader(table.getColumn(columnIndex).getLabel());
      column.setHidden(!table.getColumn(columnIndex).isVisible());
      maxCellSize.add(Math.max(50, table.getColumn(columnIndex).getLabel().length() * 10));
      // TODO type dependent formats?
      // column.setNumberFormat(NumberFormat.getFormat("0.00#####;-0.00#####"));

      configs.add(column);
    }

    // final StringUtil util = StringUtil.getInstance();
    ListStore<BaseModelData> store = new ListStore<BaseModelData>();
    for (int rowIndex = start; rowIndex < table.getRowCount() && rowIndex <= end; rowIndex++) {
      final BaseModelData model = new BaseModelData();
      model.set(rowIndexColumn.getId(), rowIndex);
      for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
        final IDataColumn<?> column = table.getColumn(columnIndex);
        Object element = column.getElement(rowIndex);

        if (element != null) {
          if (element instanceof String) {
            // element = util.removeHTMLTags((String) element);
            maxCellSize.set(columnIndex,
                Math.max(maxCellSize.get(columnIndex), ((String) element).length() * 10));
          } else if (element instanceof Double) {
            if (maxCellSize.get(columnIndex) < 100) {
              maxCellSize.set(columnIndex, 100);
            }
          } else if (!(element instanceof Number)) {
            // for normal instances (not numbers) we'll use the
            // toString method
            maxCellSize.set(columnIndex,
                Math.max(maxCellSize.get(columnIndex), element.toString().length() * 10));
          }
        }

        model.set(column.getLabel(), element);
      }
      store.add(model);
    }

    String actualAutoExpandColumn = null;
    if (containsAutoExpandColumnSelection) {
      actualAutoExpandColumn = this.autoExpandColumnSelection;
    }
    for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
      final int currentSize = maxCellSize.get(columnIndex);
      if (currentSize < 500) {
        configs.get(columnIndex + 1).setWidth(currentSize);
      } else {
        configs.get(columnIndex + 1).setWidth(500);
        if (actualAutoExpandColumn == null) {
          actualAutoExpandColumn = table.getColumn(columnIndex).getLabel();
        }
      }
    }

    ColumnModel cm = new ColumnModel(configs);

    final Grid<BaseModelData> grid = new Grid<BaseModelData>(store, cm);
    grid.setHeight("100%");

    // set the auto expand column
    if (actualAutoExpandColumn != null) {
      grid.setAutoExpandColumn(actualAutoExpandColumn);
    }

    // remove tools
    while (this.getHeader().getToolCount() > 0) {
      this.getHeader().removeTool(this.getHeader().getTool(0));
    }

    if (this.multiSelectionListener != null) {
      final ToolButton button = new ToolButton("x-tool-search");

      button.addSelectionListener(new SelectionListener<IconButtonEvent>() {
        @Override
        public void componentSelected(IconButtonEvent ce) {
          List<BaseModelData> selectedItems = grid.getSelectionModel().getSelectedItems();

          if (selectedItems.size() < 1) {
            return;
          }
          int[] selectedRows = new int[selectedItems.size()];
          int i = 0;
          for (BaseModelData selectedItem : selectedItems) {
            selectedRows[i++] = selectedItem.<Integer>get(rowIndexColumn.getId());
          }

          DataTablePanel.this.multiSelectionListener.onSelection(selectedRows);
        }

      });

      this.getHeader().addTool(button);

    } else if (this.singleSelectionListener != null) {
      grid.getSelectionModel().addSelectionChangedListener(
          new SelectionChangedListener<BaseModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<BaseModelData> se) {
              BaseModelData selectedItem = grid.getSelectionModel().getSelectedItem();

              if (selectedItem == null) {
                return;
              }
              final int selectedRow = selectedItem.<Integer>get(rowIndexColumn.getId());
              DataTablePanel.this.singleSelectionListener.onSelection(selectedRow);
            }

          });
    }

    for (ToolButton currentButton : this.buttons.values()) {
      this.getHeader().addTool(currentButton);
    }

    if (this.visibleElementCount > 0) {
      // add new tools
      this.setLeftButton(start);
      this.setRightButton(end);

    }

    this.add(grid, new RowData(1, 1));
    // TODO recalc sizes of members
    // this.setLayout(new RowLayout());

    this.layout();
  }

  public void setToolButton(String toolButtonType, SelectionListener<IconButtonEvent> listener) {
    ToolButton newButton = null;
    newButton = new ToolButton(toolButtonType);
    newButton.addSelectionListener(listener);
    this.buttons.put(toolButtonType, newButton);
    this.getHeader().addTool(newButton);
  }

  private void setLeftButton(final int start) {
    this.setToolButton("x-tool-left", new SelectionListener<IconButtonEvent>() {
      @Override
      public void componentSelected(IconButtonEvent ce) {
        final int newStart = Math.max(0, start - DataTablePanel.this.visibleElementCount);
        refreshTableView(newStart, newStart + DataTablePanel.this.visibleElementCount);
      }

    });
  }

  private void setRightButton(final int end) {
    this.setToolButton("x-tool-right", new SelectionListener<IconButtonEvent>() {
      @Override
      public void componentSelected(IconButtonEvent ce) {
        final int newEnd =
            Math.min(DataTablePanel.this.table.getRowCount(), end
                + DataTablePanel.this.visibleElementCount);
        refreshTableView(end + 1, newEnd);
      }
    });
  }

  public void setTableTitle(String tableTitle) {
    this.setHeading(tableTitle);
  }
}
