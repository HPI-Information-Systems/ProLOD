/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.layout;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.ScrollContainer;

/**
 * This layout positions the container's children in a single horizontal or
 * vertical row. Each component may specify its height and width in pixels or as
 * percentage.
 * 
 * <p/>
 * Each components margin may also be specified using a RowData instance. Only 1
 * component should specify a margin on adjacent sides.
 * 
 * @see RowData
 */
public class RowLayout extends Layout {

  private Orientation orientation = Orientation.VERTICAL;
  private boolean adjustForScroll;

  /**
   * Creates a new vertical row layout.
   */
  public RowLayout() {
    this(Orientation.VERTICAL);
  }

  /**
   * Creates a new row layout with the given orientation.
   * 
   * @param orientation the orientation of row layout
   */
  public RowLayout(Orientation orientation) {
    this.orientation = orientation;
    monitorResize = true;
  }

  /**
   * Returns the orientation of the layout.
   * 
   * @return the orientation
   */
  public Orientation getOrientation() {
    return orientation;
  }

  /**
   * Returns true if adjust for scroll is enabled.
   * 
   * @return the adjust for scroll state
   */
  public boolean isAdjustForScroll() {
    return adjustForScroll;
  }

  /**
   * True to adjust the container width calculations to account for the scroll
   * bar (defaults to false).
   * 
   * @param adjustForScroll the adjust for scroll state
   */
  public void setAdjustForScroll(boolean adjustForScroll) {
    this.adjustForScroll = adjustForScroll;
  }

  /**
   * Sets the orientation of the layout.
   * 
   * @param orientation the orientation
   */
  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
  }

  protected void layoutHorizontal(El target) {
    Size size = target.getStyleSize();

    int w = size.width - (adjustForScroll ? 19 : 0);
    int h = size.height;
    int pw = w;

    int count = container.getItemCount();

    // some columns can be percentages while others are fixed
    // so we need to make 2 passes
    for (int i = 0; i < count; i++) {
      Component c = container.getItem(i);
      c.el().makePositionable(true);
      c.el().setStyleAttribute("margin", "0px");

      RowData data = null;
      LayoutData d = getLayoutData(c);
      if (d != null && d instanceof RowData) {
        data = (RowData) d;
      } else {
        data = new RowData();
      }

      if (data.getWidth() > 1) {
        pw -= data.getWidth();
      } else if (data.getWidth() == -1) {
        callLayout(c, false);

        pw -= c.getOffsetWidth();
        if (data.getMargins() != null) {
          pw -= data.getMargins().left;
          pw -= data.getMargins().right;
        }
      }
    }

    pw = pw < 0 ? 0 : pw;

    int x = target.getFrameWidth("l");
    int sTop = target.getFrameWidth("t");

    for (int i = 0; i < count; i++) {
      Component c = container.getItem(i);
      RowData data = null;
      LayoutData d = getLayoutData(c);
      if (d != null && d instanceof RowData) {
        data = (RowData) d;
      } else {
        data = new RowData();
      }
      double height = data.getHeight();

      if (height > 0 && height <= 1) {
        height = height * h;
      } else if (height == -1) {
        height = c.getOffsetHeight();
      }

      double width = data.getWidth();
      if (width > 0 && width <= 1) {
        width = width * pw;
      } else if (width == -1) {
        width = c.getOffsetWidth();
      }

      int tx = x;
      int ty = sTop;
      int tw = (int) width;
      int th = (int) height;

      Margins m = data.getMargins();
      if (m != null) {
        tx += m.left;
        ty += m.top;
        if (data.getHeight() != -1) {
          th -= m.top;
          th -= m.bottom;
        }
        if (data.getWidth() != -1) {
          tw -= m.left;
          tw -= m.right;
        }
      }

      setPosition(c, tx, ty);
      setSize(c, tw, th);
      x += tw + (m != null ? m.right + m.left : 0);
    }
  }

  protected void layoutVertical(El target) {
    Size size = target.getStyleSize();

    int w = size.width - (adjustForScroll ? 19 : 0);
    int h = size.height;
    int ph = h;

    int count = container.getItemCount();

    // some columns can be percentages while others are fixed
    // so we need to make 2 passes
    for (int i = 0; i < count; i++) {
      Component c = container.getItem(i);
      RowData data = null;
      LayoutData d = getLayoutData(c);
      if (d != null && d instanceof RowData) {
        data = (RowData) d;
      } else {
        data = new RowData();
      }

      if (data.getHeight() > 1) {
        ph -= data.getHeight();
      } else if (data.getHeight() == -1) {
        callLayout(c, false);

        ph -= c.getOffsetHeight();
        ph -= c.el().getMargins("tb");
      }

    }

    ph = ph < 0 ? 0 : ph;

    for (int i = 0; i < count; i++) {
      Component c = container.getItem(i);
      RowData data = null;
      LayoutData d = getLayoutData(c);
      if (d != null && d instanceof RowData) {
        data = (RowData) d;
      } else {
        data = new RowData();
      }

      double width = data.getWidth();

      if (width > 0 && width <= 1) {
        width = width * w;
      }

      width -= getSideMargins(c);

      double height = data.getHeight();
      if (height > 0 && height <= 1) {
        height = height * ph;
      }

      height -= c.el().getMargins("tb");

      setSize(c, (int) width, (int) height);
    }
  }

  @Override
  protected void onLayout(Container<?> container, El target) {
    super.onLayout(container, target);

    if (container instanceof ScrollContainer<?>) {
      ScrollContainer<?> sc = (ScrollContainer<?>) container;
      sc.setScrollMode(sc.getScrollMode());
    } else {
      target.setStyleAttribute("overflow", "hidden");
    }

    if (orientation == Orientation.VERTICAL) {
      layoutVertical(target);
    } else {
      target.makePositionable();
      layoutHorizontal(target);
    }
  }

}