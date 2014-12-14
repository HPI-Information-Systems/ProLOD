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

import java.util.ArrayList;

import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;

import de.hpi.fgis.ldp.client.mvp.clustertree.ClusterTreeView;
import de.hpi.fgis.ldp.client.service.AbstractCallbackDisplay;

public abstract class AbstractDialogView implements DialogView {
  // private final WaitingPanel waitingPanel;
  protected final ArrayList<ICloseListener> closeListeners = new ArrayList<ICloseListener>(1);
  protected Window window = new Window();

  protected Widget widget = null;
  protected final AbstractCallbackDisplay callback;

  protected AbstractDialogView() {
    this(500, 300);
  }

  protected AbstractDialogView(int width, int heigth) {
    // this.waitingPanel = new WaitingPanel();

    this.window.setBodyBorder(false);
    this.window.setWidth(width);
    this.window.setHeight(heigth);
    this.window.setLayout(new FitLayout());

    this.window.addWindowListener(new WindowListener() {
      @Override
      public void windowHide(WindowEvent we) {
        super.windowHide(we);

        // Info.display("Dialog", "Dialog closed");
      }
    });
    this.window.addWindowListener(new WindowListener() {
      @Override
      public void windowHide(WindowEvent we) {
        super.windowHide(we);
        if (showOnHideView != null) {
          showOnHideView.show();
        }
        if (closeListeners.size() > 0) {
          for (ICloseListener currentListener : closeListeners) {
            if (currentListener != null) {
              currentListener.onClose();
            }
          }
        }
      }
    });

    this.window.setModal(true);
    this.window.setShadow(true);

    this.callback = new AbstractCallbackDisplay(this.window) {
      @Override
      public void resetToDefault() {
        this.setContent(widget);
      }
    };
  }

  protected DialogView showOnHideView = null;

  @Override
  public void showOnHide(DialogView otherView) {
    this.showOnHideView = otherView;
  }

  /**
   * Returns this widget as the {@link ClusterTreeView#asWidget()} value.
   */
  @Override
  public Window asWidget() {
    return this.window;
  }

  // //
  // public void startProcessing() {
  // this.setWidget(this.waitingPanel);
  // if(!this.isVisible()) {
  // this.show();
  // }
  // }

  // public void stopProcessing() {
  // }
  //
  // private void processing() {
  //
  // }
  @Override
  public void hide() {
    if (this.window.isVisible()) {
      // AbstractDialogView.this.startProcessing();
      this.window.hide();
    }
  }

  @Override
  public boolean isVisible() {
    return this.window.isVisible();
  }

  @Override
  public void show() {
    if (!this.window.isVisible()) {
      this.window.show();
    }
  }

  //
  protected void setWidget(final Widget component) {
    this.widget = component;
    this.callback.resetToDefault();
  }

  protected void setContent2(final Widget component) {
    this.widget = component;
  }

  @Override
  public void addOnCloseListener(ICloseListener listener) {
    this.closeListeners.add(listener);
  }

  @Override
  public void startProcessing() {
    this.callback.startProcessing();
  }

  @Override
  public void stopProcessing() {
    this.callback.stopProcessing();
  }

  @Override
  public void displayError() {
    this.callback.displayError();
  }
}
