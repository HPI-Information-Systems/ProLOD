/*global define */

'use strict';

define(function () {

  /* Controllers */

  var controllers = {};


  controllers.IndexViewCtrl = function ($scope, $routeParams) {
    $scope.updateBreadcrumb([]);
  };
  controllers.IndexViewCtrl.$inject = ['$scope', '$routeParams'];


  controllers.OverviewCtrl = function ($scope, $routeParams, routeBuilder) {
    $scope.updateBreadcrumb([{name:'view0', url: routeBuilder.getOverviewUrl()}]);
  };
  controllers.OverviewCtrl.$inject = ['$scope', '$routeParams', 'routeBuilder'];


  controllers.TableViewCtrl = function ($scope, httpApi, routeBuilder) {
    $scope.updateBreadcrumb([{name:'view1', url: routeBuilder.getTableUrl()}]);

    $scope.detailUrl = function (detail) {
      return routeBuilder.getTableDetailUrl(detail);
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
  controllers.TableViewCtrl.$inject = ['$scope', 'httpApi', 'routeBuilder'];


  controllers.TableDetailViewCtrl = function ($scope, $routeParams, httpApi, routeBuilder) {
    var id = $routeParams.detail;
    $scope.updateBreadcrumb([
      {name:'view1', url: routeBuilder.getTableUrl()},
      {name:'detail ' + id, url: routeBuilder.getTableDetailUrl(id)}
    ]);

    $scope.model = {
      data: null
    };

    httpApi.getTable1Detail(id).then(function (evt) {
      $scope.model.data = evt.data.data;
    });
  };
  controllers.TableDetailViewCtrl.$inject = ['$scope', '$routeParams', 'httpApi', 'routeBuilder'];

  return controllers;

});