/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.aria;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.google.gwt.event.dom.client.KeyCodes;

public class FocusManager {

  public static FocusManager get() {
    if (instance == null) {
      instance = new FocusManager();
    }
    return instance;
  }

  private List<FocusHandler> handlers = new ArrayList<FocusHandler>();
  private List<NavigationHandler> navigationHandlers = new ArrayList<NavigationHandler>();

  private BaseEventPreview preview;
  private static FocusManager instance;
  private boolean managed = true;

  private FocusManager() {
    preview = new BaseEventPreview() {
      @Override
      protected void onPreviewKeyPress(PreviewEvent pe) {
        super.onPreviewKeyPress(pe);

        Component c = ComponentManager.get().find(pe.getTarget());
        if (c != null) {
          int key = pe.getKeyCode();
          for (int i = 0; i < handlers.size(); i++) {
            FocusHandler handler = handlers.get(i);
            if (handler.canHandleKeyPress(c, pe)) {
              switch (key) {
                case KeyCodes.KEY_TAB:
                  handler.onTab(c, pe);
                  break;
                case KeyCodes.KEY_LEFT:
                  handler.onLeft(c, pe);
                  break;
                case KeyCodes.KEY_RIGHT:
                  handler.onRight(c, pe);
                  break;
                case KeyCodes.KEY_ESCAPE:
                  handler.onEscape(c, pe);
                  break;
                case KeyCodes.KEY_ENTER:
                  handler.onEnter(c, pe);
                  break;
              }
              return;
            }
          }
        }
      }

    };
    preview.setAutoHide(false);
    initHandlers();
  }

  public void disable() {
    preview.remove();
  }

  public void enable() {
    preview.add();
  }

  public NavigationHandler findNavigationHandler(Component comp) {
    for (int i = 0, len = navigationHandlers.size(); i < len; i++) {
      NavigationHandler h = navigationHandlers.get(i);
      if (h.canHandleTabKey(comp)) {
        return h;
      }
    }
    return null;
  }

  public boolean isManaged() {
    return managed;
  }

  public void register(NavigationHandler handler) {
    navigationHandlers.add(handler);
  }

  public void register(FocusHandler handler) {
    if (!handlers.contains(handler)) {
      handlers.add(handler);
    }
  }

  public void setManaged(boolean managed) {
    this.managed = managed;
  }

  public void unregister(NavigationHandler handler) {
    navigationHandlers.remove(handler);
  }

  public void unregister(FocusHandler handler) {
    handlers.remove(handler);
  }

  protected void initHandlers() {
    register(new ButtonBarHandler());
    register(new MenuHandler());
    register(new TabPanelHandler());
    register(new InputSliderHandler());
    register(new FieldHandler());
    register(new HeaderHandler());
    register(new ContentPanelHandler());
    // always last
    register(new DefaultHandler());

    register(new ContentPanelNavigationHandler());

  }

}
