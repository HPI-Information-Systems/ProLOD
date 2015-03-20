/*global require, requirejs */

'use strict';

requirejs.config({
  paths: {
    'angular': ['../lib/angularjs/angular'],
    'angular-route': ['../lib/angularjs/angular-route'],
    'ui-grid': ['../lib/ui-grid/ui-grid'],
    'treeControl': ['../lib/angular-tree-control/angular-tree-control'],
    'chartjs': ['../lib/chartjs/Chart'],
    'angular-chart': ['../lib/angular-chart.js/angular-chart']
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

require(['angular', './controllers/controllers','./controllers/viewcontrollers',
    './directives', './filters', './services', 'angular-route', 'angular-chart', 'ui-grid', 'treeControl'],
  function (angular, controllers, viewcontrollers) {
    // Declare app level module which depends on filters, and services

    var app = angular.module('Prolod2', ['Prolod2.filters', 'Prolod2.services', 'Prolod2.directives',
                                         'ngRoute', 'ui.grid', 'treeControl', 'chart.js']).
      config(['$routeProvider', function ($routeProvider) {
        // routes
        $routeProvider.when('/', {templateUrl: 'partials/index.html', controller: viewcontrollers.IndexViewCtrl});

        $routeProvider.when('/:dataset/:group/view0', {templateUrl: 'partials/partial0.html', controller: viewcontrollers.MyCtrl0});

        $routeProvider.when('/:dataset/:group/view1', {templateUrl: 'partials/table.html', controller: viewcontrollers.TableViewCtrl});
        $routeProvider.when('/:dataset/:group/view1/:detail', {templateUrl: 'partials/tabledetail.html', controller: viewcontrollers.TableDetailViewCtrl});

        $routeProvider.when('/:dataset/:group/view2', {templateUrl: 'partials/charts.html', controller: viewcontrollers.ChartsViewCtrl});

        $routeProvider.when('/:dataset/:group/view3', {templateUrl: 'partials/table.html', controller: viewcontrollers.Table2View});

        // redirects
        $routeProvider.when('/:dataset/:group/index', {redirectTo: '/:dataset/:group/view0'});
        $routeProvider.when('/:dataset/:group', {redirectTo: '/:dataset/:group/default'});
        $routeProvider.when('/:dataset', {redirectTo: '/:dataset/all/default'});

        // other
        $routeProvider.otherwise({redirectTo: '/'});
      }]);

    app.controller('MainCtrl', controllers.MainCtrl);
    app.controller('TreeViewController', controllers.TreeViewController);
    app.controller('BreadCrumbController', controllers.BreadCrumbController);

    angular.bootstrap(document, ['Prolod2']);

  });
