/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.layout;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.Layout;

/**
 * This is a layout that enables anchoring of contained widgets relative to the
 * container's dimensions. If the container is resized, all anchored items are
 * automatically re-rendered according to their anchor rules.
 * 
 * <p />
 * Child Widgets are:
 * <ul>
 * <li><b>Sized</b> : Yes - relative to parent container</li>
 * <li><b>Positioned</b> : No - child widgets will flow</li>
 * </ul>
 * 
 * <p />
 * By default, AnchorLayout will calculate anchor measurements based on the size
 * of the container itself. However, if <code>anchorSize</code> is specified,
 * the layout will use it as a virtual container for the purposes of calculating
 * anchor measurements based on it instead, allowing the container to be sized
 * independently of the anchoring logic if necessary.
 * 
 * <p />
 * The items added to an AnchorLayout can also supply an anchoring-specific
 * layout property (see {@link AnchorData#setAnchorSpec(String)}) which is a
 * string containing two values: the horizontal anchor value and the vertical
 * anchor value (for example, '100% 50%'). This value is what tells the layout
 * how the item should be anchored to the container. The following types of
 * anchor values are supported:
 * <ul>
 * <li><b>Percentage</b> : Any value between 1 and 100, expressed as a
 * percentage. The first anchor is the percentage width that the item should
 * take up within the container, and the second is the percentage height.
 * Example: '100% 50%' would render an item the complete width of the container
 * and 1/2 its height. If only one anchor value is supplied it is assumed to be
 * the width value and the height will default to auto.</li>
 * <li><b>Offsets</b> : Any positive or negative integer value. The first anchor
 * is the offset from the right edge of the container, and the second is the
 * offset from the bottom edge. Example: '-50 -100' would render an item the
 * complete width of the container minus 50 pixels and the complete height minus
 * 100 pixels. If only one anchor value is supplied it is assumed to be the
 * right offset value and the bottom offset will default to 0.</li>
 * </ul>
 * 
 * <p />
 * Anchor values can also be mixed as needed. For example, '-50 75%' would
 * render the width offset from the container right edge by 50 pixels and 75% of
 * the container's height.
 */
public class AnchorLayout extends Layout {

  private Size anchorSize;

  public AnchorLayout() {
    monitorResize = true;
  }

  /**
   * Returns the anchor size.
   * 
   * @return the anchor size
   */
  public Size getAnchorSize() {
    return anchorSize;
  }

  /**
   * Sets a virtual container for the layout to use.
   * 
   * @param anchorSize the anchor size
   */
  public void setAnchorSize(Size anchorSize) {
    this.anchorSize = anchorSize;
  }

  protected int adjustHeightAnchor(int height, Component comp) {
    return height;
  }

  protected int adjustWidthAnchor(int width, Component comp) {
    return width;
  }

  @Override
  protected void onLayout(Container<?> container, El target) {
    super.onLayout(container, target);
    Size size = target.getStyleSize();

    int w = size.width, h = size.height;

    int aw, ah;

    if (anchorSize != null) {
      aw = anchorSize.width;
      ah = anchorSize.height;
    } else {
      aw = w;
      ah = h;
    }

    for (Component comp : container.getItems()) {
      AnchorData layoutData = null;
      LayoutData d = getLayoutData(comp);
      if (d != null && d instanceof AnchorData) {
        layoutData = (AnchorData) d;
      } else {
        layoutData = comp.getData("anchorSpec");
      }

      if (layoutData == null) {
        layoutData = new AnchorData();
      }

      if (layoutData != null) {
        String anchor = layoutData.getAnchorSpec();
        if (anchor != null) {
          String[] vs = anchor.split(" ");
          int cw = parseAnchor(vs[0], w, aw, aw);
          int ch = -1;
          if (vs.length > 1) {
            ch = parseAnchor(vs[1], h, ah, ah);
          }

          cw -= getSideMargins(comp);
          ch -= comp.el().getMargins("tb");

          cw = adjustWidthAnchor(cw, comp);
          ch = adjustHeightAnchor(ch, comp);

          setSize(comp, cw, ch);
        }
      }
    }
  }

  private int parseAnchor(String a, int v, int start, int cstart) {
    if (a != null && !"none".equals(a)) {
      if (standard(a)) {
        int diff = cstart - start;
        return v - diff;
      } else if (a.indexOf("%") != -1) {
        double ratio = Float.parseFloat(a.replaceAll("%", "")) * .01;
        return (int) Math.floor(v * ratio);
      } else {
        int val = Util.parseInt(a, -1);
        return v + val;
      }
    }
    return -1;
  }

  private native boolean standard(String a) /*-{
    if(/^(r|right|b|bottom)$/i.test(a)){ 
    return true;
    } else {
    return false;
    }
  }-*/;

}
