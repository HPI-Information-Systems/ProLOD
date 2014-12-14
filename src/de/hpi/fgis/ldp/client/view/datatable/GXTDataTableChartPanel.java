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

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Text;
import com.extjs.gxt.charts.client.model.ToolTip;
import com.extjs.gxt.charts.client.model.ToolTip.MouseStyle;
import com.extjs.gxt.charts.client.model.axis.Label;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.HorizontalBarChart;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.charts.client.model.charts.SketchBarChart;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

import de.hpi.fgis.ldp.client.gin.ProLODClientModule;
import de.hpi.fgis.ldp.client.util.StringUtil;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

public class GXTDataTableChartPanel extends AbstractDataTableChartPanel {
  protected ChartModel model;

  // TODO inject --> gui.gxt.chartURL
  protected String swfURL = ProLODClientModule.OPEN_FLASH_CHART_URL;

  // TODO inject --> gui.maxPieChartElements
  protected int maxPieElementCount = ProLODClientModule.MAX_PIECHART_ENTRY_COUNT;

  // TODO inject --> gui.maxBarChartElements
  protected int maxBarElementCount = ProLODClientModule.MAX_BARCHART_ENTRY_COUNT;

  private final String[] colors = new String[] {"#0033CC", "#FF0000", "#FF9900", "#008000",
      "#FE80DF", "#000080", "#33CC00", "#FF00FF", "#00FF99", "#800000", "#80FEDF", "#808000",
      "#CC3380", "#3380CC", "#80FF33"};
  private final String defaultColor = "#888888";

  public static enum ChartType {
    Pie, Bar, HBar
  }

  protected GXTDataTableChartPanel() {}

  // public GXTDataTableChartPanel(IDataTable table, int nameColumnIndex, int
  // valueColumnIndex) {
  // this(ChartType.HBar, table, nameColumnIndex, valueColumnIndex, -1);
  // }
  public GXTDataTableChartPanel(ChartType type, IDataTable table, int nameColumnIndex,
      int valueColumnIndex) {
    this(type, table, nameColumnIndex, valueColumnIndex, -1);
  }

  // public GXTDataTableChartPanel(IDataTable table, int nameColumnIndex, int
  // valueColumnIndex, int percentageColumnIndex) {
  // this(ChartType.HBar, table, nameColumnIndex, valueColumnIndex,
  // percentageColumnIndex);
  // }

  public GXTDataTableChartPanel(ChartType type, IDataTable table, int nameColumnIndex,
      int valueColumnIndex, int percentageColumnIndex) {
    this.setHeaderVisible(false);
    this.setBorders(false);

    switch (type) {
      case Bar:
        this.setBarChartModel(table, nameColumnIndex, valueColumnIndex, percentageColumnIndex);
        break;
      case HBar:
        this.setHorizontalBarChartModel(table, nameColumnIndex, valueColumnIndex,
            percentageColumnIndex);
        break;
      case Pie:
      default:
        this.setPieChartModel(table, nameColumnIndex, valueColumnIndex, percentageColumnIndex);
        break;
    }

    this.refreshView();
  }

  private void setPieChartModel(IDataTable table, int nameColumnIndex, int valueColumnIndex,
      int percentageColumnIndex) {
    // graph
    PieChart config = new PieChart();
    // config.setStartAngle(Integer.valueOf(270));
    config.setAlpha(Float.valueOf(0.75f));
    final String[] chartColors = new String[this.maxPieElementCount];
    for (int i = 0; i < chartColors.length - 1; i += this.colors.length) {

      System.arraycopy(this.colors, 0, chartColors, 0,
          Math.min(this.colors.length, chartColors.length - i - 1));
    }
    chartColors[this.maxPieElementCount - 1] = this.defaultColor;

    config.setColours(chartColors);
    config.setTooltip("#label#<br>#val#<br>#percent#");

    boolean addOtherColumn = true;
    int numElementsToAdd = this.maxPieElementCount - 1;

    // table content
    if (table.getRowCount() <= this.maxPieElementCount) {
      numElementsToAdd = table.getRowCount();
      addOtherColumn = false;
    }

    double percentageSum = 0;
    double valueSum = 0;
    for (int rowIndex = 0; rowIndex < numElementsToAdd; rowIndex++) {
      Object currentValue = table.getColumn(valueColumnIndex).getElement(rowIndex);

      double value = 0;
      if (currentValue instanceof Number) {
        value = ((Number) currentValue).doubleValue();
      }

      // shorten names if they are urls
      final String name =
          StringUtil.getInstance().shortenURL(
              table.getColumn(nameColumnIndex).getElement(rowIndex).toString());

      // set value and name
      config.addSlice(Double.valueOf(value), name);

      if (percentageColumnIndex >= 0) {
        Object currentPercentage = table.getColumn(percentageColumnIndex).getElement(rowIndex);

        valueSum += ((Number) currentValue).intValue();

        double percentage = 0;
        if (currentPercentage instanceof Number) {
          percentage = ((Number) currentPercentage).doubleValue();
        }

        percentageSum += percentage;
      }
    }
    if (addOtherColumn) {
      final double relativeFrequency = (percentageSum / 100.0);
      config.addSlice(Double.valueOf(valueSum / relativeFrequency * (1 - relativeFrequency)),
          "Other");
    }

    this.model = new ChartModel();
    // alpha 0% for background doesn't work @ firefox
    // this.model.setBackgroundColour("-1");
    this.model.setBackgroundColour("#FFFFFF");

    /*
     * config.setNoLabels(true); Legend lg = new Legend(Position.RIGHT, true); lg.setPadding(10);
     * this.model.setLegend(lg); //
     */

    this.model.addChartConfig(config);
    this.model.setTooltipStyle(new ToolTip(MouseStyle.FOLLOW));
  }

  private void setHorizontalBarChartModel(IDataTable table, int nameColumnIndex,
      int valueColumnIndex, int percentageColumnIndex) {
    // graph
    HorizontalBarChart config = new HorizontalBarChart();
    config.setTooltip("##val#");

    boolean addOtherColumn = true;
    int numElementsToAdd = this.maxBarElementCount - 1;

    // table content
    if (table.getRowCount() <= this.maxBarElementCount) {
      numElementsToAdd = table.getRowCount();
      addOtherColumn = false;
    }

    double percentageSum = 0;
    double valueSum = 0;
    ArrayList<HorizontalBarChart.Bar> bars = new ArrayList<HorizontalBarChart.Bar>();
    ArrayList<String> labels = new ArrayList<String>();

    Double min = Double.MAX_VALUE;
    Double max = -Double.MAX_VALUE;

    for (int rowIndex = 0; rowIndex < numElementsToAdd; rowIndex++) {
      Object currentValue = table.getColumn(valueColumnIndex).getElement(rowIndex);

      double value = 0;
      if (currentValue instanceof Number) {
        value = ((Number) currentValue).doubleValue();
        if (value < min) {
          min = value;
        }
        if (value > max) {
          max = value;
        }
      }

      // shorten names if they are urls
      final String name =
          StringUtil.getInstance().shortenURL(
              table.getColumn(nameColumnIndex).getElement(rowIndex).toString());

      // set value and name (&alpha)
      final HorizontalBarChart.Bar bar =
          new HorizontalBarChart.Bar(Double.valueOf(value), this.colors[rowIndex
              % this.colors.length]);
      bar.set("alpha", Float.valueOf(0.75f));
      bars.add(bar);
      labels.add(name);

      if (percentageColumnIndex >= 0) {
        Object currentPercentage = table.getColumn(percentageColumnIndex).getElement(rowIndex);

        valueSum += ((Number) currentValue).intValue();

        double percentage = 0;
        if (currentPercentage instanceof Number) {
          percentage = ((Number) currentPercentage).doubleValue();
        }

        percentageSum += percentage;
      }
    }
    if (addOtherColumn) {
      final double relativeFrequency = (percentageSum / 100.0);
      final double value = valueSum / relativeFrequency * (1 - relativeFrequency);
      final HorizontalBarChart.Bar bar =
          new HorizontalBarChart.Bar(Double.valueOf(value), this.defaultColor);
      bar.set("alpha", Float.valueOf(0.75f));
      bars.add(bar);

      if (value < min) {
        min = value;
      }
      if (value > max) {
        max = value;
      }
      labels.add("Other");
    }

    config.addBars(bars);

    this.model = new ChartModel();
    // alpha 0% for background doesn't work @ firefox
    // this.model.setBackgroundColour("-1");
    this.model.setBackgroundColour("#FFFFFF");

    YAxis yAxis = new YAxis();
    for (int i = labels.size() - 1; i >= 0; i--) {
      yAxis.addLabels(labels.get(i));
    }
    yAxis.setOffset(true);
    yAxis.setColour("#555555");
    yAxis.setGridColour("#DDDDDD");
    this.model.setYAxis(yAxis);

    XAxis xAxis = new XAxis();

    if (min > 0.0) {
      min = 0.0;
    } else if (max < 0.0) {
      max = 0.0;
    }

    final double factor = 5.0;
    final double step = Math.ceil((max - min) / factor);
    if (min == 0) {
      max = step * factor;
    } else if (max == 0) {
      min = step * -1 * factor;
    }
    xAxis.setRange(Double.valueOf(min), Double.valueOf(max), Double.valueOf(step));
    xAxis.setColour("#555555");
    xAxis.setGridColour("#DDDDDD");
    this.model.setXAxis(xAxis);

    this.model.addChartConfig(config);
    this.model.setTooltipStyle(new ToolTip(MouseStyle.FOLLOW));
  }

  private void setBarChartModel(IDataTable table, int nameColumnIndex, int valueColumnIndex,
      int percentageColumnIndex) {
    // graph
    SketchBarChart config = new SketchBarChart();
    // BarChart config = new BarChart();

    config.setTooltip("##val#");

    boolean addOtherColumn = true;
    int numElementsToAdd = this.maxBarElementCount - 1;

    // table content
    if (table.getRowCount() <= this.maxBarElementCount) {
      numElementsToAdd = table.getRowCount();
      addOtherColumn = false;
    }

    double percentageSum = 0;
    double valueSum = 0;
    ArrayList<BarChart.Bar> bars = new ArrayList<BarChart.Bar>();
    ArrayList<Label> labels = new ArrayList<Label>();

    Double min = Double.MAX_VALUE;
    Double max = -Double.MAX_VALUE;

    for (int rowIndex = 0; rowIndex < numElementsToAdd; rowIndex++) {
      Object currentValue = table.getColumn(valueColumnIndex).getElement(rowIndex);

      double value = 0;
      if (currentValue instanceof Number) {
        value = ((Number) currentValue).doubleValue();
        if (value < min) {
          min = value;
        }
        if (value > max) {
          max = value;
        }
      }

      // shorten names if they are urls
      final String name =
          StringUtil.getInstance().shortenURL(
              table.getColumn(nameColumnIndex).getElement(rowIndex).toString());

      // set value and name
      bars.add(new BarChart.Bar(Double.valueOf(value), this.colors[rowIndex % this.colors.length]));
      labels.add(new Label(name));

      if (percentageColumnIndex >= 0) {
        Object currentPercentage = table.getColumn(percentageColumnIndex).getElement(rowIndex);

        valueSum += ((Number) currentValue).intValue();

        double percentage = 0;
        if (currentPercentage instanceof Number) {
          percentage = ((Number) currentPercentage).doubleValue();
        }

        percentageSum += percentage;
      }
    }
    if (addOtherColumn) {
      final double relativeFrequency = (percentageSum / 100.0);
      final double value = valueSum / relativeFrequency * (1 - relativeFrequency);
      bars.add(new BarChart.Bar(Double.valueOf(value), this.defaultColor));

      if (value < min) {
        min = value;
      }
      if (value > max) {
        max = value;
      }
      labels.add(new Label("Other"));
    }

    config.addBars(bars);

    this.model = new ChartModel();
    // alpha 0% for background doesn't work @ firefox
    // this.model.setBackgroundColour("-1");
    this.model.setBackgroundColour("#FFFFFF");

    XAxis xAxis = new XAxis();
    xAxis.addLabels(labels);
    this.model.setXAxis(xAxis);

    YAxis yAxis = new YAxis();

    if (min > 0.0) {
      min = 0.0;
    } else if (max < 0.0) {
      max = 0.0;
    }
    final double factor = 10.0;
    final double step = Math.ceil((max - min) / factor);
    if (min == 0) {
      max = step * factor;
    } else if (max == 0) {
      min = step * -1 * factor;
    }
    yAxis.setRange(Double.valueOf(min), Double.valueOf(max), Double.valueOf(step));
    this.model.setYAxis(yAxis);

    this.model.addChartConfig(config);
    this.model.setTooltipStyle(new ToolTip(MouseStyle.FOLLOW));
  }

  protected void refreshView() {
    this.removeAll();

    Chart piechart = new Chart(swfURL);

    piechart.setChartModel(this.model);

    piechart.setAutoHeight(true);
    piechart.setAutoWidth(true);

    this.setBodyBorder(false);
    this.setCollapsible(false);
    this.add(piechart, new RowData(1, 1));
    this.setLayout(new RowLayout());
    this.setScrollMode(Scroll.AUTO);

    this.layout();
  }

  @Override
  public void setChartTitle(String chartTitle) {
    this.model.setTitle(new Text(chartTitle));
    this.refreshView();
  }

  @Override
  public String getChartTitle() {
    return this.model.getTitle().getText();
  }
}
