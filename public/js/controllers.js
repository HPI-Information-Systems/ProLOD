/*global define */

'use strict';

define(function () {

  /* Controllers */

  var controllers = {};

  controllers.MainCtrl = function ($scope, $routeParams, $location) {
    $scope.nav = {
      view: "default"
    };

    var makeUrl = function (params) {
      var dataset = params.dataset || $scope.nav.dataset;
      var group = params.group || $scope.nav.group;
      var view = params.view || $scope.nav.view;
      return "/" + dataset + "/" + group + "/" + view;
    };

    $scope.init = function(view, params) {
      angular.extend($scope.nav, params);
      $scope.nav.view = view;
    };

    $scope.goTo = function(params){
      angular.extend($scope.nav, params);
      var url = makeUrl({});
      $location.path(url);
    };

  };
  controllers.MainCtrl.$inject = ['$scope', '$routeParams', '$location'];


  controllers.DefaultViewCtrl = function ($scope, $routeParams) {
    $scope.init('default', $routeParams);

  };
  controllers.DefaultViewCtrl.$inject = ['$scope', '$routeParams'];


  controllers.MyCtrl1 = function ($scope, $routeParams) {
    $scope.init('view1', $routeParams);

    $scope.myData = [
      {
        "firstName": "Cox",
        "lastName": "Carney",
        "age": 12
      },
      {
        "firstName": "Peter",
        "lastName": "Lustig",
        "age": 30
      }
    ]
  };
  controllers.MyCtrl1.$inject = ['$scope', '$routeParams'];

  controllers.MyCtrl2 = function ($scope, $routeParams) {
    $scope.init('view2', $routeParams);

  };
  controllers.MyCtrl2.$inject = ['$scope', '$routeParams'];

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
    $scope.dataForTheTree = [
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

    $scope.showSelected = function (selected) {
      $scope.goTo({dataset: selected.dataset, group: selected.group || "all"});
    }
  };
  controllers.TreeViewController.$inject = ['$scope'];

  return controllers;

});