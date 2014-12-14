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

package de.hpi.fgis.ldp.client.util.exception;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;

import de.hpi.fgis.ldp.shared.exception.DataConnectionException;
import de.hpi.fgis.ldp.shared.exception.OutOfMemoryException;
import de.hpi.fgis.ldp.shared.exception.RPCException;

public class ExceptionHandler {

  public interface AcceptanceListener {
    public void onAccept();
  }

  public boolean handle(Throwable t) {
    return this.handle(t, null);
  }

  public boolean handle(Throwable t, final AcceptanceListener listener) {
    if (t instanceof OutOfMemoryException) {
      // this exception can occur from time to time
      // show message box to indicate that the job couldn't be done
      errorBox(
          "Not enough memory!",
          "Sorry, we are unable to allocate enough memory for this request! You may try again later.",
          new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent be) {
              if (listener != null) {
                listener.onAccept();
              }
            }
          });
    } else if (t instanceof DataConnectionException) {
      // this exception shouldn't occur
      // show message box -> please inform the it crowd
      errorBox(
          "Connection lost!",
          "Sorry, we are unable connect to the data source! Please ask the support for further help.",
          new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent be) {
              if (listener != null) {
                listener.onAccept();
              }
            }
          });
    } else if (t instanceof RPCException) {
      // this is e serious exception which normally indicates an error in
      // the server code
      // show message box with detailed information and ask to send it to
      // the it crowd
      errorBox("Internal failure!",
          "Sorry, we are unable to execute this request! Please ask the support for further help.",
          new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent be) {
              if (listener != null) {
                listener.onAccept();
              }
            }
          });
    } else {
      // unexpected error has to be treated seriously
      // show message box with detailed information and ask to send it to
      // the it crowd
      errorBox("Unknown failure!",
          "Sorry, we are unable to execute this request! Please ask the support for further help.",
          new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent be) {
              if (listener != null) {
                listener.onAccept();
              }
            }
          });
    }

    return false;
  }

  /**
   * Displays a standard read-only message box with an OK button (comparable to the basic JavaScript
   * alert prompt).
   * 
   * @param title the title bar text
   * @param msg the message box body text
   * @param callback listener to be called when the box is closed
   * @return the new message box instance
   */
  public MessageBox errorBox(String title, String msg, Listener<MessageBoxEvent> callback) {
    MessageBox box = new MessageBox();
    box.setTitle(title);
    box.setMessage(msg);
    box.addCallback(callback);
    box.setButtons(MessageBox.OK);
    box.setIcon(MessageBox.WARNING);
    box.show();
    return box;
  }
}
