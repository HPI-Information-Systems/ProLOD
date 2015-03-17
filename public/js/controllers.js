/*global define */

'use strict';

define(function() {

/* Controllers */

var controllers = {};

controllers.MyCtrl1 = function($scope) {
  $scope.myData = [
    {
      "firstName": "Cox",
      "lastName": "Carney"
    },
    {
      "firstName": "Peter",
      "lastName": "Lustig"
    }
    ]
};
controllers.MyCtrl1.$inject = ['$scope'];

controllers.MyCtrl2 = function() {};
controllers.MyCtrl2.$inject = [];

controllers.TreeViewController = function($scope) {
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
  $scope.dataForTheTree =
    [
      { "name" : "Joe", "age" : "21", "children" : [
        { "name" : "Smith", "age" : "42", "children" : [] },
        { "name" : "Gary", "age" : "21", "children" : [
          { "name" : "Jenifer", "age" : "23", "children" : [
            { "name" : "Dani", "age" : "32", "children" : [] },
            { "name" : "Max", "age" : "34", "children" : [] }
          ]}
        ]}
      ]},
      { "name" : "Albert", "age" : "33", "children" : [] },
      { "name" : "Ron", "age" : "29", "children" : [] }
    ];

  $scope.showSelected = function(a) {
    alert(a.name);
  }
};
controllers.TreeViewController.$inject = ['$scope'];

return controllers;

});