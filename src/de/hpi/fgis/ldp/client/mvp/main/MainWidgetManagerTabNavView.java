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

package de.hpi.fgis.ldp.client.mvp.main;

import java.util.ArrayList;
import java.util.TreeMap;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.user.client.ui.Widget;

import de.hpi.fgis.ldp.client.mvp.ProLODScreen;
import de.hpi.fgis.ldp.client.mvp.main.MainWidgetManagerPresenter.NavigationPoint;
import de.hpi.fgis.ldp.client.service.AbstractCallbackDisplay;

public class MainWidgetManagerTabNavView extends ContentPanel implements
    MainWidgetManagerPresenter.Display {
  private final ProLODScreen defaultView = new ProLODScreen();

  private final AbstractCallbackDisplay ownCallback;

  protected final TabPanel contentPanels;

  private final TreeMap<String, TabItem> categorizedTabs = new TreeMap<String, TabItem>();

  public MainWidgetManagerTabNavView() {
    this.setHeaderVisible(false);
    this.setBorders(false);
    this.setLayout(new RowLayout());

    this.setDefaultView();

    contentPanels = new TabPanel();
    contentPanels.setSize(600, 250);
    contentPanels.setMinTabWidth(200);
    contentPanels.setResizeTabs(true);
    contentPanels.setAnimScroll(true);
    contentPanels.setTabScroll(true);
    contentPanels.setCloseContextMenu(true);

    ownCallback = new AbstractCallbackDisplay(this) {
      @Override
      public void resetToDefault() {
        this.setContent(defaultView);
      }
    };
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void startProcessing() {
    ownCallback.startProcessing();
  }

  @Override
  public void stopProcessing() {
    ownCallback.stopProcessing();
  }

  @Override
  public void displayError() {
    ownCallback.displayError();
  }

  @Override
  public NavigationPoint[] setNavigationPoints(NavigationPoint... navPoints) {
    ArrayList<NavigationPoint> result = new ArrayList<NavigationPoint>();
    // this.navPanel.removeAll();
    this.removeAll();
    contentPanels.removeAll();
    StringBuilder toolTipText = new StringBuilder();
    for (final NavigationPoint navPoint : navPoints) {
      if (navPoint != null) {
        final String[] names = navPoint.getNames();
        TabItem navItem = new TabItem();

        navItem.setText(navPoint.getTitle());
        toolTipText.append("<b>").append(navPoint.getCategory()).append("</b><br/>");
        for (final String name : names) {
          toolTipText.append(name).append("<br/>");
        }

        navItem.setClosable(false);
        navItem.setLayout(new RowLayout());
        navItem.add(navPoint.getWidget(), new RowData(1D, 1D));
        navItem.addStyleName("pad-text");
        navItem.setAutoWidth(true);
        navItem.setAutoHeight(true);

        categorizedTabs.put(navPoint.getCategory(), navItem);
        contentPanels.add(navItem);
        result.add(navPoint);
      }
    }
    contentPanels.addListener(Events.Select, new Listener<ComponentEvent>() {
      @Override
      public void handleEvent(ComponentEvent be) {
        contentPanels.getSelectedItem().layout(true);
      }
    });
    contentPanels.setSelection(contentPanels.getItem(result.size() - 1));

    this.add(contentPanels, new RowData(1D, 1D));
    this.contentPanels.setToolTip(new ToolTipConfig(toolTipText.toString()));
    this.layout(true);
    return result.toArray(new NavigationPoint[result.size()]);
  }

  @Override
  public void setDefaultView() {
    this.removeAll();
    this.add(this.defaultView, new RowData(1D, 1D));
  }

  @Override
  public boolean selectNavigationPoint(String category) {
    TabItem activeItem = categorizedTabs.get(category);
    if (activeItem != null) {
      contentPanels.setSelection(activeItem);
      return true;
    }
    return false;
  }
}
