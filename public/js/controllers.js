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

    $scope.init = function() {
      console.log($route);
      angular.extend($scope.nav, $routeParams); //TODO doesnt work!
    };

    $scope.goTo = function(params){
      angular.extend($scope.nav, params);
      var url = makeUrl({});
      $location.path(url);
    };

  };
  controllers.MainCtrl.$inject = ['$scope', '$routeParams', '$location'];


  controllers.MyCtrl1 = function ($scope, $routeParams) {
    $scope.nav.view = 'view1';
    $scope.nav.dataset = $routeParams.dataset;
    $scope.nav.group = $routeParams.group;

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

  controllers.MyCtrl2 = function ($scope) {
    $scope.nav.view = 'view2';

  };
  controllers.MyCtrl2.$inject = ['$scope'];

  controllers.TreeViewController = function ($scope, $location) {
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
  controllers.TreeViewController.$inject = ['$scope', '$location', '$routeParams'];

  return controllers;

});