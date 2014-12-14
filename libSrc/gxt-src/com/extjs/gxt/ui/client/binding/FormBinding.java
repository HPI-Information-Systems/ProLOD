/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.binding;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

/**
 * A <code>Bindings</code> subclass for forms.
 * 
 * <p />
 * If a store is specified, edits will done using record instances from the
 * store, rather than editing the model directly. This allows all changes to be
 * committed or rolled back in batch.
 */
@SuppressWarnings("unchecked")
public class FormBinding extends Bindings {

  protected FormPanel panel;
  protected Store store;
  protected boolean updateOriginalValue;

  /**
   * Creates a new form binding instance.
   * 
   * @param panel the form panel
   */
  public FormBinding(FormPanel panel) {
    this.panel = panel;
  }

  /**
   * Creates a new form binding instance.
   * 
   * @param panel the form panel
   * @param autoBind true to automatically add field bindings based on the forms
   *          field names
   */
  public FormBinding(FormPanel panel, boolean autoBind) {
    this.panel = panel;
    if (autoBind) {
      autoBind();
    }
  }

  /**
   * Searches the form's fields, and adds a binding to and field without a
   * binding.
   */
  public void autoBind() {
    for (Field<?> f : panel.getFields()) {
      if (!bindings.containsKey(f.getId())) {
        String name = f.getName();
        if (name != null && name.length() > 0) {
          FieldBinding b = new FieldBinding(f, f.getName());
          bindings.put(f.getId(), b);
        }
      }
    }
  }

  @Override
  public void bind(ModelData model) {
    for (FieldBinding b : getBindings()) {
      b.setStore(store);
    }
    super.bind(model);
  }

  /**
   * Returns the form binding's store.
   * 
   * @return the store
   */
  public Store getStore() {
    return store;
  }

  /**
   * Returns true if the field's original value are updated when the field is
   * bound.
   * 
   * @return true if original value is updated
   */
  public boolean isUpdateOriginalValue() {
    return updateOriginalValue;
  }

  /**
   * Sets the form binding's store. When set, edits will done using record
   * instances from the store, rather than editing the model directly.
   * 
   * @param store the store
   */
  public void setStore(Store store) {
    this.store = store;
  }

  /**
   * True to update the field's original value when bound (defaults to false).
   * 
   * @param update true to update the original value
   */
  public void setUpdateOriginalValue(boolean update) {
    this.updateOriginalValue = update;
    for (FieldBinding b : getBindings()) {
      b.setUpdateOriginalValue(update);
    }
  }

}
