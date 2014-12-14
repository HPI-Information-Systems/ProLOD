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

package de.hpi.fgis.ldp.client.service;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.google.gwt.user.client.ui.Widget;

import de.hpi.fgis.ldp.client.mvp.ErrorPanel;
import de.hpi.fgis.ldp.client.mvp.WaitingPanel;

public abstract class AbstractCallbackDisplay implements CallbackDisplay {
  private final ContentPanel[] containingPanels;
  private final WaitingPanel[] wait;
  private final ErrorPanel[] error;

  protected AbstractCallbackDisplay(ContentPanel... containingPanel) {
    this.containingPanels = containingPanel;

    wait = new WaitingPanel[containingPanel.length];
    error = new ErrorPanel[containingPanel.length];

    for (int i = 0; i < containingPanel.length; i++) {
      wait[i] = new WaitingPanel();
      error[i] = new ErrorPanel();
    }
  }

  protected void setContent(Widget... newPanels) {
    if (newPanels != null) {
      for (int i = 0; i < newPanels.length && i < containingPanels.length; i++) {
        containingPanels[i].removeAll();
        if (newPanels[i] != null) {
          containingPanels[i].add(newPanels[i], new RowData(1, 1));
        } else {
          containingPanels[i].add(error[i], new RowData(1, 1));
        }

        // recalculate sizes of members
        containingPanels[i].layout();
      }
    } else {
      this.displayError();
    }
  }

  @Override
  public void displayError() {
    this.setContent(error);
  }

  @Override
  public void startProcessing() {
    this.setContent(wait);
  }

  @Override
  public void stopProcessing() {
    this.resetToDefault();
  }

  public abstract void resetToDefault();
}
