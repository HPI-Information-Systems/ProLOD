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
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.client.gin;

import net.customware.gwt.dispatch.client.DefaultDispatchAsync;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.DefaultEventBus;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.gin.AbstractPresenterModule;
import net.customware.gwt.presenter.client.place.PlaceManager;

import com.google.inject.Singleton;
import com.google.inject.name.Names;

import de.hpi.fgis.ldp.client.mvp.AppPresenter;
import de.hpi.fgis.ldp.client.mvp.clustertree.ClusterTreePresenter;
import de.hpi.fgis.ldp.client.mvp.clustertree.ClusterTreeView;
import de.hpi.fgis.ldp.client.mvp.dialog.cluster.ClusterInfoPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.cluster.ClusterInfoView;
import de.hpi.fgis.ldp.client.mvp.dialog.dataimport.ImportPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.dataimport.ImportView;
import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressView;
import de.hpi.fgis.ldp.client.mvp.dialog.subject.SubjectDetailsPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.subject.SubjectDetailsView;
import de.hpi.fgis.ldp.client.mvp.dialog.subject.SubjectSamplePresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.subject.SubjectSampleView;
import de.hpi.fgis.ldp.client.mvp.main.MainWidgetManagerButtonNavView;
import de.hpi.fgis.ldp.client.mvp.main.MainWidgetManagerPresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.DatatypePresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.DatatypeView;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.GeneralPresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.GeneralView;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.NormalizedPatternPresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.NormalizedPatternView;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.ObjectPresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.ObjectView;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.PatternPresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.PatternView;
import de.hpi.fgis.ldp.client.service.AnalyticDispatchAsync;
import de.hpi.fgis.ldp.client.util.exception.ExceptionHandler;

public class ProLODClientModule extends AbstractPresenterModule {
  // #################
  // ### constants ###
  // #################
  // maximal number of rows within a table view
  public static final int MAX_ENTRY_COUNT = 30;
  // maximal number of elements in a bar chart
  public static final int MAX_BARCHART_ENTRY_COUNT = 16;
  // maximal number of elements in a pie chart
  public static final int MAX_PIECHART_ENTRY_COUNT = 7;
  // number of ms between two progress update calls
  public static final int PROGRESS_REFRESH_DELAY = 30000;
  // url to the gxt chart binary
  public static final String OPEN_FLASH_CHART_URL = "resources/chart/open-flash-chart.swf";

  @Override
  protected void configure() {
    bind(EventBus.class).to(DefaultEventBus.class).in(Singleton.class);
    bind(PlaceManager.class).in(Singleton.class);

    bindPresenter(ClusterTreePresenter.class, ClusterTreePresenter.Display.class,
        ClusterTreeView.class);
    bindPresenter(MainWidgetManagerPresenter.class, MainWidgetManagerPresenter.Display.class,
        MainWidgetManagerButtonNavView.class);

    bindPresenter(GeneralPresenter.class, GeneralPresenter.Display.class, GeneralView.class);
    bindPresenter(DatatypePresenter.class, DatatypePresenter.Display.class, DatatypeView.class);
    bindPresenter(NormalizedPatternPresenter.class, NormalizedPatternPresenter.Display.class,
        NormalizedPatternView.class);
    bindPresenter(PatternPresenter.class, PatternPresenter.Display.class, PatternView.class);
    bindPresenter(ObjectPresenter.class, ObjectPresenter.Display.class, ObjectView.class);

    bindPresenter(SubjectSamplePresenter.class, SubjectSamplePresenter.Display.class,
        SubjectSampleView.class);
    bindPresenter(SubjectDetailsPresenter.class, SubjectDetailsPresenter.Display.class,
        SubjectDetailsView.class);
    bindPresenter(ClusterInfoPresenter.class, ClusterInfoPresenter.Display.class,
        ClusterInfoView.class);
    bindPresenter(ImportPresenter.class, ImportPresenter.Display.class, ImportView.class);
    bindPresenter(ProgressPresenter.class, ProgressPresenter.Display.class, ProgressView.class);

    bindConstant().annotatedWith(Names.named("gui.admin")).to(this.isAdminMode());

    bindConstant().annotatedWith(Names.named("gui.maxTableRowCount")).to(MAX_ENTRY_COUNT);
    bindConstant().annotatedWith(Names.named("gui.progressRefreshDelay"))
        .to(PROGRESS_REFRESH_DELAY);
    bindConstant().annotatedWith(Names.named("gui.gxt.chartURL")).to(OPEN_FLASH_CHART_URL);
    bindConstant().annotatedWith(Names.named("gui.maxPieChartElements")).to(
        MAX_PIECHART_ENTRY_COUNT);
    bindConstant().annotatedWith(Names.named("gui.maxBarChartElements")).to(
        MAX_BARCHART_ENTRY_COUNT);

    bind(AppPresenter.class);

    bind(DefaultDispatchAsync.class).in(Singleton.class);

    bind(DispatchAsync.class).to(AnalyticDispatchAsync.class).in(Singleton.class);
    // bind(DispatchAsync.class).to(DefaultDispatchAsync.class).in(Singleton.class);
    // bind(DispatchAsync.class).to(CachingDispatchAsync.class).in(Singleton.class);

    bind(ExceptionHandler.class).in(Singleton.class);
  }

  protected boolean isAdminMode() {
    return false;
  }

  public static class Admin extends ProLODClientModule {
    @Override
    protected boolean isAdminMode() {
      return true;
    }
  }
}
