/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.core;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.core.java.util.Map_CustomFieldSerializerBase;

@SuppressWarnings("unchecked")
public class FastMap_CustomFieldSerializer {

  public static void deserialize(SerializationStreamReader streamReader, FastMap instance)
      throws SerializationException {
    Map_CustomFieldSerializerBase.deserialize(streamReader, instance);
  }

  public static void serialize(SerializationStreamWriter streamWriter, FastMap instance) throws SerializationException {
    Map_CustomFieldSerializerBase.serialize(streamWriter, instance);
  }
}
