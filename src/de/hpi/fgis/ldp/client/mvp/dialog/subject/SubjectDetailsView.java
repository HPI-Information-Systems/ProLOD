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

package de.hpi.fgis.ldp.client.mvp.dialog.subject;

import de.hpi.fgis.ldp.client.mvp.dialog.AbstractDialogView;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel;

public class SubjectDetailsView extends AbstractDialogView implements
    SubjectDetailsPresenter.Display {

  public SubjectDetailsView() {
    super(800, 500);
    // this.setModal(true);
    //
    // this.show();
    // // recalculate sizes of members
    // this.layout();
  }

  @Override
  public void setDataTable(final DataTablePanel table) {
    synchronized (this) {
      this.setContent2(table);
      this.show();
    }
  }
}
