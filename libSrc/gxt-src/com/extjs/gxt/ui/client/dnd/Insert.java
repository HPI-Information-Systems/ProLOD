/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.dnd;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.Element;

/**
 * A custom component used to show insert locations with drop targets.
 */
public class Insert extends BoxComponent {

  private static Insert instance;

  public static Insert get() {
    if (instance == null) {
      instance = new Insert();
      instance.render(XDOM.getBody());
    }
    return instance;
  }

  Insert() {
    setShadow(false);
    hide();
  }

  public void show(Element c) {
    c.insertBefore(getElement(), null);
    show();
  }

  @Override
  protected void onHide() {
    super.onHide();
    el().remove();
  }

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);

    StringBuffer sb = new StringBuffer();
    sb.append("<table class=\"x-insert-bar\" height=\"6\" cellspacing=\"0\" cellpadding=\"0\"><tbody><tr>");
    sb.append("<td height=\"6\" class=\"x-insert-left\"><div style=\"width: 3px\"></div></td>");
    sb.append("<td class=\"x-insert-mid\" width=\"100%\">&nbsp;</td>");
    sb.append("<td class=\"x-insert-right\"><div style=\"width: 3px\"></div></td>");
    sb.append("</tr></tbody></table>");

    setElement(XDOM.create(sb.toString()), target, index);
  }

  @Override
  protected void onShow() {
    super.onShow();
    if (!el().isConnected()) {
      XDOM.getBody().insertBefore(getElement(), null);
    }
  }

}
