define(["angular", "./services"], function () {

    angular.module('Prolod2.services').factory('httpApi', ['$http', function ($http) {
        function uri(parts) {
            return parts.map(encodeURIComponent).join('/')
        }

        var cache = new Map();

        function getCached(uri, config) {
            var key = JSON.stringify({u:uri, c:config});
            if (cache[key])
                return cache[key];
            var result = $http.get(uri, config);
            cache[key] = result;
            return result;
        }

        return {
            getDatasets: function () {
                return getCached(uri(['server', 'datasets']));
            },
            getGraphStatistics: function (dataset, groups) {
                return getCached(uri(['server', 'graphstatistics', dataset]), {params: {groups: groups}});
            },
            getGraphSimilarPatterns: function (dataset, groups) {
                return getCached(uri(['server', 'graphsimilarpatterns', dataset]), {params: {groups: groups}});
            },
            getGraphSimilarPattern: function (dataset, groups, pattern) {
                return getCached(uri(['server', 'graphsimilarpatterns', dataset, 'pattern', pattern]), {params: {groups: groups}});
            },
            getGraphPatternStatistics: function (dataset, groups, pattern) {
                return $http.get(uri(['server', 'graphstatistics', dataset, 'pattern', pattern]), {params: {groups: groups}});
            },
            getGraphIsoPatternStatistics: function (dataset, groups, pattern) {
                return $http.get(uri(['server', 'graphstatisticsiso', dataset, 'pattern', pattern]), {params: {groups: groups}});
            },
            getGraphDetail: function (dataset, groups, pattern, detail) {
                return $http.get(uri(['server', 'graphstatistics', dataset, 'pattern', pattern, detail]),
                    {groups: groups});
            },
            getGiantComponent: function (dataset, groups){
                return $http.get(uri(['server', 'giantcomponent', dataset]), {params: {groups: groups}});
            },
            getGCIsoPatternStatistics: function (dataset, groups, pattern) {
                return $http.get(uri(['server', 'giantcomponentiso', dataset, 'pattern', pattern]), {params: {groups: groups}});
            },
            getGCPatternStatistics: function (dataset, groups, pattern) {
                return $http.get(uri(['server', 'giantcomponent', dataset, 'pattern', pattern]), {params: {groups: groups}});
            },
            getEntityDetail: function (dataset, entity){
                return $http.get(uri(['server', 'entitydetails', dataset, entity]));
            },
            getProperties: function (dataset, groups) {
                return $http.get(uri(['server', 'properties', dataset]), {params: {groups: groups}});
            },
            getUniqueness: function (dataset, groups) {
                return $http.get(uri(['server', 'uniqueness', dataset]), {params: {groups: groups}});
            },


            // JUST DUMMIES:
            /*
            getTable1: function () {
                return $http.get('persons');
            },
            getTable1Detail: function (id) {
                return $http.get(uri(['persons', id]));
            },
            getPredicates: function (dataset, groups) {
                return $http.get(uri(['predicates', dataset]), {params: {groups: groups}});
            },
            getInversePredicates: function (dataset, groups) {
                return $http.get(uri(['inversePredicates', dataset]), {params: {groups: groups}});
            },
            getAssociationRules: function (dataset, groups) {
                return $http.get(uri(['associationRules', dataset]), {params: {groups: groups}});
            },
            getSynonyms: function (dataset, groups) {
                return $http.get(uri(['synonyms', dataset]), {params: {groups: groups}});
            }
            ,
            getFactGeneration: function (dataset, groups) {
                return $http.get(uri(['factGeneration', dataset]), {params: {groups: groups}});
            },
            getSuggestions: function (dataset, groups) {
                return $http.get(uri(['suggestions', dataset]), {params: {groups: groups}});
            }
            */
        }
    }]);

});