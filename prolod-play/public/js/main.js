/*global require, requirejs */

'use strict';

requirejs.config({
  paths: {
    'angular': ['/lib/angularjs/angular'],
    'angular-route': ['/lib/angularjs/angular-route'],
    'ui-grid': ['/lib/ui-grid/ui-grid'],
    'treeControl': ['/lib/angular-tree-control/angular-tree-control'],
    'd3': ['/lib/d3js/d3'],
    'dimple': ['/lib/dimple/dimple']
    //'d3v3': ['../lib/nvd3js/d3.v3.min']
  },
  shim: {
    'angular': {
      exports: 'angular'
    },
    'angular-route': {
      deps: ['angular'],
      exports: 'angular'
    },
    'bg-splitter': {
      deps: ['angular'],
      exports: 'angular'
    },
    'ui-grid': {
      deps: ['angular'],
      exports: 'angular'
    },
    'treeControl': {
      deps: ['angular'],
      exports: 'angular'
    },
    'dimple': {
      deps: ['d3'],
      exports: 'dimple'
    }
  }
});

require(['angular', './controllers/viewcontrollers','./controllers/tableviewcontrollers', './controllers/chartcontroller', './controllers/controllers',
         './controllers/graphstatisticscontroller', './controllers/maincontroller', './controllers/panelcontroller', './controllers/breadcrumbcontroller', './controllers/treeviewcontroller',
         './controllers/graphpatterncontroller', './controllers/graphdetailcontroller','./controllers/giantcomponentcontroller',
         './directives/directives', './directives/graphThumbnail',
         './filters/filters', './services/services', './services/httpApi', './services/routeBuilder', './services/colorHash',
         'angular-route', 'ui-grid', '../bg-splitter/js/splitter', 'treeControl', 'd3', 'dimple'],
  function (angular,  viewcontrollers, tableviewcontrollers, ChartCtrl) {
    // Declare app level module which depends on filters, and services

    var app = angular.module('Prolod2', ['Prolod2.controllers', 'Prolod2.filters', 'Prolod2.services', 'Prolod2.directives', 'ngRoute', 'ui.grid', 'ui.grid.autoResize', 'bgDirectives', 'treeControl'])
      .config(['$routeProvider', function ($routeProvider) {
      // routes
      $routeProvider.when('/', {templateUrl: 'assets/partials/index.html', controller: viewcontrollers.IndexViewCtrl, activetab: 'index'});

      $routeProvider.when('/view0/:dataset', {templateUrl: 'assets/partials/partial0.html', controller: viewcontrollers.OverviewCtrl, activetab: 'view0'});

      $routeProvider.when('/graphstatistics/:dataset', {templateUrl: 'assets/partials/graph_statistics.html', controller: 'GraphCtrl', activetab: 'graphs'});
      $routeProvider.when('/graphstatistics/:dataset/pattern/:pattern', {templateUrl: 'assets/partials/graph_pattern.html', controller: 'GraphPatternCtrl', activetab: 'graphs'});
      $routeProvider.when('/graphstatistics/:dataset/giantComponent', {templateUrl: 'assets/partials/giant_component.html', controller: 'GiantComponentCtrl', activetab: 'graphs'});
      $routeProvider.when('/graphstatistics/:dataset/pattern/:pattern/:detail', {templateUrl: 'assets/partials/graph_detail.html', controller: 'GraphDetailCtrl', activetab: 'graphs'});

      $routeProvider.when('/charts/:dataset', {templateUrl: 'assets/partials/chart.html', controller: ChartCtrl, activetab: 'charts'});

      $routeProvider.when('/view1/:dataset', {templateUrl: 'assets/partials/table.html', controller: viewcontrollers.TableViewCtrl, activetab: 'view1'});
      $routeProvider.when('/view1/:dataset/:detail', {templateUrl: 'assets/partials/tabledetail.html', controller: viewcontrollers.TableDetailViewCtrl, activetab: 'view1'});

      $routeProvider.when('/predicates/:dataset', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.PredicateViewCtrl, activetab: 'predicates'});
      $routeProvider.when('/inversePredicates/:dataset', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.InversePredicateViewCtrl, activetab: 'inversePredicates'});
      $routeProvider.when('/associationRules/:dataset', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.AssociationRuleViewCtrl, activetab: 'associationRules'});
      /*$routeProvider.when('/synonyms/:dataset', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.SynonymViewCtrl, activetab: 'synonyms'});
      $routeProvider.when('/factGeneration/:dataset', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.FactGenerationViewCtrl, activetab: 'factGeneration'});
      $routeProvider.when('/suggestions/:dataset', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.SuggestionViewCtrl, activetab: 'suggestions'});
      $routeProvider.when('/uniqueness/:dataset', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.UniquenessViewCtrl, activetab: 'uniqueness'});*/

      // other
      $routeProvider.otherwise({redirectTo: '/'});
    }]);

    angular.bootstrap(document, ['Prolod2']);
    console.log('bootstrapped!');

  });
