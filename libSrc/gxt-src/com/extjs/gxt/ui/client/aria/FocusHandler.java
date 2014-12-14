/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.aria;

import java.util.List;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public abstract class FocusHandler {

  protected static boolean managed;

  public static boolean isManaged() {
    return FocusManager.get().isManaged();
  }

  public abstract boolean canHandleKeyPress(Component component, PreviewEvent pe);

  public void onEnter(Component component, PreviewEvent pe) {

  }

  public void onEscape(Component component, PreviewEvent pe) {
    if (!isManaged()) return;
    stepOut(component);
  }

  public void onLeft(Component component, PreviewEvent pe) {

  }

  public void onRight(Component component, PreviewEvent pe) {

  }

  public void onTab(Component component, PreviewEvent pe) {

  }

  protected NavigationHandler findNavigationHandler(Component comp) {
    return FocusManager.get().findNavigationHandler(comp);
  }

  @SuppressWarnings("unchecked")
  protected void focusNextWidget(Widget c) {
    if (c instanceof Component) {
      Component comp = (Component) c;
      if (comp.getData("aria-next") != null) {
        String id = comp.getData("aria-next");
        Component p = ComponentManager.get().get(id);
        if (p != null) {
          focusWidget(p);
          return;
        }
      }
    }
    Widget p = c.getParent();
    NavigationHandler handler = findNavigationHandler((Component) p);
    if (handler != null) {
      List<Widget> widgets = handler.getOrderedWidgets(p);
      int idx = widgets.indexOf(c);
      if (idx != -1) {
        if (idx == widgets.size() - 1 && widgets.size() > 1) {
          focusWidget(widgets.get(0));
          return;
        } else if (idx != widgets.size() - 1) {
          focusWidget(widgets.get(idx + 1));
          return;
        }
      }
    }

    if (p instanceof Container) {
      Container con = (Container) p;
      int index = con.indexOf((Component) c) + 1;
      if (index == con.getItemCount()) {
        index = 0;
      }
      focusWidget(con.getItem(index));
    } else if (p instanceof ComplexPanel) {
      ComplexPanel panel = (ComplexPanel) p;
      int index = panel.getWidgetIndex(c);
      if (index != -1) {
        if (index == panel.getWidgetCount() + 1) {
          index = 0;
        } else {
          ++index;
        }
        Widget w = panel.getWidget(index);
        focusWidget(w);

      }
    }
  }

  @SuppressWarnings("unchecked")
  protected void focusPreviousWidget(Widget c) {
    if (c instanceof Component) {
      Component comp = (Component) c;
      if (comp.getData("aria-previous") != null) {
        String id = comp.getData("aria-previous");
        Component p = ComponentManager.get().get(id);
        if (p != null) {
          focusWidget(p);
          return;
        }
      }
    }

    Widget p = c.getParent();

    List<Widget> widgets = null;

    NavigationHandler handler = findNavigationHandler((Component) p);
    if (handler != null) {
      widgets = handler.getOrderedWidgets(p);
    } else if (p instanceof Container) {
      Container con = (Container) p;
      widgets = con.getItems();
    } else if (p instanceof HasWidgets) {
      HasWidgets hs = (HasWidgets) p;
      while (hs.iterator().hasNext()) {
        widgets.add(hs.iterator().next());
      }
    }
    int size = widgets.size();
    int idx = widgets.indexOf(c);
    if (idx != -1) {
      if (idx > 0) {
        focusWidget(widgets.get(idx - 1), true);
        return;
      } else if (idx != 0 && size > 1) {
        focusWidget(widgets.get(size - 1), true);
        return;
      } else if (idx == 0 && size > 1) {
        focusWidget(widgets.get(size - 1), true);
        return;
      }
    }
  }

  protected void focusWidget(Widget w) {
    focusWidget(w, true);
  }

  protected void focusWidget(Widget w, boolean forward) {
    if (w instanceof Component) {
      Component c = (Component) w;
      if (c.isAriaIgnore()) {
        if (isContainer(c)) {
          stepInto(c, null, forward);
        } else {
          if (forward) {
            focusNextWidget(c);
          } else {
            focusPreviousWidget(c);
          }

        }
      } else {
        c.focus();
      }
    } else {
      El.fly(w.getElement()).focus();
    }
  }

  protected boolean isContainer(Widget w) {
    if (w instanceof LayoutContainer || w instanceof Panel) {
      return true;
    }
    return false;
  }

  protected void stepInto(Widget w, PreviewEvent pe, boolean forward) {
    if (w instanceof ContentPanel) {
      ContentPanel panel = (ContentPanel) w;
      if (panel.getTopComponent() != null) {
        focusWidget(panel.getTopComponent());
        return;
      }
    }
    if (w instanceof LayoutContainer) {
      if (pe != null) pe.stopEvent();
      LayoutContainer c = (LayoutContainer) w;
      if (c.getItemCount() > 0) {
        focusWidget(forward ? c.getItem(0) : c.getItem(c.getItemCount() - 1));
      }
    } else if (w instanceof ComplexPanel) {
      if (pe != null) pe.stopEvent();
      ComplexPanel panel = (ComplexPanel) w;
      if (panel.getWidgetCount() > 0) {
        focusWidget(panel.getWidget(0));
      }
    }
  }

  protected void stepOut(Widget w) {
    Widget p = w.getParent();
    if (p != null) {
      if (p instanceof TabItem) {
        ((TabItem) p).getTabPanel().focus();
      } else if (p instanceof Component) {
        Component c = (Component) p;
        while (c.isAriaIgnore()) {
          p = c.getParent();
          if (p instanceof Component) {
            c = (Component) p;
          } else {
            El.fly(p.getElement()).focus();
            return;
          }
        }

        focusWidget(c);
      } else {
        El.fly(p.getElement()).focus();
        FocusFrame.get().unframe();
      }
    }
  }

}
