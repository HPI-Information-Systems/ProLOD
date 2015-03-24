/*global define */

'use strict';

define(function () {

  /* Controllers */

  var controllers = {};

  controllers.PredicateViewCtrl = function ($scope, $routeParams, httpApi) {
    $scope.updateView(['predicates']);

    $scope.model = {
      gridOptions: {
        data: 'model.data'
      },
      data: []
    };

    httpApi.getPredicates($routeParams.dataset, $routeParams.group).then(function (evt) {
      $scope.model.data = evt.data.data;
    });
  };
  controllers.PredicateViewCtrl.$inject = ['$scope', '$routeParams', 'httpApi'];


  controllers.InversePredicateViewCtrl = function ($scope, $routeParams, httpApi) {
    $scope.updateView(['inversePredicates']);

    $scope.model = {
      gridOptions: {
        data: 'model.data'
      },
      data: []
    };

    httpApi.getInversePredicates($routeParams.dataset, $routeParams.group).then(function (evt) {
      $scope.model.data = evt.data.data;
    });
  };
  controllers.InversePredicateViewCtrl.$inject = ['$scope', '$routeParams', 'httpApi'];


  controllers.AssociationRuleViewCtrl = function ($scope, $routeParams, httpApi) {
    $scope.updateView(['associationRules']);

    $scope.model = {
      gridOptions: {
        data: 'model.data'
      },
      data: []
    };

    httpApi.getAssociationRules($routeParams.dataset, $routeParams.group).then(function (evt) {
      $scope.model.data = evt.data.data;
    });
  };
  controllers.AssociationRuleViewCtrl.$inject = ['$scope', '$routeParams', 'httpApi'];


  controllers.SynonymViewCtrl = function ($scope, $routeParams, httpApi) {
    $scope.updateView(['predicates']);

    $scope.model = {
      gridOptions: {
        data: 'model.data'
      },
      data: []
    };

    httpApi.getSynonyms($routeParams.dataset, $routeParams.group).then(function (evt) {
      $scope.model.data = evt.data.data;
    });
  };
  controllers.SynonymViewCtrl.$inject = ['$scope', '$routeParams', 'httpApi'];


  controllers.FactGenerationViewCtrl = function ($scope, $routeParams, httpApi) {
    $scope.updateView(['factGeneration']);

    $scope.model = {
      gridOptions: {
        data: 'model.data'
      },
      data: []
    };

    httpApi.getFactGeneration($routeParams.dataset, $routeParams.group).then(function (evt) {
      $scope.model.data = evt.data.data;
    });
  };
  controllers.FactGenerationViewCtrl.$inject = ['$scope', '$routeParams', 'httpApi'];


  controllers.SuggestionViewCtrl = function ($scope, $routeParams, httpApi) {
    $scope.updateView(['suggestions']);

    $scope.model = {
      gridOptions: {
        data: 'model.data'
      },
      data: []
    };

    httpApi.getSuggestions($routeParams.dataset, $routeParams.group).then(function (evt) {
      $scope.model.data = evt.data.data;
    });
  };
  controllers.SuggestionViewCtrl.$inject = ['$scope', '$routeParams', 'httpApi'];


  controllers.UniquenessViewCtrl = function ($scope, $routeParams, httpApi) {
    $scope.updateView(['uniqueness']);

    $scope.model = {
      gridOptions: {
        data: 'model.data'
      },
      data: []
    };

    httpApi.getUniqueness($routeParams.dataset, $routeParams.group).then(function (evt) {
      $scope.model.data = evt.data.data;
    });
  };
  controllers.UniquenessViewCtrl.$inject = ['$scope', '$routeParams', 'httpApi'];


  return controllers;

});