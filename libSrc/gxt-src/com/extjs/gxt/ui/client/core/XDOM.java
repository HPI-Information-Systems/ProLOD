/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.core;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Provides additional static methods that allow you to manipulate the browser's
 * Document Object Model (DOM).
 * 
 * @see DOM
 */
public final class XDOM {

  private static El bodyEl;
  private static int scrollBarWidth = Style.DEFAULT;
  private static int autoId = 0;
  private static int zIndexId = 1000;
  private static String autoIdPrefix = "x-auto";

  static {
    GXT.init();
  }

  /**
   * Creates an element form the given markup.
   * 
   * @param html the markup
   * @return the new element
   */
  public static Element create(String html) {
    Element div = DOM.createDiv();
    DOM.setInnerHTML(div, html);
    Element firstChild = DOM.getFirstChild(div);
    // support text node creation
    return (firstChild != null) ? firstChild : div;
  }

  /**
   * Returns the auto id prefix.
   * 
   * @return the auto id prefix
   */
  public static String getAutoIdPrefix() {
    return autoIdPrefix;
  }

  /**
   * Returns the body element.
   * 
   * @return the body
   */
  public static native Element getBody() /*-{
    return $doc.body || $doc.documentElement;
  }-*/;

  /**
   * Returns the body El.
   * 
   * @return the body
   */
  public static El getBodyEl() {
    if (bodyEl == null) {
      bodyEl = new El(getBody());
    }
    return bodyEl;
  }

  /**
   * Returns the body elements horizontal scroll.
   * 
   * @return the scroll amount in pixels
   */
  public static native int getBodyScrollLeft() /*-{
    if(@com.extjs.gxt.ui.client.GXT::isIE && @com.extjs.gxt.ui.client.GXT::isStrict){
      return $doc.documentElement.scrollLeft || $doc.body.scrollLeft || 0;
    } else {
      return $wnd.pageXOffset || $doc.body.scrollLeft || 0;
    }
  }-*/;

  /**
   * Return the body elements vertical scroll.
   * 
   * @return the scroll amount in pixels
   */
  public static native int getBodyScrollTop() /*-{
    if(@com.extjs.gxt.ui.client.GXT::isIE && @com.extjs.gxt.ui.client.GXT::isStrict){
      return $doc.documentElement.scrollTop || $doc.body.scrollTop || 0;
    } else {
      return $wnd.pageYOffset || $doc.body.scrollTop || 0;
    }
  }-*/;

  /**
   * Returns the document's height.
   * 
   * @return the document height
   */
  public static native int getDocumentHeight()/*-{
    var scrollHeight = ($doc.compatMode != "CSS1Compat") ? $doc.body.scrollHeight : $doc.documentElement.scrollHeight;
    return Math.max(scrollHeight, @com.extjs.gxt.ui.client.core.XDOM::getViewportHeight()());
  }-*/;

  /**
   * Returns the document width.
   * 
   * @return the document width
   */
  public static native int getDocumentWidth()/*-{
    var scrollWidth = ($doc.compatMode != "CSS1Compat") ? $doc.body.scrollWidth : $doc.documentElement.scrollWidth;
    return Math.max(scrollWidth, @com.extjs.gxt.ui.client.core.XDOM::getViewportWidth()());
  }-*/;

  /**
   * Returns the HTML head element.
   * 
   * @return the head
   */
  public static native Element getHead() /*-{
    return $doc.getElementsByTagName('head')[0];
  }-*/;

  /**
   * Returns the document element.
   * 
   * @return the document
   */
  public static native Element getDocument() /*-{
    return $doc;
  }-*/;

  /**
   * Returns the element with the unique id.
   * 
   * @param id the id
   * @return the element, or null if no match
   */
  public static Element getElementById(String id) {
    return DomQuery.selectNode("#" + id);
  }

  /**
   * Returns the width of the scroll bar.
   * 
   * @return the scroll bar width
   */
  public static int getScrollBarWidth() {
    if (scrollBarWidth == Style.DEFAULT) {
      scrollBarWidth = getScrollBarWidthInternal();
    }
    return scrollBarWidth;
  }

  /**
   * Increments and returns the top z-index value. Use this value to ensure the
   * z-index is the highest value of all elements in the DOM.
   * 
   * @return the z-index
   */
  public static int getTopZIndex() {
    return ++zIndexId;
  }

  /**
   * Increments and returns the top z-index value. Use this value to ensure the
   * z-index is the highest value of all elements in the DOM.
   * 
   * @param i the increment amount
   * @return the z-index
   */
  public static int getTopZIndex(int i) {
    zIndexId += i + 1;
    return zIndexId;
  }

  /**
   * Returns an unique id.
   * 
   * @return the id
   */
  public static String getUniqueId() {
    return autoIdPrefix + "-" + autoId++;
  }

  /**
   * Returns the view height.
   * 
   * @param full true to return the document height, false for viewport height
   * @return the view height
   */
  public static int getViewHeight(boolean full) {
    return full ? getDocumentHeight() : getViewportHeight();
  }

  /**
   * Returns the viewport height.
   * 
   * @return the viewport height
   */
  public static native int getViewportHeight()/*-{
    if(@com.extjs.gxt.ui.client.GXT::isIE){
        return @com.extjs.gxt.ui.client.GXT::isStrict ? $doc.documentElement.clientHeight :
                 $doc.body.clientHeight;
    }else{
        return $wnd.self.innerHeight;
    }
  }-*/;

  /**
   * Returns the viewport width.
   * 
   * @return the viewport width
   */
  public static native int getViewportWidth() /*-{
    if(@com.extjs.gxt.ui.client.GXT::isIE){
        return @com.extjs.gxt.ui.client.GXT::isStrict ? $doc.documentElement.clientWidth :
                 $doc.body.clientWidth;
    }else{
        return $wnd.self.innerWidth;
    }
  }-*/;

  /**
   * Returns the view width.
   * 
   * @param full true to return the document width, false for viewport width
   * @return the view width
   */
  public static int getViewWidth(boolean full) {
    return full ? getDocumentWidth() : getViewportWidth();
  }

  /**
   * Returns the viewports size.
   * 
   * @return the viewport size
   */
  public static Size getViewportSize() {
    return new Size(getViewportWidth(), getViewportHeight());
  }

  /**
   * Reloads the page.
   */
  public native static void reload() /*-{
    $wnd.location.reload();
  }-*/;

  /**
   * Sets the auto id prefix which is prepended to the auto id counter when
   * generating auto ids (defaults to 'x-auto').
   * 
   * @param autoIdPrefix the auto id prefix
   */
  public static void setAutoIdPrefix(String autoIdPrefix) {
    XDOM.autoIdPrefix = autoIdPrefix;
  }

  private native static int getScrollBarWidthInternal() /*-{
    var scr = null;
    var inn = null;
    var wNoScroll = 0;
    var wScroll = 0;
    scr = $doc.createElement('div');
    scr.style.position = 'absolute';
    scr.style.top = '-1000px';
    scr.style.left = '-1000px';
    scr.style.width = '100px';
    scr.style.height = '50px';
    scr.style.overflow = 'hidden';
    inn = $doc.createElement('div');
    inn.style.height = '200px';
    scr.appendChild(inn);
    $doc.body.appendChild(scr);
    wNoScroll = inn.offsetWidth;
    scr.style.overflow = 'auto';
    if (inn.clientWidth != 'undefined') {
    wScroll = inn.clientWidth;
    } else {
    wScroll = inn.offsetWidth;
    }

    $doc.body.removeChild(
    $doc.body.lastChild);

    return (wNoScroll - wScroll);
  }-*/;

  private XDOM() {

  }

}