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

package de.hpi.fgis.ldp.client.mvp.main.content.profiling;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter;
import de.hpi.fgis.ldp.client.mvp.dialog.progress.ProgressPresenter.CompletionListener;
import de.hpi.fgis.ldp.client.mvp.main.content.AbstractMainContentPresenter;
import de.hpi.fgis.ldp.client.mvp.main.content.MainContentView;
import de.hpi.fgis.ldp.client.service.AsyncDisplayCallback;
import de.hpi.fgis.ldp.client.service.CallbackDisplay;
import de.hpi.fgis.ldp.client.view.datatable.AbstractDataTableChartPanel;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel.MultiSelectionListener;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel.SingleSelectionListener;
import de.hpi.fgis.ldp.client.view.datatable.GXTDataTableChartPanel;
import de.hpi.fgis.ldp.client.view.datatable.GXTDataTableChartPanel.ChartType;
import de.hpi.fgis.ldp.shared.data.ARSetModel;
import de.hpi.fgis.ldp.shared.data.AssociationRuleModel;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.OntologyAlignmentModel;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.data.SuggestionSetModel;
import de.hpi.fgis.ldp.shared.data.SynonymPairModel;
import de.hpi.fgis.ldp.shared.data.SynonymSetModel;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.data.table.impl.AntonymTable;
import de.hpi.fgis.ldp.shared.data.table.impl.AssociationRuleTable;
import de.hpi.fgis.ldp.shared.data.table.impl.DataColumn;
import de.hpi.fgis.ldp.shared.data.table.impl.DataTable;
import de.hpi.fgis.ldp.shared.data.table.impl.SynonymTable;
import de.hpi.fgis.ldp.shared.data.table.impl.UniquenessTable;
import de.hpi.fgis.ldp.shared.event.DataTableReceivedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEvent;
import de.hpi.fgis.ldp.shared.event.cluster.ClusterChangedEventHandler;
import de.hpi.fgis.ldp.shared.event.profiling.LinkLiteralStatisticsReceivedEvent;
import de.hpi.fgis.ldp.shared.event.profiling.PredicateStatisticsReceivedEvent;
import de.hpi.fgis.ldp.shared.event.rules.AntonymReceivedEvent;
import de.hpi.fgis.ldp.shared.event.rules.AntonymReceivedEventHandler;
import de.hpi.fgis.ldp.shared.event.rules.AssociationRuleReceivedEvent;
import de.hpi.fgis.ldp.shared.event.rules.AssociationRuleReceivedEventHandler;
import de.hpi.fgis.ldp.shared.event.rules.FactGenerationReceivedEvent;
import de.hpi.fgis.ldp.shared.event.rules.OntologyAlignmentReceivedEvent;
import de.hpi.fgis.ldp.shared.event.rules.OntologyAlignmentReceivedEventHandler;
import de.hpi.fgis.ldp.shared.event.rules.SuggestionSubjectDetailsReceivedEvent;
import de.hpi.fgis.ldp.shared.event.rules.SuggestionViewReceivedEvent;
import de.hpi.fgis.ldp.shared.event.rules.SuggestionViewReceivedEventHandler;
import de.hpi.fgis.ldp.shared.event.rules.SynonymsReceivedEvent;
import de.hpi.fgis.ldp.shared.event.rules.SynonymsReceivedEventHandler;
import de.hpi.fgis.ldp.shared.event.rules.UniquenessReceivedEvent;
import de.hpi.fgis.ldp.shared.event.rules.UniquenessReceivedEventHandler;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.cluster.SubjectDetailRequest;
import de.hpi.fgis.ldp.shared.rpc.profiling.LinkLiteralStatisticsRequest;
import de.hpi.fgis.ldp.shared.rpc.profiling.PredicateStatisticsRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.AntonymRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.AntonymResult;
import de.hpi.fgis.ldp.shared.rpc.rules.AssociationRuleRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.AssociationRuleResult;
import de.hpi.fgis.ldp.shared.rpc.rules.FactGenerationRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.FactGenerationResult;
import de.hpi.fgis.ldp.shared.rpc.rules.OntologyAlignmentRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.OntologyAlignmentResult;
import de.hpi.fgis.ldp.shared.rpc.rules.SuggestionViewRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.SuggestionViewResult;
import de.hpi.fgis.ldp.shared.rpc.rules.SynonymRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.SynonymResult;
import de.hpi.fgis.ldp.shared.rpc.rules.UniquenessRequest;
import de.hpi.fgis.ldp.shared.rpc.rules.UniquenessResult;

@Singleton
public class GeneralPresenter extends AbstractMainContentPresenter<GeneralPresenter.Display> {
  // inject as setting
  protected @Inject @Named("gui.maxTableRowCount") int entryCount;
  protected Cluster currentCluster = null;

  protected ProgressPresenter progressPresenter;
  protected DatatypePresenter datatypePresenter;
  @Inject
  protected AsyncDisplayCallback.Builder callbackBuilder;

  public interface DataRequestSource {
    public void loadAntonymSubjects();

    public void loadAssociationRules();

    public void loadSynonyms();

    public void loadNewFacts();

    public void loadSuggestionInterface();

    public void loadOntologyImprovements();

    public void loadUniqueness();
  }

  public interface Display extends MainContentView {
    public void setDataRequestSource(final DataRequestSource source);

    public void setPredicatesTable(DataTablePanel table);

    public void setAntonymTable(DataTablePanel table);

    public void setAssociationRuleTable(DataTablePanel table);

    // public void loadAssociationRuleTableLazy(boolean value);

    public void showAntonymSubjectSamples(DataTablePanel table);

    public void showUniquenessSubjectSamples(DataTablePanel table);

    public void showAssociationRuleSubjectSamples(DataTablePanel table);

    public void setTopPredicatesChart(AbstractDataTableChartPanel chart);

    public void setLinkLiteralChart(AbstractDataTableChartPanel chart);

    public void resetView();

    public CallbackDisplay getPredicatesDisplay();

    public CallbackDisplay getOntologyAlignmentDisplay();

    public CallbackDisplay getLinkLiteralDisplay();

    public CallbackDisplay getAntonymDisplay();

    public CallbackDisplay getAssociationRuleDisplay();

    public CallbackDisplay getSynonymsDisplay();

    public CallbackDisplay getFactGenerationDisplay();

    public CallbackDisplay getSuggestionDisplay();

    public CallbackDisplay getUniquenessDisplay();

    public void setSynonymTable(DataTablePanel tablePanel);

    public void setFactGenerationTable(DataTablePanel tablePanel);

    public void setSuggestionTable(DataTablePanel tablePanel);

    public void setUniquenessTable(DataTablePanel tablePanel);

    public void setOntologyAlignmentPanel(VerticalPanel panel);

    public void showSynonymRanges(DataTablePanel samplePanel);

    // public void showSuggestionBox(DockLayoutPanel panel);
    public void showSubjectSchema(Subject subject, DataTablePanel tablePanel);

    public void setSuggestionRuleMatrix(SuggestionSetModel model);

  }

  /**
   * The message displayed to the user when the server cannot be reached or returns an error.
   */

  @Inject
  public GeneralPresenter(final Display display, final EventBus eventBus,
      final DispatchAsync dispatcher) {
    super(display, eventBus, dispatcher);
  }

  @Override
  public Place getPlace() {
    return PLACE;
  }

  public GeneralPresenter init(ProgressPresenter progressPresenter,
      DatatypePresenter datatypePresenter) {
    this.progressPresenter = progressPresenter;
    this.datatypePresenter = datatypePresenter;
    return this;
  }

  @Override
  protected void onBind() {
    this.display.setDataRequestSource(new DataRequestSource() {
      @Override
      public void loadAssociationRules() {
        if (currentCluster != null) {
          requestAssociationRules(currentCluster);
        }
      }

      @Override
      public void loadAntonymSubjects() {
        if (currentCluster != null) {
          requestAntonyms(currentCluster);
        }
      }

      @Override
      public void loadSynonyms() {
        if (currentCluster != null) {
          requestSynonyms(currentCluster);
        }

      }

      @Override
      public void loadNewFacts() {
        if (currentCluster != null) {
          requestNewFacts(currentCluster);
        }

      }

      @Override
      public void loadSuggestionInterface() {
        if (currentCluster != null) {
          requestSuggestionInterface(currentCluster);
        }

      }

      @Override
      public void loadOntologyImprovements() {
        if (currentCluster != null) {
          requestOntologyAlignment(currentCluster);
        }

      }

      @Override
      public void loadUniqueness() {
        if (currentCluster != null) {
          requestUniqueness(currentCluster);
        }
      }

    });
    eventBus.addHandler(ClusterChangedEvent.TYPE, new ClusterChangedEventHandler() {
      @Override
      public void onClusterChanged(ClusterChangedEvent event) {
        final Cluster cluster = event.getCluster();
        synchronized (GeneralPresenter.this) {
          GeneralPresenter.this.currentCluster = cluster;
        }
        // if cluster is appended to a working process, stop
        // here
        if (!cluster.isInProgress()) {

          GeneralPresenter.this.getDisplay().resetView();

          // load property stats
          GeneralPresenter.this.requestPropertyStatistics(cluster, 0);
          // load link-literal-ratios
          GeneralPresenter.this.requestLinkLiteralStatistics(cluster);
        }
      }
    });

    eventBus.addHandler(PredicateStatisticsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<PredicateStatisticsReceivedEvent>() {
          @Override
          public void onDataTableReceived(final PredicateStatisticsReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            // result for unknown cluster
            synchronized (GeneralPresenter.this) {
              if (cluster == null || !cluster.equals(GeneralPresenter.this.currentCluster)) {
                return;
              }
            }

            // start waiting event
            if (event.getDataTable() == null) {
              GeneralPresenter.this.getDisplay().setPredicatesTable(null);
              GeneralPresenter.this.getDisplay().setTopPredicatesChart(null);

              return;
            }

            // create data table
            final IDataTable table = event.getDataTable();
            final int start = event.getOffset();
            final DataTablePanel tablePanel = new DataTablePanel();

            tablePanel.setSelectionListenter(new MultiSelectionListener() {
              @Override
              public void onSelection(int[] selectedRows) {
                final ArrayList<Predicate> predicates =
                    new ArrayList<Predicate>(selectedRows.length);

                for (int rowIndex : selectedRows) {
                  // first colum is the predicate
                  predicates.add((Predicate) table.getColumn(0).getElement(rowIndex));
                }
                // request the data type distribution &
                // link literal ratio
                GeneralPresenter.this.requestDetails(cluster, predicates);

              }
            });

            if (start > 0) {
              tablePanel.setToolButton("x-tool-left", new SelectionListener<IconButtonEvent>() {
                @Override
                public void componentSelected(IconButtonEvent ce) {
                  GeneralPresenter.this.requestPropertyStatistics(event.getCluster(), start
                      - GeneralPresenter.this.entryCount);
                }
              });
            }

            if (table.getRowCount() >= GeneralPresenter.this.entryCount) {
              tablePanel.setToolButton("x-tool-right", new SelectionListener<IconButtonEvent>() {
                @Override
                public void componentSelected(IconButtonEvent ce) {
                  GeneralPresenter.this.requestPropertyStatistics(event.getCluster(), start
                      + GeneralPresenter.this.entryCount);
                }
              });
            }
            tablePanel.setData(table);

            tablePanel.setTableTitle("Predicate Distribution");

            // set table
            GeneralPresenter.this.getDisplay().setPredicatesTable(tablePanel);

            if (start <= 0) {
              // create data chart
              GXTDataTableChartPanel chart =
                  new GXTDataTableChartPanel(ChartType.HBar, event.getDataTable(), 0, 1, 2);

              chart.setChartTitle("Predicate Distribution");

              // set chart
              GeneralPresenter.this.getDisplay().setTopPredicatesChart(chart);
            }
          }
        });

    eventBus.addHandler(AntonymReceivedEvent.TYPE, new AntonymReceivedEventHandler() {

      @Override
      public void onAntonymReceived(AntonymReceivedEvent event) {
        final Cluster cluster = event.getCluster();
        // result for unknown cluster
        synchronized (GeneralPresenter.this) {
          if (cluster == null || !cluster.equals(GeneralPresenter.this.currentCluster)) {
            return;
          }
        }
        // start waiting event
        if (event.getAntonyms() == null) {
          GeneralPresenter.this.getDisplay().setAntonymTable(null);

          return;
        }

        final DataTablePanel tablePanel = new DataTablePanel();
        final AntonymTable table = new AntonymTable(event.getAntonyms());
        tablePanel.setSelectionListenter(new SingleSelectionListener() {
          @Override
          public void onSelection(int selectedRow) {
            IDataTable sampleTable = table.getSampleSubjects(selectedRow);
            DataTablePanel samplePanel = new DataTablePanel();
            samplePanel.setTableTitle("Sample Subjects");
            samplePanel.setData(sampleTable);
            GeneralPresenter.this.getDisplay().showAntonymSubjectSamples(samplePanel);
          }
        });

        tablePanel.setData(table);

        tablePanel.setTableTitle("Antonyms");

        GeneralPresenter.this.getDisplay().setAntonymTable(tablePanel);
      }
    });

    eventBus.addHandler(UniquenessReceivedEvent.TYPE, new UniquenessReceivedEventHandler() {

      @Override
      public void onUniquenessReceived(UniquenessReceivedEvent event) {
        final Cluster cluster = event.getCluster();
        // result for unknown cluster
        synchronized (GeneralPresenter.this) {
          if (cluster == null || !cluster.equals(GeneralPresenter.this.currentCluster)) {
            return;
          }
        }
        // start waiting event
        if (event.getUniqueness() == null) {
          GeneralPresenter.this.getDisplay().setUniquenessTable(null);

          return;
        }

        final DataTablePanel tablePanel = new DataTablePanel();
        final UniquenessTable table = new UniquenessTable(event.getUniqueness());
        /*
         * tablePanel .setSelectionListenter(new SingleSelectionListener() { public void
         * onSelection(int selectedRow) { IDataTable sampleTable = table
         * .getSampleSubjects(selectedRow); DataTablePanel samplePanel = new DataTablePanel();
         * samplePanel .setTableTitle("Sample Subjects"); samplePanel.setData(sampleTable);
         * GeneralPresenter.this.getDisplay() .showUniquenessSubjectSamples( samplePanel); } });
         */

        tablePanel.setData(table);

        tablePanel.setTableTitle("Uniqueness");

        GeneralPresenter.this.getDisplay().setUniquenessTable(tablePanel);
      }
    });

    eventBus.addHandler(AssociationRuleReceivedEvent.TYPE,
        new AssociationRuleReceivedEventHandler() {
          @Override
          public void onAssociationRuleReceived(AssociationRuleReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            // result for unknown cluster
            synchronized (GeneralPresenter.this) {
              if (cluster == null || !cluster.equals(GeneralPresenter.this.currentCluster)) {
                return;
              }
            }
            // start waiting event
            if (event.getModel() == null) {
              GeneralPresenter.this.getDisplay().setAssociationRuleTable(null);

              return;
            }

            final DataTablePanel tablePanel = new DataTablePanel();
            final AssociationRuleTable table = new AssociationRuleTable(event.getModel());
            tablePanel.setSelectionListenter(new SingleSelectionListener() {
              @Override
              public void onSelection(int selectedRow) {
                IDataTable sampleTable = table.getSampleSubjects(selectedRow);
                DataTablePanel samplePanel = new DataTablePanel();
                samplePanel.setTableTitle("Sample Subjects");
                samplePanel.setData(sampleTable);
                GeneralPresenter.this.getDisplay().showAntonymSubjectSamples(samplePanel);
              }
            });

            tablePanel.setData(table);

            tablePanel.setTableTitle("Association Rules");

            // PredicatePresenter.this.getDisplay().loadAssociationRuleTableLazy(false);
            GeneralPresenter.this.getDisplay().setAssociationRuleTable(tablePanel);
          }
        });

    eventBus.addHandler(SynonymsReceivedEvent.TYPE, new SynonymsReceivedEventHandler() {
      @Override
      public void onSynonymsReceived(SynonymsReceivedEvent event) {
        final Cluster cluster = event.getCluster();
        // result for unknown cluster
        synchronized (GeneralPresenter.this) {
          if (cluster == null || !cluster.equals(GeneralPresenter.this.currentCluster)) {
            return;
          }
        }
        // start waiting event
        if (event.getModel() == null) {
          GeneralPresenter.this.getDisplay().setSynonymTable(null);

          return;
        }

        final DataTablePanel tablePanel = new DataTablePanel();
        final SynonymTable table = new SynonymTable(event.getModel());
        tablePanel.setSelectionListenter(new SingleSelectionListener() {
          @Override
          public void onSelection(int selectedRow) {
            IDataTable sampleTable = table.getSubjectObjects(selectedRow);
            DataTablePanel samplePanel = new DataTablePanel();
            samplePanel.setTableTitle("Sample Subjects/Objects");
            samplePanel.setData(sampleTable);

            GeneralPresenter.this.getDisplay().showSynonymRanges(samplePanel);
          }
        });

        tablePanel.setData(table);

        tablePanel.setTableTitle("Synonyms");

        // PredicatePresenter.this.getDisplay().loadAssociationRuleTableLazy(false);
        GeneralPresenter.this.getDisplay().setSynonymTable(tablePanel);
      }
    });
    eventBus.addHandler(FactGenerationReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<FactGenerationReceivedEvent>() {
          @Override
          public void onDataTableReceived(FactGenerationReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            // result for unknown cluster
            synchronized (GeneralPresenter.this) {
              if (cluster == null || !cluster.equals(GeneralPresenter.this.currentCluster)) {
                return;
              }
            }
            // start waiting event
            if (event.getDataTable() == null) {
              GeneralPresenter.this.getDisplay().setFactGenerationTable(null);

              return;
            }

            final DataTablePanel tablePanel = new DataTablePanel();
            tablePanel.setData(event.getDataTable());
            // tablePanel.setSelectionListenter(new
            // SingleSelectionListener() {
            // public void onSelection(int selectedRow) {
            // IDataTable sampleTable =
            // table.getSampleObjects(selectedRow);
            // DataTablePanel samplePanel = new DataTablePanel();
            // samplePanel.setTableTitle("Sample Objects");
            // samplePanel.setData(sampleTable);
            // GeneralPresenter.this.getDisplay().showSynonymRanges(samplePanel);
            // }
            // });

            tablePanel.setTableTitle("New Generated Facts");

            // PredicatePresenter.this.getDisplay().loadAssociationRuleTableLazy(false);
            GeneralPresenter.this.getDisplay().setFactGenerationTable(tablePanel);
          }
        });

    eventBus.addHandler(SuggestionViewReceivedEvent.TYPE, new SuggestionViewReceivedEventHandler() {
      @Override
      public void onSuggestionViewReceived(SuggestionViewReceivedEvent event) {
        final Cluster cluster = event.getCluster();
        // result for unknown cluster
        synchronized (GeneralPresenter.this) {
          if (cluster == null || !cluster.equals(GeneralPresenter.this.currentCluster)) {
            return;
          }
        }
        // start waiting event
        if (event.getModel() == null) {
          GeneralPresenter.this.getDisplay().setSuggestionTable(null);

          return;
        }
        final List<Subject> subjects = event.getModel().getSubjects();

        final DataColumn<Subject> subjectColumn = new DataColumn<Subject>("Subject", true);
        for (int i = 0; i < subjects.size(); i++) {
          subjectColumn.setElement(i, subjects.get(i));
        }

        final DataTablePanel tablePanel = new DataTablePanel();

        tablePanel.setSelectionListenter(new SingleSelectionListener() {
          @Override
          public void onSelection(int selectedRow) {
            final Subject selectedSubject = subjectColumn.getElement(selectedRow);
            requestSubjectDetails(cluster, selectedSubject);
          }

        });
        tablePanel.setData(new DataTable(subjectColumn));
        tablePanel.setTableTitle("Sample of subjects in the dataset");
        GeneralPresenter.this.getDisplay().setSuggestionTable(tablePanel);
        GeneralPresenter.this.getDisplay().setSuggestionRuleMatrix(event.getModel());
      }
    });

    eventBus.addHandler(OntologyAlignmentReceivedEvent.TYPE,
        new OntologyAlignmentReceivedEventHandler() {
          @Override
          public void onOntologyAlignmentReceived(OntologyAlignmentReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            // result for unknown cluster
            synchronized (GeneralPresenter.this) {
              if (cluster == null || !cluster.equals(GeneralPresenter.this.currentCluster)) {
                return;
              }
            }
            // start waiting event
            if (event.getModel() == null) {
              GeneralPresenter.this.getDisplay().setOntologyAlignmentPanel(null);

              return;
            }

            // TODO

            final VerticalPanel vPanel = new VerticalPanel();
            // final HorizontalPanel ontologySuggestionPanel = new
            // HorizontalPanel();
            // ontologySuggestionPanel.setAutoHeight(true);
            // ontologySuggestionPanel.setAutoWidth(true);
            // ontologySuggestionPanel.setLayout(new FitLayout());
            final DataTablePanel propertyPanel = new DataTablePanel();

            if (cluster.getDataSource().getUserView() == null
                || !cluster.getDataSource().getUserView().equals(DataSource.ONTOLOGY_USER_VIEW)) {
              propertyPanel.setTableTitle("No Ontology class");
            } else {
              propertyPanel.setTableTitle("Defined Properties for the class " + cluster.getLabel());
            }

            propertyPanel.setAutoExpandColumn("Properties by the Ontology");
            propertyPanel.setAutoHeight(true);
            propertyPanel.setAutoWidth(true);
            propertyPanel.setData(new DataTable(event.getModel().getProperties()));

            final DataTablePanel removalPanel = new DataTablePanel();
            removalPanel.setTableTitle("Properties to be removed");
            final DataTable removalTable = new DataTable(event.getModel().getRemovedProperties());
            removalTable.addColumn(event.getModel().getSourceClassRemoved());
            removalPanel.setAutoExpandColumn("Source Class");
            removalPanel.setAutoHeight(true);
            removalPanel.setAutoWidth(true);
            removalPanel.setData(removalTable);

            final DataTablePanel inclusionPanel = new DataTablePanel();
            inclusionPanel.setTableTitle("Properties to be added");
            final DataTable suggestionTab =
                new DataTable(event.getModel().getInclusionProperties());
            suggestionTab.addColumn(event.getModel().getSouceClass());
            inclusionPanel.setAutoExpandColumn("Source Class");
            inclusionPanel.setAutoHeight(true);
            inclusionPanel.setAutoWidth(true);
            inclusionPanel.setData(suggestionTab);

            vPanel.add(propertyPanel);
            vPanel.add(removalPanel);
            vPanel.add(inclusionPanel);
            vPanel.setAutoHeight(true);
            vPanel.setAutoWidth(true);
            vPanel.setLayout(new FitLayout());

            GeneralPresenter.this.getDisplay().setOntologyAlignmentPanel(vPanel);
          }
        });

    eventBus.addHandler(LinkLiteralStatisticsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<LinkLiteralStatisticsReceivedEvent>() {
          @Override
          public void onDataTableReceived(LinkLiteralStatisticsReceivedEvent event) {
            final Cluster cluster = event.getCluster();
            // result for unknown cluster
            synchronized (GeneralPresenter.this) {
              if (cluster == null || !cluster.equals(GeneralPresenter.this.currentCluster)) {
                return;
              }
            }

            // start waiting event
            if (event.getDataTable() == null) {
              GeneralPresenter.this.getDisplay().setLinkLiteralChart(null);

              return;
            }

            // create data chart
            GXTDataTableChartPanel chart =
                new GXTDataTableChartPanel(ChartType.Pie, event.getDataTable(), 0, 1);

            chart.setChartTitle("Link Literal Ratio");

            // set chart
            GeneralPresenter.this.getDisplay().setLinkLiteralChart(chart);
          }
        });

    eventBus.addHandler(SuggestionSubjectDetailsReceivedEvent.TYPE,
        new DataTableReceivedEvent.Handler<SuggestionSubjectDetailsReceivedEvent>() {
          @Override
          public void onDataTableReceived(SuggestionSubjectDetailsReceivedEvent event) {
            // final Cluster cluster = event.getCluster();
            // result for unknown cluster

            Subject subject = event.getSubject();

            // create data table
            IDataTable table = event.getDataTable();
            DataTablePanel tablePanel = new DataTablePanel();

            tablePanel.setData(table);
            tablePanel.setTableTitle(subject.getLabel());
            GeneralPresenter.this.getDisplay().showSubjectSchema(subject, tablePanel);
          }
        });

  }

  protected void requestNewFacts(final Cluster cluster) {
    this.requestActivation();
    getDispatcher().execute(
        new FactGenerationRequest(cluster),
        callbackBuilder.build(getDisplay().getFactGenerationDisplay(),
            new AsyncDisplayCallback.Handler<FactGenerationResult>() {
              @Override
              protected boolean handleSuccess(FactGenerationResult result) {
                progressPresenter.showProgress(result.getProcessIdentifier(), DataTable.class,
                    new CompletionListener<DataTable>() {
                      @Override
                      public boolean onCompletion(boolean success, DataTable data) {
                        if (success) {
                          if (data != null) {
                            getEventBus().fireEvent(new FactGenerationReceivedEvent(cluster, data));
                          } else {
                            // show
                            // empty
                            // list
                            getEventBus().fireEvent(
                                new FactGenerationReceivedEvent(cluster, new DataTable(
                                    new DataColumn<String>("", false))));
                          }
                          getDisplay().getFactGenerationDisplay().stopProcessing();
                          return true;
                        }
                        getDisplay().getFactGenerationDisplay().displayError();
                        return false;
                      }
                    }

                    , true);

                return false;
              }
            }));

  }

  protected void requestSuggestionInterface(final Cluster cluster) {
    this.requestActivation();
    getDispatcher().execute(
        new SuggestionViewRequest(cluster),
        callbackBuilder.build(getDisplay().getSuggestionDisplay(),
            new AsyncDisplayCallback.Handler<SuggestionViewResult>() {
              @Override
              protected boolean handleSuccess(SuggestionViewResult result) {
                progressPresenter.showProgress(result.getProcessIdentifier(),
                    SuggestionSetModel.class, new CompletionListener<SuggestionSetModel>() {
                      @Override
                      @SuppressWarnings("synthetic-access")
                      public boolean onCompletion(boolean success, SuggestionSetModel result) {
                        if (success) {
                          if (result != null) {
                            getEventBus().fireEvent(
                                new SuggestionViewReceivedEvent(cluster, result));
                          } else {
                            // show
                            // empty
                            // list
                            getEventBus().fireEvent(
                                new SuggestionViewReceivedEvent(cluster, new SuggestionSetModel(
                                    null, null, null)));
                          }
                          getDisplay().getSuggestionDisplay().stopProcessing();
                          return true;
                        }
                        getDisplay().getSuggestionDisplay().displayError();
                        return false;
                      }
                    }, true);

                return false;
              }
            }));

  }

  protected void requestOntologyAlignment(final Cluster cluster) {
    // start with waiting event w/o data
    this.requestActivation();
    getDispatcher().execute(
        new OntologyAlignmentRequest(cluster),
        callbackBuilder.build(getDisplay().getOntologyAlignmentDisplay(),
            new AsyncDisplayCallback.Handler<OntologyAlignmentResult>() {
              @Override
              protected boolean handleSuccess(OntologyAlignmentResult result) {
                progressPresenter.showProgress(result.getProcessIdentifier(),
                    OntologyAlignmentModel.class, new CompletionListener<OntologyAlignmentModel>() {
                      @Override
                      @SuppressWarnings("synthetic-access")
                      public boolean onCompletion(boolean success, OntologyAlignmentModel result) {
                        if (success) {
                          if (result != null) {
                            getEventBus().fireEvent(
                                new OntologyAlignmentReceivedEvent(cluster, result));
                          } else {
                            // show
                            // empty
                            // list
                            getEventBus().fireEvent(
                                new OntologyAlignmentReceivedEvent(cluster, null));
                          }
                          getDisplay().getOntologyAlignmentDisplay().stopProcessing();
                          return true;
                        }
                        getDisplay().getOntologyAlignmentDisplay().displayError();
                        return false;
                      }
                    }, true);

                return false;
              }
            }));
  }

  protected void requestSynonyms(final Cluster cluster) {
    this.requestActivation();
    getDispatcher().execute(
        new SynonymRequest(cluster),
        callbackBuilder.build(getDisplay().getSynonymsDisplay(),
            new AsyncDisplayCallback.Handler<SynonymResult>() {
              @Override
              protected boolean handleSuccess(SynonymResult result) {
                progressPresenter.showProgress(result.getProcessIdentifier(),
                    SynonymSetModel.class, new CompletionListener<SynonymSetModel>() {
                      @Override
                      @SuppressWarnings("synthetic-access")
                      public boolean onCompletion(boolean success, SynonymSetModel result) {
                        if (success) {
                          if (result != null) {
                            getEventBus().fireEvent(
                                new SynonymsReceivedEvent(cluster, result.getModelSet()));
                          } else {
                            // show empty list
                            getEventBus().fireEvent(
                                new SynonymsReceivedEvent(cluster,
                                    new ArrayList<SynonymPairModel>()));
                          }
                          getDisplay().getSynonymsDisplay().stopProcessing();
                          return true;
                        }
                        getDisplay().getSynonymsDisplay().displayError();
                        return false;
                      }
                    }, true);

                return false;
              }
            }));

  }

  protected void requestAntonyms(final Cluster cluster) {
    // start with waiting event w/o data
    this.requestActivation();
    getDispatcher().execute(
        new AntonymRequest(cluster),
        callbackBuilder.build(getDisplay().getAntonymDisplay(),
            new AsyncDisplayCallback.Handler<AntonymResult>() {
              @Override
              protected boolean handleSuccess(AntonymResult result) {
                getEventBus().fireEvent(new AntonymReceivedEvent(cluster, result.getAntonyms()));

                return true;
              }
            }));
  }

  protected void requestUniqueness(final Cluster cluster) {
    // start with waiting event w/o data
    this.requestActivation();
    getDispatcher().execute(
        new UniquenessRequest(cluster),
        callbackBuilder.build(getDisplay().getUniquenessDisplay(),
            new AsyncDisplayCallback.Handler<UniquenessResult>() {
              @Override
              protected boolean handleSuccess(UniquenessResult result) {
                getEventBus().fireEvent(
                    new UniquenessReceivedEvent(cluster, result.getUniqueness()));

                return true;
              }
            }));
  }

  protected void requestAssociationRules(final Cluster cluster) {
    // start with waiting event w/o data
    this.requestActivation();
    getDispatcher().execute(
        new AssociationRuleRequest(cluster),
        callbackBuilder.build(getDisplay().getAssociationRuleDisplay(),
            new AsyncDisplayCallback.Handler<AssociationRuleResult>() {
              @Override
              protected boolean handleSuccess(AssociationRuleResult result) {
                progressPresenter.showProgress(result.getProcessIdentifier(), ARSetModel.class,
                    new CompletionListener<ARSetModel>() {
                      @Override
                      @SuppressWarnings("synthetic-access")
                      public boolean onCompletion(boolean success, ARSetModel result) {
                        if (success) {
                          if (result != null) {
                            getEventBus().fireEvent(
                                new AssociationRuleReceivedEvent(cluster, result.getModelSet()));
                          } else {
                            // show
                            // empty
                            // list
                            getEventBus().fireEvent(
                                new AssociationRuleReceivedEvent(cluster,
                                    new ArrayList<AssociationRuleModel>()));
                          }
                          getDisplay().getAssociationRuleDisplay().stopProcessing();
                          return true;
                        }
                        getDisplay().getAssociationRuleDisplay().displayError();
                        return false;
                      }
                    }, true);

                return false;
              }
            }));

  }

  protected void requestPropertyStatistics(final Cluster cluster, final int start) {
    // start with waiting event w/o data
    this.requestActivation();
    getDispatcher().execute(
        new PredicateStatisticsRequest(cluster, start, start + this.entryCount - 1),
        callbackBuilder.build(getDisplay().getPredicatesDisplay(),
            new AsyncDisplayCallback.Handler<DataTableResult>() {

              @Override
              protected boolean handleSuccess(DataTableResult value) {
                final PredicateStatisticsReceivedEvent event =
                    new PredicateStatisticsReceivedEvent();
                event.setCluster(cluster);
                event.setDataTable(value.getDataTable());
                event.setOffset(start);
                getEventBus().fireEvent(event);

                return true;
              }
            }));
  }

  protected void requestLinkLiteralStatistics(final Cluster cluster) {
    // start with waiting event w/o data
    this.requestActivation();
    getDispatcher().execute(
        new LinkLiteralStatisticsRequest(cluster),
        callbackBuilder.build(getDisplay().getLinkLiteralDisplay(),
            new AsyncDisplayCallback.Handler<DataTableResult>() {
              @Override
              protected boolean handleSuccess(DataTableResult value) {
                final LinkLiteralStatisticsReceivedEvent event =
                    new LinkLiteralStatisticsReceivedEvent();
                event.setCluster(cluster);
                event.setDataTable(value.getDataTable());
                getEventBus().fireEvent(event);

                return true;
              }
            }));
  }

  protected void requestDetails(final Cluster cluster, final ArrayList<Predicate> predicates) {
    this.datatypePresenter.requestDatatypes(cluster, predicates);
  }

  private void requestSubjectDetails(final Cluster cluster, final Subject subject) {
    getDispatcher().execute(new SubjectDetailRequest(cluster, subject),
        callbackBuilder.build(getDisplay(), new AsyncDisplayCallback.Handler<DataTableResult>() {
          @Override
          protected boolean handleSuccess(DataTableResult result) {
            // take the result from the server and notify
            // client interested components
            SuggestionSubjectDetailsReceivedEvent event =
                new SuggestionSubjectDetailsReceivedEvent();
            event.setCluster(cluster);
            event.setSubject(subject);
            event.setDataTable(result.getDataTable());

            getEventBus().fireEvent(event);

            return true;
          }
        }));

  }
}
