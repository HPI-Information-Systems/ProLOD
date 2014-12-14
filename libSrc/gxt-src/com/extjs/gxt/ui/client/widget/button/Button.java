/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.button;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.aria.FocusFrame;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.TextMetrics;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.IconSupport;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Accessibility;

/**
 * A button component.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>BeforeSelect</b> : ButtonEvent(button, event)<br>
 * <div>Fires before this button is selected.</div>
 * <ul>
 * <li>button : this</li>
 * <li>event : the dom event</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Select</b> : ButtonEvent(button, event)<br>
 * <div>Fires when this button is selected.</div>
 * <ul>
 * <li>button : this</li>
 * <li>event : the dom event</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>MenuShow</b> : ButtonEvent(button, item)<br>
 * <div>If this button has a menu, this event fires when it is shown.</div>
 * <ul>
 * <li>button : this</li>
 * <li>menu : the menu</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>MenuHide</b> : ButtonEvent(button, item)<br>
 * <div>If this button has a menu, this event fires when it is hidden.</div>
 * <ul>
 * <li>button : this</li>
 * <li>menu : the menu</li>
 * </ul>
 * </dd>
 * 
 * </dt>
 */

public class Button extends BoxComponent implements IconSupport {

  private static Template buttonTemplate;

  protected El buttonEl;
  protected String buttonSelector = "button";
  protected Menu menu;
  protected ButtonScale scale = ButtonScale.SMALL;
  protected Template template;
  protected String text;
  private ButtonArrowAlign arrowAlign = ButtonArrowAlign.RIGHT;
  private boolean handleMouseEvents = true;
  private IconAlign iconAlign = IconAlign.LEFT;
  protected AbstractImagePrototype icon;
  private String menuAlign = "tl-bl?";
  private int minWidth = Style.DEFAULT;
  private int tabIndex = 0;
  private String type = "button";

  private BaseEventPreview preview;
  private Listener<MenuEvent> menuListener;

  /**
   * Creates a new button.
   */
  public Button() {
    baseStyle = "x-btn";
    preview = new BaseEventPreview() {
      protected boolean onAutoHide(PreviewEvent ce) {
        Button.this.onMouseOut(null);
        return true;
      }
    };
  }

  /**
   * Creates a new button with the given text.
   * 
   * @param text the button text
   */
  public Button(String text) {
    this();
    setText(text);
  }

  /**
   * Creates a new button with the given text and iconStyle.
   * 
   * @param text the button text
   * @param icon the icon
   */
  public Button(String text, AbstractImagePrototype icon) {
    this(text);
    setIcon(icon);
  }

  /**
   * Creates a new button with the given text, iconStyle and specified selection
   * listener.
   * 
   * @param text the button text
   * @param icon the icon
   * @param listener the selection listener
   */
  public Button(String text, AbstractImagePrototype icon, SelectionListener<ButtonEvent> listener) {
    this(text, icon);
    addSelectionListener(listener);
  }

  /**
   * Creates a new button with the given text and specified selection listener.
   * 
   * @param text the button's text
   * @param listener the selection listener
   */
  public Button(String text, SelectionListener<ButtonEvent> listener) {
    this(text);
    addSelectionListener(listener);
  }

  /**
   * Adds a selection listener.
   * 
   * @param listener the listener to add
   */
  public void addSelectionListener(SelectionListener<ButtonEvent> listener) {
    addListener(Events.Select, listener);
  }

  /**
   * Returns the button's arrow alignment.
   * 
   * @return the arrow alignment
   */
  public ButtonArrowAlign getArrowAlign() {
    return arrowAlign;
  }

  /**
   * Returns the button's icon style.
   * 
   * @return the icon style
   */
  public AbstractImagePrototype getIcon() {
    return icon;
  }

  /**
   * Returns the button's icon alignment.
   * 
   * @return the icon alignment
   */
  public IconAlign getIconAlign() {
    return iconAlign;
  }

  /**
   * Returns the button's menu (if it has one).
   * 
   * @return the menu
   */
  public Menu getMenu() {
    return menu;
  }

  /**
   * Returns the button's menu alignment.
   * 
   * @return the menu alignment
   */
  public String getMenuAlign() {
    return menuAlign;
  }

  /**
   * Returns the button's minimum width.
   * 
   * @return the minWidth the minimum width
   */
  public int getMinWidth() {
    return minWidth;
  }

  /**
   * Returns true if mouse over effect is disabled.
   * 
   * @return the handleMouseEvents the handle mouse event state
   */
  public boolean getMouseEvents() {
    return handleMouseEvents;
  }

  /**
   * Returns the buttons scale.
   * 
   * @return the scale
   */
  public ButtonScale getScale() {
    return scale;
  }

  /**
   * Returns the button's text.
   * 
   * @return the button text
   */
  public String getText() {
    return text;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Hide this button's menu (if it has one).
   */
  public void hideMenu() {
    if (menu != null) {
      menu.hide();
    }
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);
    ButtonEvent be = (ButtonEvent) ce;
    switch (ce.getEventTypeInt()) {
      case Event.ONMOUSEOVER:
        onMouseOver(ce);
        break;
      case Event.ONMOUSEOUT:
        onMouseOut(ce);
        break;
      case Event.ONMOUSEDOWN:
        onMouseDown(ce);
        break;
      case Event.ONMOUSEUP:
        onMouseUp(ce);
        break;
      case Event.ONCLICK:
        onClick(ce);
        break;
      case Event.ONFOCUS:
        onFocus(ce);
        break;
      case Event.ONBLUR:
        onBlur(be);
        break;
      case Event.ONKEYUP:
        onKeyPress(be);
        break;
    }
  }

  /**
   * Removes a previously added listener.
   * 
   * @param listener the listener to be removed
   */
  public void removeSelectionListener(SelectionListener<ButtonEvent> listener) {
    removeListener(Events.Select, listener);
  }

  /**
   * Sets the arrow alignment (defaults to RIGHT).
   * 
   * @param arrowAlign the arrow alignment
   */
  public void setArrowAlign(ButtonArrowAlign arrowAlign) {
    this.arrowAlign = arrowAlign;
  }

  /**
   * Sets the button's icon style. The style name should match a CSS style that
   * specifies a background image using the following format:
   * 
   * <pre>
   * 
   * &lt;code&gt; .my-icon { background: url(images/icons/my-icon.png) no-repeat
   * center left !important; } &lt;/code&gt;
   * 
   * </pre>
   * 
   * @param icon the icon
   */
  public void setIcon(AbstractImagePrototype icon) {
    if (rendered) {
      El oldIcon = buttonEl.selectNode(".x-btn-image");
      if (oldIcon != null) {
        oldIcon.remove();
        el().removeStyleName("x-btn-text-icon", "x-btn-icon", "x-btn-noicon");
      }
      el().addStyleName(
          (icon != null ? (!Util.isEmptyString(text) ? " x-btn-text-icon" : " x-btn-icon") : " x-btn-noicon"));
      Element e = null;
      String align = null;
      if (icon != null) {
        e = (Element) icon.createElement().cast();

        Accessibility.setRole(e, "presentation");
        fly(e).addStyleName("x-btn-image");

        buttonEl.insertFirst(e);
        El.fly(e).makePositionable(true);

        if (iconAlign == IconAlign.BOTTOM) {
          align = "b-b";
        } else if (iconAlign == IconAlign.TOP) {
          align = "t-t";
        } else if (iconAlign == IconAlign.LEFT) {
          align = "l-l";
        } else if (iconAlign == IconAlign.RIGHT) {
          align = "r-r";
        }

      }
      autoWidth();
      if (e != null) {
        El.fly(e).alignTo(buttonEl.dom, align, null);
      }
    }
    this.icon = icon;
  }

  /**
   * Sets the icon alignment (defaults to LEFT).
   * 
   * @param iconAlign the icon alignment
   */
  public void setIconAlign(IconAlign iconAlign) {
    this.iconAlign = iconAlign;
  }

  public void setIconStyle(String icon) {
    setIcon(IconHelper.create(icon));
  }

  /**
   * Sets the button's menu.
   * 
   * @param menu the menu
   */
  public void setMenu(Menu menu) {
    if (menuListener == null) {
      menuListener = new Listener<MenuEvent>() {
        public void handleEvent(MenuEvent be) {
          if (Events.Show.equals(be.getType())) {
            onMenuShow(be);
          } else if (Events.Hide.equals(be.getType())) {
            onMenuHide(be);
          }
        }
      };
    }
    if (this.menu != null) {
      this.menu.setData("parent", null);
      this.menu.removeListener(Events.Hide, menuListener);
      this.menu.removeListener(Events.Show, menuListener);
    }
    this.menu = menu;
    if (this.menu != null) {
      this.menu.setData("parent", this);
      this.menu.addListener(Events.Hide, menuListener);
      this.menu.addListener(Events.Show, menuListener);
    }
  }

  /**
   * Sets the position to align the menu to, see {@link El#alignTo} for more
   * details (defaults to 'tl-bl?', pre-render).
   * 
   * @param menuAlign the menu alignment
   */
  public void setMenuAlign(String menuAlign) {
    this.menuAlign = menuAlign;
  }

  /**
   * Sets he minimum width for this button (used to give a set of buttons a
   * common width)
   * 
   * @param minWidth the minimum width
   */
  public void setMinWidth(int minWidth) {
    this.minWidth = minWidth;
  }

  /**
   * False to disable visual cues on mouseover, mouseout and mousedown (defaults
   * to true).
   * 
   * @param handleMouseEvents false to disable mouse over changes
   */
  public void setMouseEvents(boolean handleMouseEvents) {
    this.handleMouseEvents = handleMouseEvents;
  }

  /**
   * Sets the button scale.
   * 
   * @param scale the scale to set
   */
  public void setScale(ButtonScale scale) {
    this.scale = scale;
  }

  /**
   * Sets the button's tab index.
   * 
   * @param index the tab index
   */
  public void setTabIndex(int index) {
    this.tabIndex = index;
    if (rendered && buttonEl != null) {
      buttonEl.dom.setPropertyInt("tabIndex", index);
    }
  }

  /**
   * Sets the button's text.
   * 
   * @param text the new text
   */
  public void setText(String text) {
    this.text = text;
    if (rendered) {
      buttonEl.update(Util.isEmptyString(text) ? "&#160;" : text);
      setIcon(icon);
    }
  }

  /**
   * Submit, reset or button (defaults to 'button').
   * 
   * @param type the new type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Show this button's menu (if it has one).
   */
  public void showMenu() {
    if (menu != null) {
      if (GXT.isAriaEnabled()) {
        // delay need for readers
        DeferredCommand.addCommand(new Command() {
          public void execute() {
            menu.show(getElement(), menuAlign);
          }
        });
      } else {
        menu.show(getElement(), menuAlign);
      }
    }
  }

  @Override
  protected void afterRender() {
    super.afterRender();

    setTabIndex(tabIndex);

    setIcon(icon);
  }

  protected void autoWidth() {
    if (rendered && width == null && buttonEl != null) {
      int w = 0;
      if (!Util.isEmptyString(text)) {
        TextMetrics.get().bind(buttonEl);
        w = TextMetrics.get().getWidth(text);

        if (GXT.isGecko || GXT.isWebKit) {
          w += 6;
        }
        w += buttonEl.getFrameWidth("lr");
      } else {
        buttonEl.dom.getStyle().setProperty("width", null);
        w = buttonEl.getWidth();
      }

      if (w < minWidth - 6) {
        buttonEl.setWidth(minWidth - 6, true);
      } else {
        buttonEl.setWidth(w, true);
      }
    }
  }

  @Override
  protected ComponentEvent createComponentEvent(Event event) {
    return new ButtonEvent(this);
  }

  @Override
  protected El getFocusEl() {
    return buttonEl;
  }

  protected String getMenuClass() {
    if (menu != null) {
      if (arrowAlign == ButtonArrowAlign.BOTTOM) {
        return "x-btn-arrow-bottom";
      } else {
        return "x-btn-arrow";
      }
    } else {
      return "";
    }
  }

  protected void onBlur(ButtonEvent e) {
    removeStyleName(baseStyle + "-focus");
    if (GXT.isAriaEnabled()) {
      FocusFrame.get().unframe();
    }
  }

  protected void onClick(ComponentEvent ce) {
    ce.preventDefault();
    focus();
    hideToolTip();
    if (!disabled) {
      ButtonEvent be = new ButtonEvent(this);
      if (!fireEvent(Events.BeforeSelect, be)) {
        return;
      }
      if (menu != null && !menu.isVisible()) {
        showMenu();
      }
      fireEvent(Events.Select, be);
    }
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    preview.remove();
    removeStyleName(baseStyle + "-click");
    removeStyleName(baseStyle + "-over");
    removeStyleName(baseStyle + "-menu-active");
    removeStyleName(baseStyle + "-focus");
  }

  @Override
  protected void onDisable() {
    if (!GXT.isIE6 || text == null) {
      addStyleName(disabledStyle);
    }
    removeStyleName(baseStyle + "-over");
    el().disable();
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    el().enable();
  }

  protected void onFocus(ComponentEvent ce) {
    if (!disabled) {
      addStyleName(baseStyle + "-focus");
      if (GXT.isAriaEnabled() && !GXT.isIE) {
        FocusFrame.get().frame(this);
      }
    }
  }

  protected void onKeyPress(ButtonEvent be) {
    if (be.getEvent().getKeyCode() == KeyCodes.KEY_DOWN) {
      if (menu != null && !menu.isVisible()) {
        showMenu();
      }
    }
  }

  protected void onMenuHide(ComponentEvent ce) {
    removeStyleName(baseStyle + "-menu-active");
    
    ButtonEvent be = new ButtonEvent(this);
    be.setMenu(menu);
    fireEvent(Events.MenuHide, be);
    focus();
  }

  protected void onMenuShow(ComponentEvent ce) {
    addStyleName(baseStyle + "-menu-active");
    
    ButtonEvent be = new ButtonEvent(this);
    be.setMenu(menu);
    fireEvent(Events.MenuShow, be);
    
    if (GXT.isAriaEnabled()) {
      if (menu.getItemCount() > 0) {
        menu.setActiveItem(menu.getItem(0), false);
      }
    }
  }

  protected void onMouseDown(ComponentEvent ce) {
    if (handleMouseEvents) {
      addStyleName(baseStyle + "-click");
    }
  }

  protected void onMouseOut(ComponentEvent ce) {
    removeStyleName(baseStyle + "-click");
    removeStyleName(baseStyle + "-over");
  }

  protected void onMouseOver(ComponentEvent ce) {
    if (handleMouseEvents) {
      addStyleName(baseStyle + "-over");
      preview.add();
    }
  }

  protected void onMouseUp(ComponentEvent ce) {
    removeStyleName(baseStyle + "-click");
  }

  protected void onRender(Element target, int index) {
    if (template == null) {
      if (buttonTemplate == null) {
        StringBuffer sb = new StringBuffer();
        sb.append("<table cellspacing=\"0\" class=\"x-btn\" role=\"presentation\"><tbody class=\"{2}\" >");
        sb.append("<tr><td class=\"x-btn-tl\"><i>&#160;</i></td><td class=\"x-btn-tc\"></td><td class=\"x-btn-tr\"><i>&#160;</i></td></tr>");
        sb.append("<tr><td class=\"x-btn-ml\"><i>&#160;</i></td><td class=\"x-btn-mc\"><em class=\"{3}\" unselectable=\"on\"><button class=\"x-btn-text\" type=\"{1}\" style='position: static'>{0}</button></em></td><td class=\"x-btn-mr\"><i>&#160;</i></td></tr>");
        sb.append("<tr><td class=\"x-btn-bl\"><i>&#160;</i></td><td class=\"x-btn-bc\"></td><td class=\"x-btn-br\"><i>&#160;</i></td></tr>");
        sb.append("</tbody></table>");

        buttonTemplate = new Template(sb.toString());
      }
      template = buttonTemplate;
    }

    setElement(template.create((text != null && text.length() > 0) ? text : "&nbsp;", getType(), "x-btn-"
        + scale.name().toLowerCase() + " x-btn-icon-" + scale.name().toLowerCase() + "-"
        + iconAlign.name().toLowerCase(), getMenuClass()), target, index);

    super.onRender(target, index);

    buttonEl = el().selectNode(buttonSelector);
    buttonEl.makePositionable();

    if (getFocusEl() != null) {
      getFocusEl().addEventsSunk(Event.FOCUSEVENTS);
    }

    preview.getIgnoreList().add(getElement());

    buttonEl.setTabIndex(0);

    if (GXT.isAriaEnabled()) {
      Accessibility.setRole(buttonEl.dom, Accessibility.ROLE_BUTTON);
      if (menu != null) {
        Accessibility.setState(buttonEl.dom, "aria-haspopup", "true");
      }
    }

    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS | Event.KEYEVENTS);
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    buttonEl.setSize(width - 6, height - 6, true);
  }
}
