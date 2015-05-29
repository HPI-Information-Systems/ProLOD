/*global require, requirejs */

'use strict';

requirejs.config({
  paths: {
    'angular': ['../lib/angularjs/angular'],
    'angular-route': ['../lib/angularjs/angular-route'],
    'ui-grid': ['../lib/ui-grid/ui-grid'],
    'treeControl': ['../lib/angular-tree-control/angular-tree-control'],
    'chartjs': ['../lib/chartjs/Chart'],
    'angular-chart': ['../lib/angular-chart.js/angular-chart'],
    'bg-splitter': ['../bg-splitter/js/splitter'],
    'd3': ['../lib/d3js/d3'],
    'nv': ['../lib/nvd3-community/nv.d3'],
    'jquery': ['../lib/jquery/jquery']

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
    'angular-chart': {
      deps: ['angular', 'chartjs'],
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
    'nv': {
      deps: ['d3'],
      exports: 'nv'
    }
  }
});

require(['angular', './controllers/viewcontrollers','./controllers/tableviewcontrollers', './controllers/graphcontroller', './controllers/chartcontroller',
      './controllers/controllers', './directives', './filters', './services',
      'angular-route', 'angular-chart', 'ui-grid', 'bg-splitter', 'treeControl', 'd3', 'nv', 'jquery'],
  function (angular,  viewcontrollers, tableviewcontrollers, GraphCtrl, ChartCtrl) {
    // Declare app level module which depends on filters, and services

    var app = angular.module('Prolod2', ['Prolod2.controllers', 'Prolod2.filters', 'Prolod2.services', 'Prolod2.directives', 'ngRoute', 'ui.grid', 'ui.grid.autoResize', 'bgDirectives', 'treeControl', 'chart.js'])
      .config(['$routeProvider', function ($routeProvider) {
      // routes
      $routeProvider.when('/', {templateUrl: 'assets/partials/index.html', controller: viewcontrollers.IndexViewCtrl});

      $routeProvider.when('/:dataset/:group/view0', {templateUrl: 'assets/partials/partial0.html', controller: viewcontrollers.OverviewCtrl, activetab: 'view0'});

      $routeProvider.when('/:dataset/:group/view1', {templateUrl: 'assets/partials/table.html', controller: viewcontrollers.TableViewCtrl, activetab: 'view1'});
      $routeProvider.when('/:dataset/:group/view1/:detail', {templateUrl: 'assets/partials/tabledetail.html', controller: viewcontrollers.TableDetailViewCtrl, activetab: 'view1'});

      $routeProvider.when('/:dataset/:group/charts', {templateUrl: 'assets/partials/chart.html', controller: ChartCtrl, activetab: 'charts'});
      $routeProvider.when('/:dataset/:group/graphs', {templateUrl: 'assets/partials/graphs.html', controller: GraphCtrl, activetab: 'graphs'});

      $routeProvider.when('/:dataset/:group/predicates', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.PredicateViewCtrl, activetab: 'predicates'});
      $routeProvider.when('/:dataset/:group/inversePredicates', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.InversePredicateViewCtrl, activetab: 'inversePredicates'});
      $routeProvider.when('/:dataset/:group/associationRules', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.AssociationRuleViewCtrl, activetab: 'associationRules'});
      $routeProvider.when('/:dataset/:group/synonyms', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.SynonymViewCtrl, activetab: 'synonyms'});
      $routeProvider.when('/:dataset/:group/factGeneration', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.FactGenerationViewCtrl, activetab: 'factGeneration'});
      $routeProvider.when('/:dataset/:group/suggestions', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.SuggestionViewCtrl, activetab: 'suggestions'});
      $routeProvider.when('/:dataset/:group/uniqueness', {templateUrl: 'assets/partials/table.html', controller: tableviewcontrollers.UniquenessViewCtrl, activetab: 'uniqueness'});

      // redirects
      $routeProvider.when('/:dataset/:group/index', {redirectTo: '/:dataset/:group/view0'});
      $routeProvider.when('/:dataset/:group', {redirectTo: '/:dataset/:group/index'});
      $routeProvider.when('/:dataset', {redirectTo: '/:dataset/all'});

      // other
      $routeProvider.otherwise({redirectTo: '/'});
    }]);

    angular.bootstrap(document, ['Prolod2']);
    console.log("bootstrapped!");

  });
