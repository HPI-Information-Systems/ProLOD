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

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import de.hpi.fgis.ldp.client.mvp.dialog.AbstractDialogView;
import de.hpi.fgis.ldp.client.mvp.main.content.profiling.GeneralPresenter.DataRequestSource;
import de.hpi.fgis.ldp.client.service.AbstractCallbackDisplay;
import de.hpi.fgis.ldp.client.service.CallbackDisplay;
import de.hpi.fgis.ldp.client.util.dataStructures.RuleMatrixManager;
import de.hpi.fgis.ldp.client.util.dataStructures.SuggestionList;
import de.hpi.fgis.ldp.client.view.datatable.AbstractDataTableChartPanel;
import de.hpi.fgis.ldp.client.view.datatable.DataTablePanel;
import de.hpi.fgis.ldp.shared.data.ObjectValue;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.data.SuggestionSetModel;
import de.hpi.fgis.ldp.shared.data.table.IDataColumn;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

public class GeneralView extends ContentPanel implements GeneralPresenter.Display {

  protected final TabPanel contentPanels;
  private final ContentPanel linkLiteralChartPanel;
  private final ContentPanel topPredicateChartPanel;
  private final ContentPanel predicatePanel;
  private final ContentPanel antonymPanel;
  private final ContentPanel associationRulePanel;
  private final ContentPanel ontologyAlignmentPanel;
  private final ContentPanel synonymPanel;
  private final ContentPanel factGenerationPanel;
  private final ContentPanel suggestionPanel;
  private final ContentPanel uniquenessPanel;

  private AbstractDataTableChartPanel linkLiteralChartTablePanel;
  private AbstractDataTableChartPanel topPredicateChartTablePanel;
  private DataTablePanel predicateTablePanel;
  private DataTablePanel antonymTablePanel;
  private DataTablePanel associationRuleTablePanel;
  private VerticalPanel ontologyAlignmentTablePanel;
  private DataTablePanel synonymTablePanel;
  private DataTablePanel factGenerationTablePanel;
  private DataTablePanel suggestionTablePanel;
  private DataTablePanel uniquenessTablePanel;

  private final AbstractCallbackDisplay antonymCallback;
  private final AbstractCallbackDisplay associationRuleCallback;
  private final AbstractCallbackDisplay linkLiteralCallback;
  private final AbstractCallbackDisplay predicateCallback;
  private final AbstractCallbackDisplay ontologyAlignmentCallBack;
  private final AbstractCallbackDisplay synonymCallBack;
  private final AbstractCallbackDisplay factGenerationCallBack;
  private final AbstractCallbackDisplay suggestionCallBack;
  private final AbstractCallbackDisplay uniquenessCallBack;

  private final SubjectSampleView subjectSamples;
  private final ObjectSampleView objectSamples;

  private TabItem defaultTab = null;
  protected boolean antonymsRequested = false;
  protected boolean associationRulesRequested = false;
  private boolean ontologyAlignmentRequested = false;
  protected boolean synonymsRequested = false;
  protected boolean factGenerationRequested = false;
  protected boolean suggestionRequested = false;
  protected boolean uniquenessRequested = false;

  protected DataRequestSource requestSource;

  // private SuggestionDialogView suggestionDialogView;

  @Override
  public void setDataRequestSource(DataRequestSource source) {
    this.requestSource = source;
  }

  public GeneralView() {
    this.predicatePanel = new ContentPanel();
    this.antonymPanel = new ContentPanel();
    this.associationRulePanel = new ContentPanel();
    this.ontologyAlignmentPanel = new ContentPanel();
    this.linkLiteralChartPanel = new ContentPanel();
    this.topPredicateChartPanel = new ContentPanel();
    this.synonymPanel = new ContentPanel();
    this.factGenerationPanel = new ContentPanel();
    this.suggestionPanel = new ContentPanel();
    this.uniquenessPanel = new ContentPanel();

    this.subjectSamples = new SubjectSampleView();
    this.objectSamples = new ObjectSampleView();

    // this.suggestionDialogView = new SuggestionDialogView();

    antonymCallback = new AbstractCallbackDisplay(antonymPanel) {
      @Override
      public void resetToDefault() {
        this.setContent(antonymTablePanel);
      }
    };

    associationRuleCallback = new AbstractCallbackDisplay(associationRulePanel) {
      @Override
      public void resetToDefault() {
        this.setContent(associationRuleTablePanel);
      }
    };

    predicateCallback = new AbstractCallbackDisplay(predicatePanel, topPredicateChartPanel) {
      @Override
      public void resetToDefault() {
        this.setContent(predicateTablePanel, topPredicateChartTablePanel);
      }
    };
    linkLiteralCallback = new AbstractCallbackDisplay(linkLiteralChartPanel) {
      @Override
      public void resetToDefault() {
        this.setContent(linkLiteralChartTablePanel);
      }
    };

    ontologyAlignmentCallBack = new AbstractCallbackDisplay(ontologyAlignmentPanel) {

      @Override
      public void resetToDefault() {
        this.setContent(ontologyAlignmentTablePanel);
      }
    };

    synonymCallBack = new AbstractCallbackDisplay(synonymPanel) {

      @Override
      public void resetToDefault() {
        this.setContent(synonymTablePanel);
      }
    };
    factGenerationCallBack = new AbstractCallbackDisplay(factGenerationPanel) {

      @Override
      public void resetToDefault() {
        this.setContent(factGenerationTablePanel);
      }
    };
    suggestionCallBack = new AbstractCallbackDisplay(suggestionPanel) {

      @Override
      public void resetToDefault() {
        this.setContent(suggestionTablePanel);
      }
    };
    uniquenessCallBack = new AbstractCallbackDisplay(uniquenessPanel) {

      @Override
      public void resetToDefault() {
        this.setContent(uniquenessTablePanel);
      }
    };

    this.contentPanels = new TabPanel();
    this.init();
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void startProcessing() {
    antonymCallback.startProcessing();
    associationRuleCallback.startProcessing();
    predicateCallback.startProcessing();
    linkLiteralCallback.startProcessing();
    ontologyAlignmentCallBack.startProcessing();
    synonymCallBack.startProcessing();
    factGenerationCallBack.startProcessing();
    suggestionCallBack.startProcessing();
    uniquenessCallBack.startProcessing();
  }

  @Override
  public void stopProcessing() {
    antonymCallback.stopProcessing();
    associationRuleCallback.stopProcessing();
    predicateCallback.stopProcessing();
    linkLiteralCallback.stopProcessing();
    ontologyAlignmentCallBack.stopProcessing();
    synonymCallBack.stopProcessing();
    factGenerationCallBack.stopProcessing();
    suggestionCallBack.stopProcessing();
    uniquenessCallBack.stopProcessing();
  }

  @Override
  public void displayError() {
    antonymCallback.displayError();
    associationRuleCallback.displayError();
    predicateCallback.displayError();
    linkLiteralCallback.displayError();
    ontologyAlignmentCallBack.displayError();
    synonymCallBack.displayError();
    factGenerationCallBack.displayError();
    suggestionCallBack.displayError();
    uniquenessCallBack.displayError();
  }

  // public void clear() {
  // this.predicatePanel.removeAll();
  // this.linkLiteralChartPanel.removeAll();
  // this.topPredicateChartPanel.removeAll();
  // this.antonymPanel.removeAll();
  // this.associationRulePanel.removeAll();
  // // this.removeAll();
  // }

  private void init() {
    this.predicatePanel.setHeaderVisible(false);
    this.predicatePanel.setBorders(false);
    this.predicatePanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

    this.antonymPanel.setHeaderVisible(false);
    this.antonymPanel.setBorders(false);
    this.antonymPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

    this.associationRulePanel.setHeaderVisible(false);
    this.associationRulePanel.setBorders(false);
    this.associationRulePanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

    this.topPredicateChartPanel.setHeaderVisible(false);
    this.topPredicateChartPanel.setBorders(false);
    this.topPredicateChartPanel.setLayout(new RowLayout());

    this.linkLiteralChartPanel.setHeaderVisible(false);
    this.linkLiteralChartPanel.setBorders(false);
    this.linkLiteralChartPanel.setLayout(new RowLayout());

    this.ontologyAlignmentPanel.setHeaderVisible(false);
    this.ontologyAlignmentPanel.setBorders(false);
    this.ontologyAlignmentPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

    this.synonymPanel.setHeaderVisible(false);
    this.synonymPanel.setBorders(false);
    this.synonymPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

    this.factGenerationPanel.setHeaderVisible(false);
    this.factGenerationPanel.setBorders(false);
    this.factGenerationPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

    this.suggestionPanel.setHeaderVisible(false);
    this.suggestionPanel.setBorders(false);
    this.suggestionPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

    this.uniquenessPanel.setHeaderVisible(false);
    this.uniquenessPanel.setBorders(false);
    this.uniquenessPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));

    ContentPanel chartPanel = new ContentPanel(new BorderLayout());
    chartPanel.setHeaderVisible(false);
    chartPanel.setBorders(false);
    chartPanel.setSize(1, 1);
    chartPanel.setBodyBorder(false);

    BorderLayoutData leftData = new BorderLayoutData(LayoutRegion.CENTER);
    leftData.setMargins(new Margins(0));
    leftData.setSize(0.5f);
    leftData.setFloatable(true);
    leftData.setHideCollapseTool(true);
    leftData.setSplit(true);
    leftData.setMinSize(0);

    BorderLayoutData rightData = new BorderLayoutData(LayoutRegion.EAST);
    rightData.setMargins(new Margins(0));
    rightData.setSize(0.5f);
    rightData.setFloatable(true);
    rightData.setHideCollapseTool(true);
    rightData.setSplit(true);
    rightData.setMinSize(0);

    chartPanel.add(this.topPredicateChartPanel, leftData);
    chartPanel.add(this.linkLiteralChartPanel, rightData);

    this.contentPanels.setResizeTabs(true);
    this.contentPanels.setAnimScroll(true);
    this.contentPanels.setTabScroll(true);
    this.contentPanels.setCloseContextMenu(true);
    this.contentPanels.setTabPosition(TabPosition.BOTTOM);

    TabItem predicateItem = new TabItem();

    predicateItem.setText("Predicates");
    predicateItem.setClosable(false);
    predicateItem.setLayout(new RowLayout());
    predicateItem.add(this.predicatePanel, new RowData(1D, 1D));
    predicateItem.addStyleName("pad-text");
    predicateItem.setAutoWidth(true);
    predicateItem.setAutoHeight(true);
    this.contentPanels.add(predicateItem);

    this.defaultTab = predicateItem;

    TabItem antonymItem = new TabItem();

    antonymItem.setText("Inverse Predicates");
    antonymItem.setClosable(false);
    antonymItem.setLayout(new RowLayout());
    antonymItem.add(this.antonymPanel, new RowData(1D, 1D));
    antonymItem.addStyleName("pad-text");
    antonymItem.setAutoWidth(true);
    antonymItem.setAutoHeight(true);
    antonymItem.addListener(Events.Select, new Listener<ComponentEvent>() {
      @Override
      public void handleEvent(ComponentEvent e) {
        if (!antonymsRequested) {
          antonymsRequested = true;
          requestSource.loadAntonymSubjects();
        }
      }
    });

    this.contentPanels.add(antonymItem);

    TabItem associationRuleItem = new TabItem();

    associationRuleItem.setText("Association Rules");
    associationRuleItem.setClosable(false);
    associationRuleItem.setLayout(new RowLayout());
    associationRuleItem.add(this.associationRulePanel, new RowData(1D, 1D));
    associationRuleItem.addStyleName("pad-text");
    associationRuleItem.setAutoWidth(true);
    associationRuleItem.setAutoHeight(true);

    associationRuleItem.addListener(Events.Select, new Listener<ComponentEvent>() {
      @Override
      public void handleEvent(ComponentEvent e) {
        if (!associationRulesRequested) {
          associationRulesRequested = true;
          requestSource.loadAssociationRules();
        }
      }
    });

    this.contentPanels.add(associationRuleItem);

    TabItem ontologyItem = new TabItem();

    ontologyItem.setText("Ontology Alignment");
    ontologyItem.setClosable(false);
    ontologyItem.setLayout(new RowLayout());
    ontologyItem.add(this.ontologyAlignmentPanel, new RowData(1D, 1D));
    ontologyItem.addStyleName("pad-text");
    ontologyItem.setAutoWidth(true);
    ontologyItem.setAutoHeight(true);

    ontologyItem.addListener(Events.Select, new Listener<ComponentEvent>() {

      @Override
      public void handleEvent(ComponentEvent e) {
        if (!ontologyAlignmentRequested) {
          ontologyAlignmentRequested = true;
          requestSource.loadOntologyImprovements();
        }
      }
    });
    this.contentPanels.add(ontologyItem);

    TabItem synonymItem = new TabItem();

    synonymItem.setText("Synonym Discovery");
    synonymItem.setClosable(false);
    synonymItem.setLayout(new RowLayout());
    synonymItem.add(this.synonymPanel, new RowData(1D, 1D));
    synonymItem.addStyleName("pad-text");
    synonymItem.setAutoWidth(true);
    synonymItem.setAutoHeight(true);

    synonymItem.addListener(Events.Select, new Listener<ComponentEvent>() {
      @Override
      public void handleEvent(ComponentEvent e) {
        if (!synonymsRequested) {
          synonymsRequested = true;
          requestSource.loadSynonyms();
        }
      }
    });
    this.contentPanels.add(synonymItem);

    TabItem factGenerationItem = new TabItem();

    factGenerationItem.setText("Fact Generation");
    factGenerationItem.setClosable(false);
    factGenerationItem.setLayout(new RowLayout());
    factGenerationItem.add(this.factGenerationPanel, new RowData(1D, 1D));
    factGenerationItem.addStyleName("pad-text");
    factGenerationItem.setAutoWidth(true);
    factGenerationItem.setAutoHeight(true);

    factGenerationItem.addListener(Events.Select, new Listener<ComponentEvent>() {
      @Override
      public void handleEvent(ComponentEvent e) {
        if (!factGenerationRequested) {
          factGenerationRequested = true;
          requestSource.loadNewFacts();
        }
      }
    });
    this.contentPanels.add(factGenerationItem);

    TabItem suggestionItem = new TabItem();

    suggestionItem.setText("Suggestion");
    suggestionItem.setClosable(false);
    suggestionItem.setLayout(new RowLayout());
    suggestionItem.add(this.suggestionPanel, new RowData(1D, 1D));
    suggestionItem.addStyleName("pad-text");
    suggestionItem.setAutoWidth(true);
    suggestionItem.setAutoHeight(true);

    suggestionItem.addListener(Events.Select, new Listener<ComponentEvent>() {
      @Override
      public void handleEvent(ComponentEvent e) {
        if (!suggestionRequested) {
          suggestionRequested = true;
          requestSource.loadSuggestionInterface();
        }
      }
    });
    this.contentPanels.add(suggestionItem);

    TabItem uniquenessItem = new TabItem();

    uniquenessItem.setText("Uniqueness");
    uniquenessItem.setClosable(false);
    uniquenessItem.setLayout(new RowLayout());
    uniquenessItem.add(this.uniquenessPanel, new RowData(1D, 1D));
    uniquenessItem.addStyleName("pad-text");
    uniquenessItem.setAutoWidth(true);
    uniquenessItem.setAutoHeight(true);

    uniquenessItem.addListener(Events.Select, new Listener<ComponentEvent>() {
      @Override
      public void handleEvent(ComponentEvent e) {
        if (!uniquenessRequested) {
          uniquenessRequested = true;
          requestSource.loadUniqueness();
        }
      }
    });
    this.contentPanels.add(uniquenessItem);

    this.contentPanels.addListener(Events.Select, new Listener<ComponentEvent>() {
      @Override
      public void handleEvent(ComponentEvent be) {
        contentPanels.getSelectedItem().layout(true);
      }
    });

    this.setLayout(new BorderLayout());
    this.setSize(1, 1);
    this.setBorders(false);
    this.setBodyBorder(false);
    this.setHeaderVisible(false);

    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.CENTER);
    northData.setMargins(new Margins(0));
    northData.setSize(0.7f);
    northData.setFloatable(true);
    northData.setHideCollapseTool(true);

    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH);
    southData.setMargins(new Margins(0));
    southData.setSize(0.3f);
    southData.setFloatable(true);
    southData.setHideCollapseTool(true);
    southData.setSplit(true);

    this.add(this.contentPanels, northData);
    this.add(chartPanel, southData);

    // this.setLayout(new RowLayout(Orientation.VERTICAL));
    // this.setHeaderVisible(false);
    // this.setBorders(false);
    this.layout(true);
  }

  @Override
  public void setPredicatesTable(DataTablePanel table) {
    synchronized (this.predicatePanel) {
      this.predicateTablePanel = table;
    }
  }

  @Override
  public void setLinkLiteralChart(AbstractDataTableChartPanel chart) {
    synchronized (this.linkLiteralChartPanel) {
      this.linkLiteralChartTablePanel = chart;
    }
  }

  @Override
  public void setTopPredicatesChart(AbstractDataTableChartPanel chart) {
    synchronized (this.topPredicateChartPanel) {
      this.topPredicateChartTablePanel = chart;
    }
  }

  @Override
  public void setAntonymTable(DataTablePanel table) {
    synchronized (this.antonymPanel) {
      this.antonymTablePanel = table;
    }
  }

  @Override
  public void setAssociationRuleTable(DataTablePanel table) {
    synchronized (this.associationRulePanel) {
      this.associationRuleTablePanel = table;
    }
  }

  @Override
  public void setOntologyAlignmentPanel(VerticalPanel panel) {
    synchronized (this.ontologyAlignmentPanel) {
      this.ontologyAlignmentTablePanel = panel;
    }
  }

  @Override
  public void setSynonymTable(DataTablePanel table) {
    synchronized (this.synonymPanel) {
      this.synonymTablePanel = table;
    }

  }

  @Override
  public void setFactGenerationTable(DataTablePanel table) {
    synchronized (this.factGenerationPanel) {
      this.factGenerationTablePanel = table;
    }
  }

  @Override
  public void setSuggestionTable(DataTablePanel table) {
    synchronized (this.suggestionPanel) {
      this.suggestionTablePanel = table;
    }

  }

  @Override
  public void setUniquenessTable(DataTablePanel table) {
    synchronized (this.uniquenessPanel) {
      this.uniquenessTablePanel = table;
    }

  }

  public class SubjectSampleView extends AbstractDialogView {

    public SubjectSampleView() {
      super(800, 400);
      this.window.setTitle("Sample subjects");
    }

    public void setDataTable(final DataTablePanel table) {
      synchronized (this) {
        this.show();
        this.setWidget(table);
        this.callback.resetToDefault();
      }
    }

    @Override
    public void startProcessing() {
      throw new IllegalStateException("Unable to handle remote processing!");
    }

    @Override
    public void stopProcessing() {
      throw new IllegalStateException("Unable to handle remote processing!");
    }
  }

  public class SuggestionDialogView extends AbstractDialogView {

    // private VerticalPanel vPanel = new VerticalPanel();
    private RuleMatrixManager rulematrixManager;

    public SuggestionDialogView() {
      super(800, 400);
      this.window.setTitle("Add a new fact");
    }

    public void createRuleMatrix(SuggestionSetModel model) {
      rulematrixManager = new RuleMatrixManager(model);
    }

    public void setDataTable(Subject subject, final DataTablePanel panel) {
      synchronized (this) {
        // panel.setHeight(100);
        // panel.setWidth(100);

        IDataTable entityData = panel.getData();
        @SuppressWarnings("unchecked")
        IDataColumn<Predicate> predicateColumn = (IDataColumn<Predicate>) entityData.getColumn(0);// contains
                                                                                                  // the
                                                                                                  // predicates
        @SuppressWarnings("unchecked")
        IDataColumn<ObjectValue> objectColumn = (IDataColumn<ObjectValue>) entityData.getColumn(1); // contains
                                                                                                    // the
                                                                                                    // objects

        List<String> predicateStrings = new ArrayList<String>();
        for (int i = 0; i < predicateColumn.getElementCount(); i++) {
          predicateStrings.add(predicateColumn.getElement(i).getLabel());
        }

        List<String> objectStrings = new ArrayList<String>();
        for (int i = 0; i < objectColumn.getElementCount(); i++) {
          objectStrings.add(objectColumn.getElement(i).getLabel());
        }

        SuggestionList predicateSuggestionList =
            rulematrixManager.generatePredicateSugestions(predicateStrings);
        SuggestionList objectSuggestionList =
            rulematrixManager.generateObjectSugestions(objectStrings);

        List<String> topPredicateSuggestions = predicateSuggestionList.getTopSuggestions(10);
        List<String> topObjectSuggestions = objectSuggestionList.getTopSuggestions(10);

        ListBox predicateBox = new ListBox();
        for (String suggestion : topPredicateSuggestions) {
          predicateBox.addItem(suggestion);
        }
        // Make enough room for all five items (setting this value to 1
        // turns it
        // into a drop-down list).
        predicateBox.setVisibleItemCount(3);

        // Add it to the root panel.
        ListBox objectBox = new ListBox();
        for (String suggestion : topObjectSuggestions) {
          objectBox.addItem(suggestion);
        }

        // Make enough room for all five items (setting this value to 1
        // turns it
        // into a drop-down list).
        objectBox.setVisibleItemCount(3);

        Label predicateLabel = new Label("Enter new predicate:");
        Label objectLabel = new Label("Enter new object:");

        VerticalPanel vPanel = new VerticalPanel();
        HorizontalPanel hPanel = new HorizontalPanel();

        VerticalPanel predicatePanel = new VerticalPanel();
        predicatePanel.setAutoHeight(true);
        predicatePanel.setAutoWidth(true);
        predicatePanel.setLayout(new FitLayout());
        predicatePanel.add(predicateLabel);

        VerticalPanel objectPanel = new VerticalPanel();
        objectPanel.setAutoHeight(true);
        objectPanel.setAutoWidth(true);
        objectPanel.setLayout(new FitLayout());

        if (predicateBox.getItemCount() == 0) {
          predicatePanel.add(new Label("No Predicate Suggestions"));
        } else {
          predicatePanel.add(predicateBox);
        }
        objectPanel.add(objectLabel);
        if (objectBox.getItemCount() == 0) {
          objectPanel.add(new Label("No Object Suggestions"));

        } else {
          objectPanel.add(objectBox);
        }
        objectPanel.setLayout(new FitLayout());

        // vPanel.setTitle(subject.getLabel());
        this.setWidget(null);
        this.hide();
        this.callback.resetToDefault();
        // VerticalPanel vPanel = new VerticalPanel();
        // vPanel.removeAll();
        vPanel.add(panel);
        hPanel.add(predicatePanel);
        hPanel.add(objectPanel);
        vPanel.add(hPanel);

        vPanel.setAutoHeight(true);
        vPanel.setAutoWidth(true);
        vPanel.setLayout(new FitLayout());

        this.setWidget(vPanel);
        this.callback.resetToDefault();
        this.show();
      }
    }

    @Override
    public void startProcessing() {
      throw new IllegalStateException("Unable to handle remote processing!");
    }

    @Override
    public void stopProcessing() {
      throw new IllegalStateException("Unable to handle remote processing!");
    }
  }

  public class ObjectSampleView extends SubjectSampleView {

    public ObjectSampleView() {
      super();
      super.window.setTitle("Sample objects");
    }
  }

  @Override
  public void showAntonymSubjectSamples(DataTablePanel table) {
    synchronized (this.subjectSamples) {
      this.subjectSamples.setDataTable(table);
      if (!this.subjectSamples.asWidget().isVisible()) {
        this.subjectSamples.show();
      }
    }
  }

  @Override
  public void showUniquenessSubjectSamples(DataTablePanel table) {
    synchronized (this.subjectSamples) {
      this.subjectSamples.setDataTable(table);
      if (!this.subjectSamples.asWidget().isVisible()) {
        this.subjectSamples.show();
      }
    }
  }

  @Override
  public void showSynonymRanges(DataTablePanel table) {
    synchronized (this.objectSamples) {
      this.objectSamples.setDataTable(table);
      if (!this.objectSamples.asWidget().isVisible()) {
        this.objectSamples.show();
      }
    }
  }

  @Override
  public void showAssociationRuleSubjectSamples(DataTablePanel table) {
    synchronized (this.subjectSamples) {
      this.subjectSamples.setDataTable(table);
      if (!this.subjectSamples.asWidget().isVisible()) {
        this.subjectSamples.show();
      }
    }
  }

  @Override
  public void resetView() {
    // disable reloading of data
    this.antonymsRequested = true;
    this.associationRulesRequested = true;

    // set waiting progress for all panels
    this.setPredicatesTable(null);
    this.setTopPredicatesChart(null);
    this.setLinkLiteralChart(null);
    this.setAntonymTable(null);
    this.setOntologyAlignmentPanel(null);
    this.setSuggestionTable(null);
    this.setUniquenessTable(null);

    // select default tab
    contentPanels.setSelection(defaultTab);

    // mark antonyms&association rules to be done for next selection
    this.antonymsRequested = false;
    this.associationRulesRequested = false;
    this.ontologyAlignmentRequested = false;
    this.synonymsRequested = false;
    this.factGenerationRequested = false;
    this.suggestionRequested = false;
    this.uniquenessRequested = false;
  }

  @Override
  public CallbackDisplay getAntonymDisplay() {
    return this.antonymCallback;
  }

  @Override
  public CallbackDisplay getAssociationRuleDisplay() {
    return this.associationRuleCallback;
  }

  @Override
  public CallbackDisplay getLinkLiteralDisplay() {
    return this.linkLiteralCallback;
  }

  @Override
  public CallbackDisplay getPredicatesDisplay() {
    return this.predicateCallback;
  }

  @Override
  public CallbackDisplay getOntologyAlignmentDisplay() {
    return this.ontologyAlignmentCallBack;
  }

  @Override
  public CallbackDisplay getSynonymsDisplay() {
    return synonymCallBack;
  }

  @Override
  public CallbackDisplay getFactGenerationDisplay() {
    return factGenerationCallBack;
  }

  @Override
  public CallbackDisplay getSuggestionDisplay() {
    return suggestionCallBack;
  }

  // @Override
  // public void showSuggestionBox(DockLayoutPanel box) {
  // synchronized (this.suggestionDialogView) {
  // this.suggestionDialogView.setSuggestionBox(box);
  // if (!this.suggestionDialogView.asWidget().isVisible())
  // this.suggestionDialogView.show();
  // }
  // }

  @Override
  public void showSubjectSchema(Subject subject, DataTablePanel table) {
    // synchronized (this.suggestionDialogView) {
    SuggestionDialogView suggestionDialogView = new SuggestionDialogView();
    suggestionDialogView.createRuleMatrix(suggestions);
    suggestionDialogView.setDataTable(subject, table);
    if (!suggestionDialogView.asWidget().isVisible()) {
      suggestionDialogView.show();
    }

    // }
  }

  @Override
  public CallbackDisplay getUniquenessDisplay() {
    return this.uniquenessCallBack;
  }

  private SuggestionSetModel suggestions;

  @Override
  public void setSuggestionRuleMatrix(SuggestionSetModel model) {
    // this.suggestionDialogView = new SuggestionDialogView();
    // suggestionDialogView.createRuleMatrix(model);
    // synchronized (this.suggestionDialogView) {
    // this.suggestionDialogView.createRuleMatrix(model);
    // }
    this.suggestions = model;
  }

}
