define(["angular", "./services"], function () {

    angular.module('Prolod2.services').factory('httpApi', ['$http', function ($http) {
        function uri(parts) {
            return parts.map(encodeURIComponent).join('/')
        }

        var cache = new Map();

        function getCached(uri, params) {
            var key = JSON.stringify({u:uri, p:params});
            if (cache[key])
                return cache[key];
            var result = $http.get(uri, params);
            cache[key] = result;
            return result;
        }

        return {
            getDatasets: function () {
                return getCached(uri(['server', 'datasets']));
            },
            getGraphStatistics: function (dataset, groups) {
                return getCached(uri(['server', 'graphstatistics', dataset]), {groups: groups});
            },
            getGraphPatternStatistics: function (dataset, groups, pattern) {
                return getCached(uri(['server', 'graphstatistics', dataset, 'pattern', pattern]), {groups: groups});
            },
            getGraphDetail: function (dataset, groups, pattern, detail) {
                return $http.get(uri(['server', 'graphstatistics', dataset, 'pattern', pattern, detail]),
                    {groups: groups});
            },
            getGiantComponent: function (dataset, giantomponent){
                return $http.get(uri(['server', 'bigcomponent', dataset]));
            },
            getEntityDetail: function (dataset, entity){
                return $http.get(uri(['server', 'entitydetails', dataset, entity]));
            },

            getTable1: function () {
                return $http.get('persons');
            },
            getTable1Detail: function (id) {
                return $http.get(uri(['persons', id]));
            },
            getPredicates: function (dataset, group) {
                return $http.get(uri(['predicates', dataset, group]));
            },
            getInversePredicates: function (dataset, group) {
                return $http.get(uri(['inversePredicates', dataset, group]));
            },
            getAssociationRules: function (dataset, group) {
                return $http.get(uri(['associationRules', dataset, group]));
            },
            getSynonyms: function (dataset, group) {
                return $http.get(uri(['synonyms', dataset, group]));
            },
            getFactGeneration: function (dataset, group) {
                return $http.get(uri(['factGeneration', dataset, group]));
            },
            getSuggestions: function (dataset, group) {
                return $http.get(uri(['suggestions', dataset, group]));
            },
            getUniqueness: function (dataset, group) {
                return $http.get(uri(['uniqueness', dataset, group]));
            }
        }
    }]);

});