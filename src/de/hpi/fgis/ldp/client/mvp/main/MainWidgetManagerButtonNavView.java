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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;

import de.hpi.fgis.ldp.client.mvp.ProLODScreen;
import de.hpi.fgis.ldp.client.mvp.main.MainWidgetManagerPresenter.NavigationPoint;
import de.hpi.fgis.ldp.client.service.AbstractCallbackDisplay;

public class MainWidgetManagerButtonNavView extends ContentPanel implements
    MainWidgetManagerPresenter.Display {
  private final ProLODScreen defaultView = new ProLODScreen();

  private final AbstractCallbackDisplay ownCallback;

  // private ContentPanel navPanel;

  protected final ContentPanel contentPanel;
  private final ToolBar toolBar;

  private final TreeMap<String, NavigationPoint> categorizedTabs = new TreeMap<String, NavigationPoint>();

  protected final int buttonHeight = 30;

  public MainWidgetManagerButtonNavView() {
    this.setHeaderVisible(false);
    this.setBorders(false);
    this.setLayout(new RowLayout());

    ContentPanel navPanel = new ContentPanel();
    navPanel.setHeaderVisible(false);
    navPanel.setBorders(false);
    navPanel.setLayout(new RowLayout());

    toolBar = new ToolBar();

    navPanel.add(this.toolBar, new RowData(1D, 1D));

    this.contentPanel = new ContentPanel();
    this.contentPanel.setHeaderVisible(false);
    this.contentPanel.setBorders(false);
    this.contentPanel.setLayout(new RowLayout());

    this.add(navPanel, new RowData(1D, this.buttonHeight));
    this.add(this.contentPanel, new RowData(1D, 1D));

    ownCallback = new AbstractCallbackDisplay(this) {
      @Override
      public void resetToDefault() {
        this.setContent(defaultView);
      }
    };

    this.setDefaultView();
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
    this.toolBar.removeAll();
    StringBuilder toolTipText = new StringBuilder();
    boolean firstButton = true;
    for (final NavigationPoint navPoint : navPoints) {
      if (navPoint != null) {
        final String[] names = navPoint.getNames();
        Button navButton = new Button();
        navButton.setAutoHeight(true);
        navButton.setText(navPoint.getTitle());

        toolTipText.append("<b>").append(navPoint.getCategory()).append("</b><br/>");
        for (final String name : names) {
          toolTipText.append(name).append("<br/>");
        }

        navButton.setToolTip(new ToolTipConfig(navPoint.getCategory(), toolTipText.toString()));
        navButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
          @Override
          public void componentSelected(ButtonEvent ce) {
            setContent(navPoint.getWidget());
          }
        });
        if (!firstButton) {
          firstButton = false;
          this.toolBar.add(new Label(">>"));
        }
        this.toolBar.add(navButton);

        result.add(navPoint);

        categorizedTabs.put(navPoint.getCategory(), navPoint);
      }
    }
    this.setContent(result.get(result.size() - 1).getWidget());

    return result.toArray(new NavigationPoint[result.size()]);
  }

  protected void setContent(Widget widget) {
    this.contentPanel.removeAll();
    this.contentPanel.add(widget, new RowData(1D, 1D));
    this.layout(true);
  }

  @Override
  public void setDefaultView() {
    this.setContent(this.defaultView);
  }

  @Override
  public boolean selectNavigationPoint(String category) {
    NavigationPoint activeItem = categorizedTabs.get(category);
    if (activeItem != null) {
      setContent(activeItem.getWidget());
      return true;
    }
    return false;
  }
}
