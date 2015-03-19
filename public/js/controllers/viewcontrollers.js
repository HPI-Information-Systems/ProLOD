/*global define */

'use strict';

define(function () {

  /* Controllers */

  var controllers = {};


  controllers.IndexViewCtrl = function ($scope, $routeParams) {
    $scope.updateView(['index'], $routeParams);
  };
  controllers.IndexViewCtrl.$inject = ['$scope', '$routeParams'];



  controllers.MyCtrl0 = function ($scope) {
    $scope.updateView(['view0']);

  };
  controllers.MyCtrl0.$inject = ['$scope'];


  controllers.MyCtrl1 = function ($scope) {
    $scope.updateView(['view1']);

    $scope.url = function(detail) {
      return $scope.makeUrl({view: ['view1', detail]})
    };

    var data = [
      {
        firstName: 'Cox',
        lastName: 'Carney',
        age: 12
      },
      {
        firstName: 'Peter',
        lastName: 'Lustig',
        age: 30
      }
    ];

    $scope.model = {
      gridOptions: {
        data: 'model.data',
        columnDefs: [
          { name: '', width: 50, field: 'firstName',
            cellTemplate: '<div class="ui-grid-cell-contents"><a href="#{{grid.appScope.url(COL_FIELD)}}">X</a></div>' },
          { name: 'firstName' },
          { name: 'lastName' },
          { name: 'age' }
        ]
      },
      data: data
    }
  };
  controllers.MyCtrl1.$inject = ['$scope'];

  controllers.MyCtrl1Detail = function ($scope, $routeParams) {
    $scope.updateView(['view1', $routeParams.detail]);

    $scope.model = {
      name: $routeParams.detail
    }
  };
  controllers.MyCtrl1Detail.$inject = ['$scope', '$routeParams'];


  controllers.MyCtrl2 = function ($scope) {
    $scope.updateView(['view2']);

  };
  controllers.MyCtrl2.$inject = ['$scope'];


  controllers.MyCtrl3 = function ($scope) {
    $scope.updateView(['view3']);

    $scope.model = {
      gridOptions: {
        data: 'model.data'
      },
      data: [
        {
          property: 'instanceOf',
          object: 'Human',
          p: 0.4
        },
        {
          property: 'dateOfBirth',
          object: '1.1.12',
          p: 0.2
        },
        {
          property: 'first name',
          object: 'peter',
          p: 0.1
        }
      ]
    }
  };
  controllers.MyCtrl3.$inject = ['$scope'];

  return controllers;

});