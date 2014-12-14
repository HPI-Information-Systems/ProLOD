/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.core.FastSet;

/**
 * Default <code>ModelData</code> implementation.
 */
public class BaseModelData implements ModelData, Serializable {

  protected RpcMap map;
  protected boolean allowNestedValues = true;

  /**
   * Creates a new model data instance.
   */
  public BaseModelData() {
  }

  /**
   * Creates a new model with the given properties.
   * 
   * @param properties the initial properties
   */
  public BaseModelData(Map<String, Object> properties) {
    super();
    setProperties(properties);
  }

  @SuppressWarnings("unchecked")
  public <X> X get(String property) {
    if (allowNestedValues && NestedModelUtil.isNestedProperty(property)) {
      return (X) NestedModelUtil.getNestedValue(this, property);
    }
    if (map == null) {
      return null;
    }
    int start = property.indexOf("[");
    int end = property.indexOf("]");
    X obj = null;
    if (start > -1 && end > -1) {
      Object o = map.get(property.substring(0, start));
      String p = property.substring(start + 1, end);
      if (o instanceof Object[]) {
        obj = (X) ((Object[]) o)[Integer.valueOf(p)];
      } else if (o instanceof List) {
        obj = (X) ((List) o).get(Integer.valueOf(p));
      } else if (o instanceof Map) {
        obj = (X) ((Map) o).get(p);
      }
    } else {
      obj = (X) map.get(property);
    }
    return obj;
  }

  /**
   * Returns a property value.
   * 
   * @param property the property name
   * @param valueWhenNull
   * @return the value
   */
  @SuppressWarnings("unchecked")
  public <X> X get(String property, X valueWhenNull) {
    X value = (X) get(property);
    return (value == null) ? valueWhenNull : value;
  }

  public Map<String, Object> getProperties() {
    Map<String, Object> newMap = new FastMap<Object>();
    if (map != null) {
      newMap.putAll(map.getTransientMap());
    }
    return newMap;
  }

  public Collection<String> getPropertyNames() {
    Set<String> set = new FastSet();
    if (map != null) {
      set.addAll(map.keySet());
    }
    return set;
  }

  /**
   * Returns true if nested values are enabled.
   * 
   * @return the nested values state
   */
  public boolean isAllowNestedValues() {
    return allowNestedValues;
  }

  @SuppressWarnings("unchecked")
  public <X> X remove(String property) {
    return map == null ? null : (X) map.remove(property);
  }

  /**
   * Sets the property and fires an <i>Update</i> event.
   * 
   * @param property the property name
   * @param value the property value
   */
  @SuppressWarnings("unchecked")
  public <X> X set(String property, X value) {
    if (allowNestedValues && NestedModelUtil.isNestedProperty(property)) {
      return (X) NestedModelUtil.setNestedValue(this, property, value);
    }
    if (map == null) {
      map = new RpcMap();
    }

    int start = property.indexOf("[");
    int end = property.indexOf("]");

    if (start > -1 && end > -1) {
      Object o = get(property.substring(0, start));
      String p = property.substring(start + 1, end);
      if (o instanceof Object[]) {
        int i = Integer.valueOf(p);
        Object[] oa = (Object[]) o;
        X old = (X) oa[i];
        oa[i] = value;
        return old;
      } else if (o instanceof List) {
        int i = Integer.valueOf(p);
        List list = (List) o;
        return (X) list.set(i, value);
      } else if (o instanceof Map) {
        Map map = (Map) o;
        return (X) map.put(p, value);
      } else {
        // not supported
        return null;
      }
    } else {
      return (X) map.put(property, value);
    }

  }

  /**
   * Sets whether nested properties are enabled (defaults to true).
   * 
   * @param allowNestedValues true to enable nested properties
   */
  public void setAllowNestedValues(boolean allowNestedValues) {
    this.allowNestedValues = allowNestedValues;
  }

  /**
   * Sets the properties.
   * 
   * @param properties the properties
   */
  public void setProperties(Map<String, Object> properties) {
    for (String property : properties.keySet()) {
      set(property, properties.get(property));
    }
  }

}
