/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.ContainerEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.LayoutEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Layout provides the basic foundation for all other layout classes in GXT. It
 * is a non-visual class that simply provides the base logic required to
 * function as a layout. This class is intended to be extended.
 * 
 * <p/>
 * Layout instances should not be shared with multiple containers.
 * 
 * @see LayoutContainer
 */
public abstract class Layout extends BaseObservable {

  protected Component activeItem;
  protected String componentStyleName;
  protected Container<?> container;
  protected boolean monitorResize;
  protected boolean renderHidden;
  protected El target;
  protected String targetStyleName;
  private Listener<ComponentEvent> componentListener = new Listener<ComponentEvent>() {

    public void handleEvent(ComponentEvent be) {
      EventType type = be.getType();
      if (type == Events.Render) {
        onComponentRender(be.getComponent());
      } else if (type == Events.Show) {
        onComponentShow(be.getComponent());
      } else if (type == Events.Hide) {
        onComponentHide(be.getComponent());
      }

    }

  };
  private Listener<ComponentEvent> containerListener;
  private String extraStyle;
  private int resizeDelay = 0;

  private DelayedTask resizeTask;

  private boolean running;

  /**
   * Returns the extra style name.
   * 
   * @return the extra style
   */
  public String getExtraStyle() {
    return extraStyle;
  }

  /**
   * Returns the window resize delay.
   * 
   * @return the delay
   */
  public int getResizeDelay() {
    return resizeDelay;
  }

  /**
   * Returns true if the container will be render child components hidden.
   * 
   * @return the render hidden state
   */
  public boolean isRenderHidden() {
    return renderHidden;
  }

  /**
   * Returns true if the layout is currently running.
   * 
   * @return true if the layout is currently running
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * Layouts the container, by executing it's layout.
   */
  public void layout() {
    if (container != null && container.isRendered() && !running) {
      if (fireEvent(Events.BeforeLayout, new LayoutEvent(container, this))) {
        running = true;
        initTarget();
        onLayout(container, target);
        running = false;
        fireEvent(Events.AfterLayout, new LayoutEvent(container, this));
      }

    }
  }

  /**
   * Sets the layout's container.
   * 
   * @param ct the container
   */
  public void setContainer(Container<?> ct) {
    if (containerListener == null) {
      containerListener = new Listener<ComponentEvent>() {
        public void handleEvent(ComponentEvent be) {
          if (be.getType() == Events.Remove) {
            onRemove(((ContainerEvent<?, ?>) be).getItem());
          } else if (be.getType() == Events.Resize) {
            if (monitorResize) {
              onResize(be);
            }
          } else if (be.getType() == Events.Add) {
            onAdd(((ContainerEvent<?, ?>) be).getItem());
          }
        }

      };
    }

    if (container != ct) {
      if (container != null) {
        if (target != null) {
          target.removeStyleName(targetStyleName);
          target = null;
        }
        container.removeListener(Events.Remove, containerListener);
        container.removeListener(Events.Add, containerListener);
        container.removeListener(Events.Resize, containerListener);
        if (resizeTask != null) {
          resizeTask.cancel();
        }
        for (Component c : container.getItems()) {
          onRemove(c);
        }
      }

      container = (Container<?>) ct;
      if (ct != null) {
        ct.addListener(Events.Remove, containerListener);
        ct.addListener(Events.Add, containerListener);
        if (resizeTask == null) {
          resizeTask = new DelayedTask(new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
              if (container != null) {
                layout();
              }
            }
          });
        }
        ct.addListener(Events.Resize, containerListener);
        for (Component c : container.getItems()) {
          onAdd(c);
        }
      }
    }
  }

  /**
   * Sets an optional extra CSS style name that will be added to the container.
   * This can be useful for adding customized styles to the container or any of
   * its children using standard CSS rules.
   * 
   * @param extraStyle the extra style name
   */
  public void setExtraStyle(String extraStyle) {
    this.extraStyle = extraStyle;
  }

  /**
   * True to hide each contained component on render (defaults to false).
   * 
   * @param renderHidden true to render hidden
   */
  public void setRenderHidden(boolean renderHidden) {
    this.renderHidden = renderHidden;
  }

  /**
   * Sets the number of milliseconds to buffer resize events (defaults to 0).
   * Only applies when {@link #monitorResize} = true.
   * 
   * @param resizeDelay the delay in milliseconds
   */
  public void setResizeDelay(int resizeDelay) {
    this.resizeDelay = resizeDelay;
  }

  protected void applyMargins(El target, Margins margins) {
    target.setMargins(margins);
  }

  protected void applyPadding(El target, Padding paddings) {
    target.setPadding(paddings);
  }

  protected void callLayout(Component c, boolean force) {
    if (c instanceof WidgetComponent) {
      if (!c.isAttached()) {
        ComponentHelper.doAttach(c);
        ComponentHelper.doDetach(c);
      }
    } else {
      if (c instanceof Composite) {
        c = ((Composite) c).getComponent();
      }
      if (c instanceof Container<?>) {
        Container<?> container = (Container<?>) c;
        if (isLayoutNeeded(container)) {
          doLayout(container);
        }
      }
    }
  }

  protected El fly(com.google.gwt.dom.client.Element elem) {
    return El.fly(elem);
  }

  protected El fly(Element elem) {
    return El.fly(elem);
  }

  protected LayoutData getLayoutData(Component c) {
    return ComponentHelper.getLayoutData(c);
  }

  protected int getSideMargins(Component c) {
    if (GXT.isWebKit) {
      LayoutData data = getLayoutData(c);
      if (data != null && data instanceof MarginData) {
        MarginData m = (MarginData) data;
        Margins margins = m.getMargins();
        if (margins == null) {
          return 0;
        }
        int tot = 0;
        if (margins.left != -1) {
          tot += margins.left;
        }
        if (margins.right != -1) {
          tot += margins.right;
        }
        return tot;
      }
    } else {
      return c.el().getMargins("lr");
    }
    return 0;
  }

  protected void initTarget() {
    if (target == null) {
      target = container.getLayoutTarget();
      target.addStyleName(targetStyleName);
    }
  }

  protected native boolean isLayoutExecuted(Container<?> c) /*-{
    return c.@com.extjs.gxt.ui.client.widget.Container::layoutExecuted;
  }-*/;

  protected native boolean isLayoutNeeded(Container<?> c) /*-{
    return c.@com.extjs.gxt.ui.client.widget.Container::layoutNeeded;
  }-*/;

  protected boolean isValidParent(Element elem, Element parent) {
    return parent != null && parent.isOrHasChild(elem);
  }

  protected void layoutContainer() {
    container.layout();
  }

  protected void onAdd(Component component) {
    if (component.isRendered()) {
      onComponentRender(component);
    } else {
      component.addListener(Events.Render, componentListener);
    }
    component.addListener(Events.Show, componentListener);
    component.addListener(Events.Hide, componentListener);
  }

  protected void onComponentHide(Component component) {

  }

  protected void onComponentShow(Component component) {

  }

  protected void onLayout(Container<?> container, El target) {
    renderAll(container, target);
    for (Component component : container.getItems()) {
      LayoutData data = getLayoutData(component);
      if (data != null && data instanceof MarginData) {
        MarginData ld = (MarginData) data;
        applyMargins(component.el(), ld.getMargins());
      }
    }
  }

  protected void onRemove(Component component) {
    if (activeItem == component) {
      activeItem = null;
    }
    if (extraStyle != null) {
      component.removeStyleName(extraStyle);
    }
    if (componentStyleName != null) {
      component.removeStyleName(componentStyleName);
    }
    component.removeListener(Events.Render, componentListener);
    component.removeListener(Events.Show, componentListener);
    component.removeListener(Events.Hide, componentListener);
  }

  protected void onResize(ComponentEvent ce) {
    resizeTask.delay(resizeDelay);
  }

  @SuppressWarnings("unchecked")
  protected void renderAll(Container container, El target) {
    int count = container.getItemCount();
    for (int i = 0; i < count; i++) {
      Component c = container.getItem(i);
      if (!c.isRendered() || !isValidParent(c.el().dom, target.dom)) {
        renderComponent(c, i, target);
      }
    }
  }

  protected void renderComponent(Component component, int index, El target) {
    if (component.isRendered()) {
      target.insertChild(component.el().dom, index);
    } else {
      component.render(target.dom, index);
    }
    if (renderHidden && component != activeItem) {
      component.hide();
    }
  }

  protected void setBounds(Widget w, int x, int y, int width, int height) {
    if (w instanceof BoxComponent) {
      ((BoxComponent) w).setBounds(x, y, width, height);
    } else {
      fly(w.getElement()).setBounds(x, y, width, height, true);
    }
  }

  protected void setLayoutData(Component c, LayoutData data) {
    ComponentHelper.setLayoutData(c, data);
  }

  protected native void setLayoutNeeded(Container<?> c, boolean needed) /*-{
    c.@com.extjs.gxt.ui.client.widget.Container::layoutNeeded = needed;
  }-*/;

  protected native void setLayoutOnChange(Container<?> c, boolean change) /*-{
    c.@com.extjs.gxt.ui.client.widget.Container::layoutOnChange = change;
  }-*/;

  protected void setPosition(Component c, int left, int top) {
    if (c instanceof BoxComponent) {
      ((BoxComponent) c).setPosition(left, top);
    } else if (c.isRendered()) {
      fly(c.getElement()).setLeftTop(left, top);
    }
  }

  protected void setSize(Component c, int width, int height) {
    if (c instanceof BoxComponent) {
      ((BoxComponent) c).setSize(width, height);
    } else if (c.isRendered()) {
      fly(c.getElement()).setSize(width, height, true);
    }
  }

  private void onComponentRender(Component component) {
    if (extraStyle != null) {
      component.addStyleName(extraStyle);
    }
    if (componentStyleName != null) {
      component.addStyleName(componentStyleName);
    }
  }

  private native void doLayout(Container<?> c) /*-{
    c.@com.extjs.gxt.ui.client.widget.Container::layout()();
  }-*/;

}
