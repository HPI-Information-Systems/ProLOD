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
    'd3': ['../lib/d3js/d3'],
    'nv': ['../lib/nvd3js/nv.d3'],
      'tipsy': ['../lib/tipsy/tipsy'],
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
    'ui-grid': {
      deps: ['angular'],
      exports: 'angular'
    },
    'treeControl': {
      deps: ['angular'],
      exports: 'angular'
    }
  }
});

require(['angular', './controllers/controllers','./controllers/viewcontrollers','./controllers/tableviewcontrollers','./controllers/graphcontroller', './controllers/chartcontroller',
    './directives', './filters', './services', 'angular-route', 'angular-chart', 'ui-grid', 'treeControl', 'd3', 'nv','tipsy', 'jquery'],
  function (angular, controllers, viewcontrollers, tableviewcontrollers, GraphCtrl, ChartCtrl) {
    // Declare app level module which depends on filters, and services

    var app = angular.module('Prolod2', ['Prolod2.filters', 'Prolod2.services', 'Prolod2.directives',
                             'ngRoute', 'ui.grid', 'treeControl', 'chart.js'])
      .config(['$routeProvider', function ($routeProvider) {
      // routes
      $routeProvider.when('/', {templateUrl: 'partials/index.html', controller: viewcontrollers.IndexViewCtrl});

      $routeProvider.when('/:dataset/:group/view0', {templateUrl: 'partials/partial0.html', controller: viewcontrollers.OverviewCtrl});

      $routeProvider.when('/:dataset/:group/view1', {templateUrl: 'partials/table.html', controller: viewcontrollers.TableViewCtrl});
      $routeProvider.when('/:dataset/:group/view1/:detail', {templateUrl: 'partials/tabledetail.html', controller: viewcontrollers.TableDetailViewCtrl});

      $routeProvider.when('/:dataset/:group/view2', {templateUrl: 'partials/chart.html', controller: ChartCtrl});
      $routeProvider.when('/:dataset/:group/view4', {templateUrl: 'partials/graph.html', controller: GraphCtrl});
      //$routeProvider.when('/:dataset/:group/view2', {templateUrl: 'partials/charts.html'});
      $routeProvider.when('/:dataset/:group/predicates', {templateUrl: 'partials/table.html', controller: tableviewcontrollers.PredicateViewCtrl});
      $routeProvider.when('/:dataset/:group/inversePredicates', {templateUrl: 'partials/table.html', controller: tableviewcontrollers.InversePredicateViewCtrl});
      $routeProvider.when('/:dataset/:group/associationRules', {templateUrl: 'partials/table.html', controller: tableviewcontrollers.AssociationRuleViewCtrl});
      $routeProvider.when('/:dataset/:group/synonyms', {templateUrl: 'partials/table.html', controller: tableviewcontrollers.SynonymViewCtrl});
      $routeProvider.when('/:dataset/:group/factGeneration', {templateUrl: 'partials/table.html', controller: tableviewcontrollers.FactGenerationViewCtrl});
      $routeProvider.when('/:dataset/:group/suggestions', {templateUrl: 'partials/table.html', controller: tableviewcontrollers.SuggestionViewCtrl});
      $routeProvider.when('/:dataset/:group/uniqueness', {templateUrl: 'partials/table.html', controller: tableviewcontrollers.UniquenessViewCtrl});



      // redirects
      $routeProvider.when('/:dataset/:group/index', {redirectTo: '/:dataset/:group/view0'});
      $routeProvider.when('/:dataset/:group', {redirectTo: '/:dataset/:group/index'});
      $routeProvider.when('/:dataset', {redirectTo: '/:dataset/all'});

      // other
      $routeProvider.otherwise({redirectTo: '/'});
    }]);

    app.controller('MainCtrl', controllers.MainCtrl);
    app.controller('TreeViewController', controllers.TreeViewController);
    app.controller('BreadCrumbController', controllers.BreadCrumbController);

    angular.bootstrap(document, ['Prolod2']);

  });
