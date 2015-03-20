/*global define */

'use strict';

define(function () {

  /* Controllers */

  var controllers = {};


  controllers.IndexViewCtrl = function ($scope, $routeParams) {
    $scope.updateView(['index'], $routeParams);
  };
  controllers.IndexViewCtrl.$inject = ['$scope', '$routeParams'];


  controllers.OverviewCtrl = function ($scope) {
    $scope.updateView(['view0']);

  };
  controllers.OverviewCtrl.$inject = ['$scope'];


  controllers.TableViewCtrl = function ($scope, httpApi) {
    $scope.updateView(['view1']);

    $scope.detailUrl = function (detail) {
      return $scope.makeUrl({view: ['view1', detail]})
    };

    $scope.model = {
      gridOptions: {
        data: 'model.data',
        columnDefs: [
          {
            name: '', width: 20, field: 'id', enableSorting: false,
            cellTemplate: '<div class="ui-grid-cell-contents"><a href="#{{grid.appScope.detailUrl(COL_FIELD)}}"><i class="glyphicon glyphicon-zoom-in"/></a></div>'
          },
          {name: 'firstName'},
          {name: 'lastName'},
          {name: 'age'}
        ]
      },
      data: []
    };

    httpApi.getTable1().then(function(evt){
      $scope.model.data = evt.data.data;
    });

  };
  controllers.TableViewCtrl.$inject = ['$scope', 'httpApi'];


  controllers.TableDetailViewCtrl = function ($scope, $routeParams, httpApi) {
    var id = $routeParams.detail;
    $scope.updateView(['view1', id]);

    $scope.model = {
      data: null
    };

    httpApi.getTable1Detail(id).then(function(evt){
      $scope.model.data = evt.data.data;
    });
  };
  controllers.TableDetailViewCtrl.$inject = ['$scope', '$routeParams', 'httpApi'];


  controllers.ChartsViewCtrl = function ($scope) {
    $scope.updateView(['view2']);

    Chart.defaults.global.colours = ['#1FBED6', '#97C30A', '#FF717E', '#555555'];

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


  controllers.Table2ViewCtrl = function ($scope) {
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
  controllers.Table2ViewCtrl.$inject = ['$scope'];

  return controllers;

});