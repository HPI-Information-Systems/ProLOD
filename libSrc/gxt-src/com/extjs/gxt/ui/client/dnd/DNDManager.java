/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.dnd;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Util;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

class DNDManager {

  private static DNDManager manager;

  static DNDManager get() {
    if (manager == null) {
      manager = new DNDManager();
    }
    return manager;
  }

  private List<DropTarget> targets = new ArrayList<DropTarget>();
  private DropTarget currentTarget;

  void registerDropTarget(DropTarget target) {
    targets.add(target);
  }

  void unregisterDropTarget(DropTarget target) {
    targets.remove(target);
  }

  void handleDragCancelled(DragSource source, DNDEvent event) {
    source.onDragCancelled(event);
    source.fireEvent(Events.DragCancel, event);
    if (currentTarget != null) {
      currentTarget.onDragCancelled(event);
      currentTarget = null;
    }
  }

  void handleDragMove(DragSource source, DNDEvent event) {
    DropTarget target = getTarget(source, event.getTarget());

    // no target with current
    if (target == null) {
      if (currentTarget != null) {
        currentTarget.handleDragLeave(event);
        currentTarget = null;
      }
      return;
    }

    // match move
    if (target == currentTarget) {
      event.setCancelled(true);
      event.setDropTarget(currentTarget);
      currentTarget.onDragMove(event);
      currentTarget.fireEvent(Events.DragMove, event);
      if (event.isCancelled()) {
        Insert.get().hide();
      } else {
        currentTarget.showFeedback(event);
      }
      return;
    }

    if (target != currentTarget) {
      if (currentTarget != null) {
        currentTarget.handleDragLeave(event);
        currentTarget = null;
      }

      currentTarget = target;
    }

    if (!currentTarget.isAllowSelfAsSource() && source.getComponent() == currentTarget.getComponent()) {
      currentTarget = null;
      return;
    }

    // entering
    event.setCancelled(true);
    event.setDropTarget(currentTarget);
    currentTarget.handleDragEnter(event);
    if (event.isCancelled()) {
      Insert.get().hide();
      currentTarget = null;
    } else {
      currentTarget.showFeedback(event);
    }
  }

  void handleDragStart(DragSource source, DNDEvent event) {
    source.onDragStart(event);
    if (event.getData() == null || !source.fireEvent(Events.DragStart, event)) {
      event.setCancelled(true);
      event.getDragEvent().setCancelled(true);
      return;
    }
    source.setData(event.getData());
    source.statusProxy.setStatus(false);
  }

  void handleDragEnd(DragSource source, DNDEvent event) {
    if (currentTarget != null) {
      event.setDropTarget(currentTarget);
      event.setOperation(currentTarget.getOperation());
    }
    if (currentTarget != null && event.getStatus().getStatus()) {
      source.onDragDrop(event);
      source.fireEvent(Events.Drop, event);

      currentTarget.handleDrop(event);
      currentTarget.fireEvent(Events.Drop, event);
    } else {
      source.onDragFail(event);
      source.fireEvent(Events.DragFail, event);
    }
    currentTarget = null;
    Insert.get().hide();

  }

  protected DropTarget getTarget(DragSource source, Element elem) {
    DropTarget target = null;
    for (DropTarget t : targets) {
      if (t.isEnabled()
          && Util.equalWithNull(t.getGroup(), source.getGroup())
          && DOM.isOrHasChild(t.component.getElement(), elem)
          && (target == null || (target != null && DOM.isOrHasChild(target.component.getElement(),
              t.component.getElement())))) {
        target = t;
      }
    }
    return target;
  }

}
