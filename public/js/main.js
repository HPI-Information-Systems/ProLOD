/*global require, requirejs */

'use strict';

requirejs.config({
  paths: {
    'angular': ['../lib/angularjs/angular'],
    'angular-route': ['../lib/angularjs/angular-route'],
    'ui-bootstrap': ['../lib/angular-ui-bootstrap/ui-bootstrap-tpls'],
    'ui-grid': ['../lib/ui-grid/ui-grid'],
    'treeControl': ['../lib/angular-tree-control/angular-tree-control']
  },
  shim: {
    'angular': {
      exports: 'angular'
    },
    'angular-route': {
      deps: ['angular'],
      exports: 'angular'
    },
    'ui-bootstrap': {
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
    }
  }
});

require(['angular', './controllers', './directives', './filters', './services', 'angular-route', 'ui-bootstrap', 'ui-grid', 'treeControl'],
  function (angular, controllers) {
    // Declare app level module which depends on filters, and services

    var app = angular.module('myApp', ['myApp.filters', 'myApp.services', 'myApp.directives', 'ngRoute', 'ui.bootstrap', 'ui.grid', 'treeControl']).
      config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/:dataset/:group/default', {templateUrl: 'partials/partial1.html', controller: controllers.MyCtrl1});
        $routeProvider.when('/:dataset/:group/view1', {templateUrl: 'partials/partial1.html', controller: controllers.MyCtrl1});
        $routeProvider.when('/:dataset/:group/view2', {templateUrl: 'partials/partial2.html', controller: controllers.MyCtrl2});
        $routeProvider.when('/', {templateUrl: 'partials/partial1.html', controller: controllers.MyCtrl1});
        $routeProvider.otherwise({redirectTo: '/'});
      }]);

    app.controller('MainCtrl', controllers.MainCtrl);

    app.controller('TreeViewController', controllers.TreeViewController);

    angular.bootstrap(document, ['myApp']);

  });
