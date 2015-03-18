/*global define */

'use strict';

define(function () {

  /* Controllers */

  var controllers = {};

  controllers.MyCtrl1 = function ($scope) {
    $scope.updateParams('view1');

    $scope.model = {
      data: [
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
    }
  };
  controllers.MyCtrl1.$inject = ['$scope'];


  controllers.MyCtrl2 = function ($scope) {
    $scope.updateParams('view2');

  };
  controllers.MyCtrl2.$inject = ['$scope'];


  controllers.MyCtrl3 = function ($scope) {
    $scope.updateParams('view3');

    $scope.model = {
      data: [
        {
          "property": "instanceOf",
          "object": "Human",
          "p": 0.4
        },
        {
          "property": "dateOfBirth",
          "object": "1.1.12",
          "p": 0.2
        },
        {
          "property": "first name",
          "object": "peter",
          "p": 0.1
        }
      ]
    }
  };
  controllers.MyCtrl3.$inject = ['$scope'];

  return controllers;

});