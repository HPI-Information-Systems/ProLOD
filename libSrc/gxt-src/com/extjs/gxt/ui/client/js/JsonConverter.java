/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */package com.extjs.gxt.ui.client.js;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * Helper class to decode and encode objects to and from Json. Converter handles
 * simple data types (strings, numbers, booleans) and lists and maps.
 */
public class JsonConverter {

  /**
   * Decodes a Json string into a map.
   * 
   * @param jsonString the Json string
   * @return the map
   */
  public static Map<String, Object> decode(String jsonString) {
    JSONValue v = JSONParser.parse(jsonString);
    if (v.isObject() != null) {
      return decode(v.isObject());
    } else {
      return null;
    }
  }

  /**
   * Decodes a JSONObject to a map.
   * 
   * @param jso the JSONObject
   * @return the map
   */
  public static Map<String, Object> decode(JSONObject jso) {
    Map<String, Object> map = new FastMap<Object>();
    for (String key : jso.keySet()) {
      JSONValue j = jso.get(key);
      if (j.isObject() != null) {
        map.put(key, decode(j.isObject()));
      } else if (j.isArray() != null) {
        map.put(key, decodeToList(j.isArray()));
      } else if (j.isBoolean() != null) {
        map.put(key, j.isBoolean().booleanValue());
      } else if (j.isString() != null) {
        map.put(key, decodeValue(j.isString().stringValue()));
      }
    }
    return map;
  }

  /**
   * Encodes a map into a JSONObject.
   * 
   * @param map the map
   * @return the JSONObject
   */
  public static JSONObject encode(Map<String, Object> map) {
    return encodeMap((Map<String, Object>) map);
  }

  /**
   * Encodes a model data instance into a JSONObject.
   * 
   * @param model the model data object
   * @return the JSONObject
   */
  public static JSONObject encode(ModelData model) {
    return encodeMap(((ModelData) model).getProperties());
  }

  protected static Object decodeValue(String value) {
    try {
      if (value == null || value.length() < 3) {
        return null;
      }
      String type = value.substring(0, 2);
      String val = value.substring(2);
      if (type.equals("d:")) {
        long time = Long.parseLong(val);
        return new Date(time);
      } else if (type.equals("i:")) {
        return Integer.decode(val);
      } else if (type.equals("f:")) {
        return new Float(val);
      }
      return val;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  protected static List<Object> decodeToList(JSONArray array) {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < array.size(); i++) {
      JSONValue v = array.get(i);
      if (v.isObject() != null) {
        list.add(decode(v.isObject()));
      } else if (v.isArray() != null) {
        list.add(decodeToList(v.isArray()));
      } else if (v.isNull() != null) {
        list.add(null);
      } else if (v.isNumber() != null) {
        list.add(v.isNumber().doubleValue());
      } else if (v.isBoolean() != null) {
        list.add(v.isBoolean().booleanValue());
      } else if (v.isString() != null) {
        list.add(decodeValue(v.isString().stringValue()));
      }
    }

    return list;
  }

  protected static String encodeValue(Object value) {
    if (value instanceof Date) {
      return "d:" + ((Date) value).getTime();
    } else if (value instanceof Integer) {
      return "i:" + value;
    } else if (value instanceof Float) {
      return "f:" + value;
    }
    return "s:" + value.toString();
  }

  @SuppressWarnings("unchecked")
  protected static JSONObject encodeMap(Map<String, Object> data) {
    JSONObject jsobj = new JSONObject();
    for (String key : data.keySet()) {
      Object val = data.get(key);
      if (val instanceof String) {
        jsobj.put(key, new JSONString(encodeValue(val)));
      } else if (val instanceof Date) {
        jsobj.put(key, new JSONString(encodeValue(val)));
      } else if (val instanceof Number) {
        jsobj.put(key, new JSONString(encodeValue(val)));
      } else if (val instanceof Boolean) {
        jsobj.put(key, JSONBoolean.getInstance((Boolean) val));
      } else if (val == null) {
        jsobj.put(key, JSONNull.getInstance());
      } else if (val instanceof Map) {
        jsobj.put(key, encodeMap((Map<String, Object>) val));
      } else if (val instanceof List) {
        jsobj.put(key, encodeList((List<Object>) val));
      } else if (val instanceof ModelData) {
        jsobj.put(key, encodeMap(((ModelData) val).getProperties()));
      }
    }

    return jsobj;
  }

  @SuppressWarnings("unchecked")
  protected static JSONArray encodeList(List<Object> data) {
    JSONArray jsona = new JSONArray();
    for (int i = 0; i < data.size(); i++) {
      Object val = data.get(i);
      if (val instanceof ModelData) {
        jsona.set(i, encodeMap(((ModelData) val).getProperties()));
      } else if (val instanceof Map) {
        jsona.set(i, encodeMap((Map<String, Object>) val));
      } else if (val instanceof List) {
        jsona.set(i, encodeList((List<Object>) val));
      } else if (val instanceof String) {
        jsona.set(i, new JSONString(encodeValue(val)));
      } else if (val instanceof Number) {
        jsona.set(i, new JSONString(encodeValue(val)));
      } else if (val instanceof Boolean) {
        jsona.set(i, JSONBoolean.getInstance((Boolean) val));
      } else if (val == null) {
        jsona.set(i, JSONNull.getInstance());
      } else if (val instanceof Date) {
        jsona.set(i, new JSONString(encodeValue(val)));
      }
    }
    return jsona;
  }
}