define(["angular", "./services"], function () {

    angular.module('Prolod2.services').factory('httpApi', ['$http', function ($http) {
        return {
            getDatasets: function () {
                return $http.get('server/datasets');
            },
            getTable1: function () {
                return $http.get('persons');
            },
            getTable1Detail: function (id) {
                return $http.get('persons/' + encodeURIComponent(id));
            },
            getPredicates: function (dataset, group) {
                return $http.get('predicates/' + encodeURIComponent(dataset) + '/'
                                 + encodeURIComponent(group));
            },
            getInversePredicates: function (dataset, group) {
                return $http.get('inversePredicates/' + encodeURIComponent(dataset) + '/'
                                 + encodeURIComponent(group));
            },
            getAssociationRules: function (dataset, group) {
                return $http.get('associationRules/' + encodeURIComponent(dataset) + '/'
                                 + encodeURIComponent(group));
            },
            getSynonyms: function (dataset, group) {
                return $http.get('synonyms/' + encodeURIComponent(dataset) + '/'
                                 + encodeURIComponent(group));
            },
            getFactGeneration: function (dataset, group) {
                return $http.get('factGeneration/' + encodeURIComponent(dataset) + '/'
                                 + encodeURIComponent(group));
            },
            getSuggestions: function (dataset, group) {
                return $http.get('suggestions/' + encodeURIComponent(dataset) + '/'
                                 + encodeURIComponent(group));
            },
            getUniqueness: function (dataset, group) {
                return $http.get('uniqueness/' + encodeURIComponent(dataset) + '/'
                                 + encodeURIComponent(group));
            }
        }
    }]);

});