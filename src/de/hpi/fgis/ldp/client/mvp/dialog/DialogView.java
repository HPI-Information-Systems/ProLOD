/*-
 * Copyright 2012 by: Hasso Plattner Institute for Software Systems Engineering 
 * Prof.-Dr.-Helmert-Str. 2-3
 * 14482 Potsdam, Germany
 * Potsdam District Court, HRB 12184
 * Authorized Representative Managing Director: Prof. Dr. Christoph Meinel
 * http://www.hpi-web.de/
 * 
 * Information Systems Group, http://www.hpi-web.de/naumann/
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.client.mvp.dialog;

import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import de.hpi.fgis.ldp.client.service.CallbackDisplay;

public interface DialogView extends WidgetDisplay, CallbackDisplay {
  public interface ICloseListener {
    public void onClose();
  }

  public void show();

  public void hide();

  public boolean isVisible();

  public void showOnHide(DialogView view);

  public void addOnCloseListener(ICloseListener listener);

  // public void processing();
}
