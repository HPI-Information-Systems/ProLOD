/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.util.Format;
import com.google.gwt.user.client.Element;

public class ComputedStyleImpl {

  public FastMap<String> getStyleAttribute(El elem, List<String> names) {
    return getStyleAttribute(elem.dom, names);
  }

  protected Map<String, String> camelCache = new FastMap<String>();
  protected Map<String, String> hyphenCache = new FastMap<String>();

  public FastMap<String> getStyleAttribute(Element elem, List<String> names) {
    return getComputedStyle(elem, names, checkHyphenCache(names), checkCamelCache(names), null);
  }

  public void setStyleAttribute(Element elem, String name, Object value) {
    elem.getStyle().setProperty(checkCamelCache(Arrays.asList(name)).get(0), String.valueOf(value));
  }

  protected List<String> checkCamelCache(List<String> l) {
    List<String> list = new ArrayList<String>(l);
    for (int i = 0; i < list.size(); i++) {
      String s = list.get(i);
      String t = camelCache.get(s);
      if (t == null) {
        t = Format.camelize(getPropertyName(s));
        camelCache.put(s, t);
      }
      list.set(i, t);
    }
    return list;
  }

  protected List<String> checkHyphenCache(List<String> l) {
    List<String> list = new ArrayList<String>(l);
    for (int i = 0; i < list.size(); i++) {
      String s = list.get(i);
      String t = hyphenCache.get(s);
      if (t == null) {
        t = Format.hyphenize(getPropertyName(s));
        hyphenCache.put(s, t);
      }
      list.set(i, t);
    }
    return list;
  }

  protected String getPropertyName(String name) {
    if ("float".equals(name)) {
      return "cssFloat";
    }
    return name;
  }

  protected native FastMap<String> getComputedStyle(Element elem, List<String> orginals, List<String> names,
      List<String> names2, String pseudo) /*-{
    var cStyle;
    var map = @com.extjs.gxt.ui.client.core.FastMap::new()();
    var size = orginals.@java.util.List::size()();
    for(var i = 0;i<size;i++){
      var orginal = orginals.@java.util.List::get(I)(i);

      var name2 = names2.@java.util.List::get(I)(i);
      var v = elem.style[name2];
      if(v){
        map.@com.extjs.gxt.ui.client.core.FastMap::put(Ljava/lang/String;Ljava/lang/Object;)(orginal,String(v));
        continue;
      }
      var name = names.@java.util.List::get(I)(i);
      if(!cStyle){
        cStyle = $doc.defaultView.getComputedStyle(elem, pseudo);
      }
      map.@com.extjs.gxt.ui.client.core.FastMap::put(Ljava/lang/String;Ljava/lang/Object;)(orginal,cStyle ? String(cStyle.getPropertyValue(name)) : null);
    }
    return map;
  }-*/;

}
