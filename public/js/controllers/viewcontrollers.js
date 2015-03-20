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


  controllers.TableViewCtrl = function ($scope) {
    $scope.updateView(['view1']);

    $scope.url = function (detail) {
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
          {
            name: '', width: 20, field: 'firstName', enableSorting: false,
            cellTemplate: '<div class="ui-grid-cell-contents"><a href="#{{grid.appScope.url(COL_FIELD)}}"><i class="glyphicon glyphicon-zoom-in"/></a></div>'
          },
          {name: 'firstName'},
          {name: 'lastName'},
          {name: 'age'}
        ]
      },
      data: data
    }
  };
  controllers.TableViewCtrl.$inject = ['$scope'];

  controllers.TableDetailViewCtrl = function ($scope, $routeParams) {
    $scope.updateView(['view1', $routeParams.detail]);

    $scope.model = {
      name: $routeParams.detail
    }
  };
  controllers.TableDetailViewCtrl.$inject = ['$scope', '$routeParams'];


  controllers.ChartsViewCtrl = function ($scope) {
    $scope.updateView(['view2']);

    $scope.chart0 = {
      labels: ["Download Sales", "In-Store Sales", "Mail-Order Sales"],
      data: [300, 500, 100]

    };
    $scope.chart1 = {
      labels: ['2006', '2007', '2008', '2009', '2010', '2011', '2012'],
      series: ['Series A', 'Series B'],
      data: [
        [65, 59, 80, 81, 56, 55, 40],
        [28, 48, 40, 19, 86, 27, 90]
      ]
    }
  };
  controllers.ChartsViewCtrl.$inject = ['$scope'];


  controllers.Table2View = function ($scope) {
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
  controllers.Table2View.$inject = ['$scope'];

  return controllers;

});