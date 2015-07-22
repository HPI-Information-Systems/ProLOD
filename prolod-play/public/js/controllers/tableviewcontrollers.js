/*global define */

'use strict';

define(['angular', 'ui-grid'], function () {

  var controllers = {};

  // This creates a controller for simple tables
  var createGenericTableView = function (name, httpCall, columnDefs) {
    var ctrl = function ($scope, $routeParams, httpApi, routeBuilder) {
      $scope.updateBreadcrumb([{name: name, url: routeBuilder.getGenericUrl(name)}]);

      $scope.model = {
        gridOptions: {
          data: 'model.data',
          columnDefs: columnDefs
        },
        data: []
      };

      httpApi[httpCall]($routeParams.dataset, $routeParams.group).then(function (evt) {
        $scope.model.data = evt.data.data;
      });
    };
    ctrl.$inject = ['$scope', '$routeParams', 'httpApi', 'routeBuilder'];
    return ctrl
  };

  controllers.InversePredicateViewCtrl = createGenericTableView("Inverse Properties", "getInversePredicates");

  controllers.AssociationRuleViewCtrl = createGenericTableView('Association Rules', "getAssociationRules");

  controllers.SynonymViewCtrl = createGenericTableView('Synonyms', "getSynonyms");
/*
  controllers.FactGenerationViewCtrl = createGenericTableView('factGeneration', "getFactGeneration");

  controllers.SuggestionViewCtrl = createGenericTableView('suggestions',"getSuggestions");
*/
  return controllers;

});