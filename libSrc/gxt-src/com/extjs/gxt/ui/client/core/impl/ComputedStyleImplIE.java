/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.core.impl;

import java.util.List;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.FastMap;
import com.google.gwt.user.client.Element;

public class ComputedStyleImplIE extends ComputedStyleImpl {

  @Override
  public FastMap<String> getStyleAttribute(Element elem, List<String> names) {
    return getComputedStyle(elem, names, checkCamelCache(names), null, null);
  }

  @Override
  public void setStyleAttribute(Element elem, String name, Object value) {
    if ("opacity".equals(name)) {
      setOpacity(elem, Double.valueOf((String.valueOf(value))));
    } else {
      super.setStyleAttribute(elem, name, value);
    }
  }

  @Override
  protected String getPropertyName(String name) {
    if ("float".equals(name)) {
      return "styleFloat";
    }
    return name;
  }

  @Override
  protected native FastMap<String> getComputedStyle(Element elem, List<String> orginals, List<String> names, List<String> names2, String pseudo) /*-{
    var map = @com.extjs.gxt.ui.client.core.FastMap::new()();
    var size = orginals.@java.util.List::size()()
    for(var i = 0;i<size;i++){
      var name = names.@java.util.List::get(I)(i);
      var orginal = orginals.@java.util.List::get(I)(i);

      if(name == "opacity"){
        if(typeof elem.style.filter == "string"){
          var m = elem.style.filter.match(/alpha\(opacity=(.*)\)/i);
          if(m){
            var fv = parseFloat(m[1]);
            if(!isNaN(fv)){
              map.@com.extjs.gxt.ui.client.core.FastMap::put(Ljava/lang/String;Ljava/lang/Object;)(orginal,String(fv ? fv / 100 : 0));
              continue;
            }
          }
        }
        map.@com.extjs.gxt.ui.client.core.FastMap::put(Ljava/lang/String;Ljava/lang/Object;)(orginal,String(1));
        continue;
      }

      var v, cs;
      if(v = elem.style[name]){
        map.@com.extjs.gxt.ui.client.core.FastMap::put(Ljava/lang/String;Ljava/lang/Object;)(orginal,String(v));
      } else if(cs = elem.currentStyle) {
        map.@com.extjs.gxt.ui.client.core.FastMap::put(Ljava/lang/String;Ljava/lang/Object;)(orginal,String(cs[name]));
      } else {
        map.@com.extjs.gxt.ui.client.core.FastMap::put(Ljava/lang/String;Ljava/lang/Object;)(orginal,null);
      }
    }
    return map;
  }-*/;

  protected native El setOpacity(Element dom, double opacity)/*-{
    dom.style.zoom = 1;
    dom.style.filter = (dom.style.filter || '').replace(/alpha\([^\)]*\)/gi,"") + (opacity == 1 ? "" : " alpha(opacity=" + opacity * 100 + ")");
  }-*/;

}
