/*global define */

'use strict';

define(function () {

  /* Controllers */

  var controllers = {};


  controllers.IndexViewCtrl = function ($scope, $routeParams) {
    $scope.updateView(['index']);
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
            type: 'object',
            cellTemplate: '<div class="ui-grid-cell-contents"><a href="#{{grid.appScope.detailUrl(COL_FIELD)}}"><i class="glyphicon glyphicon-zoom-in"/></a></div>'
          },
          {name: 'firstName', type: 'string'},
          {name: 'lastName', type: 'string'},
          {name: 'age', type: 'number'}
        ]
      },
      data: []
    };

    httpApi.getTable1().then(function (evt) {
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

    httpApi.getTable1Detail(id).then(function (evt) {
      $scope.model.data = evt.data.data;
    });
  };
  controllers.TableDetailViewCtrl.$inject = ['$scope', '$routeParams', 'httpApi'];

  return controllers;

});