/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.util;

import java.util.Map;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.impl.ClippedImagePrototype;

/**
 * Provides helper methods to create <code>AbstractImagePrototype</code>
 * instances from paths and CSS style names.
 */
public class IconHelper {

  private static El el;

  /**
   * Returns an 16 x 16 image prototype for the given url.
   * 
   * @param url the image url
   * @return the image
   */
  public static AbstractImagePrototype createPath(String url) {
    return createPath(url, 16, 16);
  }

  /**
   * Returns an image for the given url.
   * 
   * @param url the image url
   * @param width the image width in pixels
   * @param height the image height in pixels
   * @return the image
   */
  public static AbstractImagePrototype createPath(String url, int width, int height) {
    ClippedImagePrototype c = new ClippedImagePrototype(url, 0, 0, width, height);
    return c;
  }

  /**
   * Returns a 16 x 16 from the given CSS style name.
   * 
   * @param styleName the style name
   * @return the image
   */
  public static AbstractImagePrototype createStyle(String styleName) {
    return createStyle(styleName, 16, 16);
  }

  /**
   * Returns an image from the given CSS style name.
   * 
   * @param styleName the style name
   * @param width the image width
   * @param height the image height
   * @return the image
   */
  public static AbstractImagePrototype createStyle(String styleName, int width, int height) {
    if (cacheMap == null) {
      el = new El(DOM.createDiv());
      DOM.appendChild(XDOM.getBody(), el.dom);
      el.makePositionable(true);
      el.setLeftTop(-10000, -10000);
      el.setVisibility(false);
      cacheMap = new FastMap<String>();
    }

    String url = cacheMap.get(styleName);
    if (url == null) {
      el.addStyleName(styleName);
      url = el.getStyleAttribute("backgroundImage").replace("\")", "").replace(")", "").replace("url(\"", "").replace(
          "url(", "");
      el.removeStyleName(styleName);
      if ("none".equals(url)) {
        return null;
      }
      cacheMap.put(styleName, url);

    }

    return createPath(url, width, height);
  }

  /**
   * Returns an 16 x 16 image. If the passed parameter is an image path, as
   * defined by @link {@link Util#isImagePath(String)}, it is treated as an
   * image path. Otherwise, the parameter is treated as a CSS style name.
   * 
   * @param s either a image path or a CSS style name
   * @return the image
   */
  public static AbstractImagePrototype create(String s) {
    return create(s, 16, 16);
  }

  /**
   * Returns an image.If the passed parameter is an image path, as defined by @link
   * {@link Util#isImagePath(String)}, it is treated as an image path.
   * Otherwise, the parameter is treated as a CSS style name.
   * 
   * @param s either a image path or a CSS style name
   * @param width the image width
   * @param height the image height
   * @return the image
   */
  public static AbstractImagePrototype create(String s, int width, int height) {
    return Util.isImagePath(s) ? createPath(s, width, height) : createStyle(s, width, height);
  }

  private static Map<String, String> cacheMap;

}
