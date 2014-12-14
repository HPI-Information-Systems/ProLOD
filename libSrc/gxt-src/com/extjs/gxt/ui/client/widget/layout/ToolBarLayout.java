/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.layout;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.menu.HeaderMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class ToolBarLayout extends Layout {

  protected Button more;
  protected Menu moreMenu;

  private El extrasTr;
  private List<Component> hiddens;
  private boolean lastOverflow = false;
  private int lastWidth = 0;
  private El leftTr;
  private String noItemsMenuText = "<div class=\"x-toolbar-no-items\">(None)</div>";
  private El rightTr;
  private int spacing = 0;
  private int triggerWidth = 18;

  public ToolBarLayout() {
    monitorResize = true;
    hiddens = new ArrayList<Component>();
    targetStyleName = "x-toolbar-layout-ct";
  }

  /**
   * Returns the no menu item text.
   * 
   * @return the no menu item text
   */
  public String getNoItemsMenuText() {
    return noItemsMenuText;
  }

  /**
   * Returns the item spacing.
   * 
   * @return the spacing
   */
  public int getSpacing() {
    return spacing;
  }

  /**
   * Sets the no menu items text (defaults to '<div
   * class=\"x-toolbar-no-items\">(None)</div>').
   * 
   * @param noItemsMenuText the no menu items text
   */
  public void setNoItemsMenuText(String noItemsMenuText) {
    this.noItemsMenuText = noItemsMenuText;
  }

  /**
   * Sets the amount of spacing between items (defaults to 0).
   * 
   * @param spacing the spacing
   */
  public void setSpacing(int spacing) {
    this.spacing = spacing;
  }

  protected void addComponentToMenu(Menu menu, Component c) {
    if (c instanceof SeparatorToolItem) {
      menu.add(new SeparatorMenuItem());
    } else if (c instanceof SplitButton) {
      final SplitButton sb = (SplitButton) c;
      MenuItem item = new MenuItem(sb.getText(), sb.getIcon());
      item.setEnabled(c.isEnabled());
      item.setItemId(c.getItemId());
      if (sb.getMenu() != null) {
        item.setSubMenu(sb.getMenu());
      }
      item.addSelectionListener(new SelectionListener<MenuEvent>() {

        @Override
        public void componentSelected(MenuEvent ce) {
          ButtonEvent e = new ButtonEvent(sb);
          e.setEvent(ce.getEvent());
          sb.fireEvent(Events.Select, e);
        }

      });
      menu.add(item);

    } else if (c instanceof Button) {
      final Button b = (Button) c;
      MenuItem item = new MenuItem(b.getText(), b.getIcon());
      item.setItemId(c.getItemId());
      if (b.getMenu() != null) {
        item.setHideOnClick(false);
        item.setSubMenu(b.getMenu());
      }
      item.setEnabled(c.isEnabled());
      item.addSelectionListener(new SelectionListener<MenuEvent>() {

        @Override
        public void componentSelected(MenuEvent ce) {
          ButtonEvent e = new ButtonEvent(b);
          e.setEvent(ce.getEvent());
          b.fireEvent(Events.Select, e);
        }

      });
      menu.add(item);
    } else if (c instanceof ButtonGroup) {
      ButtonGroup g = (ButtonGroup) c;
      g.setItemId(c.getItemId());
      menu.add(new SeparatorMenuItem());
      String heading = g.getHeading();
      if (heading != null && heading.length() > 0 && !heading.equals("&#160;")) {
        menu.add(new HeaderMenuItem(g.getHeading()));
      }
      for (Component c2 : g.getItems()) {
        addComponentToMenu(menu, c2);
      }
      menu.add(new SeparatorMenuItem());
    }

    if (menu.getItemCount() > 0) {
      if (menu.getItem(0) instanceof SeparatorMenuItem) {
        menu.remove(menu.getItem(0));
      }
      if (menu.getItemCount() > 0) {
        if (menu.getItem(menu.getItemCount() - 1) instanceof SeparatorMenuItem) {
          menu.remove(menu.getItem(menu.getItemCount() - 1));
        }
      }
    }
  }

  protected void cleanup(El row) {
    NodeList<Node> cn = row.dom.getChildNodes();
    for (int i = cn.getLength() - 1; i >= 0; i--) {
      Element td = (Element) cn.getItem(i);
      if (!td.hasChildNodes()) {
        row.dom.removeChild(td);
      }
    }
  }

  protected void clearMenu() {
    moreMenu.removeAll();
  }

  protected void fitToSize(El t) {
    if (!((ToolBar) container).isEnableOverflow()) {
      return;
    }
    if (target.getWidth() < 1) {
      return;
    }
    int w = t.getWidth(true);
    int lw = lastWidth;

    lastWidth = w;
    int iw = t.firstChild().getWidth(true);

    int clipWidth = w - triggerWidth;

    if (iw > w || (hiddens != null && hiddens.size() > 0 && w >= lw)) {
      int loopWidth = 0;
      for (Component c : container.getItems()) {
        if (!(c instanceof FillToolItem)) {
          loopWidth += getComponentWidth(c);
          if (loopWidth >= clipWidth) {
            if (!isHidden(c)) {
              hideComponent(c);
            }
          } else {
            if (isHidden(c)) {
              unhideComponent(c);
            }
          }
        }
      }
    }
    if (hiddens != null && hiddens.size() > 0) {
      initMore();
      if (!lastOverflow) {
        lastOverflow = true;
      }
    } else if (more != null) {
      ComponentHelper.doDetach(more);
      more.el().removeFromParent();
      if (lastOverflow) {
        lastOverflow = false;
      }
    }
  }

  protected int getComponentWidth(Component c) {
    return (Integer) (c.getData("xtbWidth") != null ? c.getData("xtbWidth") : c.el().getParent().getWidth());
  }

  protected void hideComponent(Component c) {
    c.setData("xtbWidth", c.el().getParent().getWidth());
    c.setData("xtbIsVisible", c.isVisible(false));
    hiddens.add(c);
    c.hide();
  }

  protected void initMore() {
    if (more == null) {
      moreMenu = new Menu();
      moreMenu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {

        public void handleEvent(MenuEvent be) {
          clearMenu();
          for (Component c : container.getItems()) {

            if (isHidden(c)) {
              addComponentToMenu(be.getContainer(), c);
            }
          }
          // put something so the menu isn't empty
          // if no compatible items found
          if (be.getContainer().getItemCount() == 0) {
            be.getContainer().add(new HeaderMenuItem(noItemsMenuText));
          }
        }

      });

      more = new Button();
      more.addStyleName("x-toolbar-more");
      more.setIcon(GXT.IMAGES.toolbar_more());
      more.setMenu(moreMenu);

    }
    Element td = insertCell(more, extrasTr, 100);
    if (more.isRendered()) {
      td.appendChild(more.el().dom);
    } else {
      more.render(td);
    }
    ComponentHelper.doAttach(more);
  }

  protected Element insertCell(Component c, El side, int pos) {
    Element td = DOM.createTD();
    td.setClassName("x-toolbar-cell");
    Element point;
    if (pos >= side.dom.getChildNodes().getLength()) {
      point = null;
    } else {
      point = side.getChild(pos) != null ? side.getChild(pos).dom : null;
    }
    side.insertBefore(td, point);
    return td;
  }

  protected boolean isHidden(Component c) {
    return hiddens.contains(c);
  }

  @Override
  protected void onComponentHide(Component component) {
    super.onComponentHide(component);
    if (component.isRendered()) {
      component.el().getParent().addStyleName(component.getHideMode().value());
    }

  }

  @Override
  protected void onComponentShow(Component component) {
    super.onComponentShow(component);
    if (component.isRendered()) {
      component.el().getParent().removeStyleName(component.getHideMode().value());
    }
  }

  protected void onLayout(Container<?> container, El target) {
    if (leftTr == null) {
      target.insertHtml(
          "beforeEnd",
          "<table cellspacing=\"0\" class=\"x-toolbar-ct\" role=\"presentation\"><tbody><tr><td class=\"x-toolbar-left\" align=\"left\"><table cellspacing=\"0\" role=\"presentation\"><tbody><tr class=\"x-toolbar-left-row\"></tr></tbody></table></td><td class=\"x-toolbar-right\" align=\"right\"><table cellspacing=\"0\" class=\"x-toolbar-right-ct\"><tbody><tr><td><table cellspacing=\"0\"><tbody><tr class=\"x-toolbar-right-row\"></tr></tbody></table></td><td><table cellspacing=\"0\"><tbody><tr class=\"x-toolbar-extras-row\"></tr></tbody></table></td></tr></tbody></td></tr></tbody></table>");
      leftTr = target.child("tr.x-toolbar-left-row");
      rightTr = target.child("tr.x-toolbar-right-row");
      extrasTr = target.child("tr.x-toolbar-extras-row");
    }
    El side = leftTr;
    int pos = 0;

    for (int i = 0, len = container.getItemCount(); i < len; i++, pos++) {
      Component c = container.getItem(i);
      if (c instanceof FillToolItem) {
        side = rightTr;
        pos = -1;
      } else if (!c.isRendered()) {
        c.render(insertCell(c, side, pos));
        if (i < len - 1) {
          c.el().setStyleAttribute("marginRight", spacing + "px");
        } else {
          c.el().setStyleAttribute("marginRight", "0px");
        }
      } else {
        if (!isHidden(c) && !isValidParent(c.el().dom, side.getChildElement(pos))) {
          Element td = insertCell(c, side, pos);
          td.appendChild(c.el().dom);
          if (i < len - 1) {
            c.el().setStyleAttribute("marginRight", spacing + "px");
          } else {
            c.el().setStyleAttribute("marginRight", "0px");
          }
        }
      }
    }
    // strip extra empty cells
    cleanup(leftTr);
    cleanup(rightTr);
    cleanup(extrasTr);
    fitToSize(target);
  }

  protected void unhideComponent(Component c) {
    if (hiddens.remove(c)) {
      if (c.<Boolean> getData("xtbIsVisible")) {
        c.show();
      }
      c.setData("xtbWidth", null);
      c.setData("xtbIsVisible", null);
    }
  }
}