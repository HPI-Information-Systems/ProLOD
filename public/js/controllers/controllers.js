/*global define */

'use strict';

define(function () {

  /* Controllers */

  var controllers = {};

  controllers.MainCtrl = function ($scope, $rootScope, $routeParams, $location) {
    $scope.nav = {
      view: "default"
    };

    $rootScope.$on('$routeChangeSuccess', function(event, current, previous){
          angular.extend($scope.nav, current.params);
    });

    var makeUrl = function (params) {
      params = params || {};
      var dataset = params.dataset || $scope.nav.dataset;
      var group = params.group || $scope.nav.group;
      var view = params.view || $scope.nav.view;
      return "/" + dataset + "/" + group + "/" + view;
    };

    $scope.init = function() {
      console.log("init!");
    };

    $scope.updateParams = function(view) {
      $scope.nav.view = view;
      console.log("view: " + JSON.stringify($scope.nav));
    };

    $scope.goTo = function(params){
      angular.extend($scope.nav, params);
      var url = makeUrl();
      $location.path(url);
    };

  };
  controllers.MainCtrl.$inject = ['$scope', '$rootScope', '$routeParams', '$location'];


  controllers.DefaultViewCtrl = function ($scope, $routeParams) {
    $scope.updateParams('default', $routeParams);
  };
  controllers.DefaultViewCtrl.$inject = ['$scope', '$routeParams'];


  controllers.TreeViewController = function ($scope) {
    $scope.treeOptions = {
      nodeChildren: "children",
      dirSelectable: true,
      injectClasses: {
        ul: "a1",
        li: "a2",
        liSelected: "a7",
        iExpanded: "a3",
        iCollapsed: "a4",
        iLeaf: "a5",
        label: "a6",
        labelSelected: "a8"
      }
    };
    $scope.treeData = [
      {
        "name": "Dbpedia", dataset: "dbpedia", "children": [
        {"name": "Tiere", dataset: "dbpedia", group: "tiere"},
        {"name": "Autos", dataset: "dbpedia", group: "autos"}
      ]
      },
      {
        "name": "Drugbank", dataset: "drugbank", "children": [
        {"name": "Drugs", dataset: "drugbank", group: "drugs"},
        {"name": "Diseases", dataset: "drugbank", group: "diseases"}
      ]
      }
    ];

    $scope.onSelection = function (selected) {
      $scope.goTo({dataset: selected.dataset, group: selected.group || "all"});
    }
  };
  controllers.TreeViewController.$inject = ['$scope'];

  controllers.BreadCrumbController = function ($scope) {
    $scope.model = {
        data: [
          
        ]
    }
  };
  controllers.BreadCrumbController.$inject = ['$scope'];

  return controllers;

});