"use strict";

/**
 *  Creates urls for each view. the params parameter is optional and can be used
 *  to override the dataset and the groups but this shouldn't be neccessary in most cases.
 */

define(["angular", "./services"], function () {
    angular.module('Prolod2.services').factory('routeBuilder', ['$route', function ($route) {
        function buildUri(parts, args) {
            var url = "/" + parts.map(encodeURIComponent).join('/');

            parts = [];
            for (var arg in args) {
                if (!args.hasOwnProperty(arg)) {
                    continue;
                }
                var values = args[arg];
                if (!angular.isArray(values)) {
                    values = [values]
                }
                var encodedArg = encodeURIComponent(arg);
                values.forEach(function (value) {
                    if (angular.isDefined(value)) {
                        parts.push(encodedArg + '=' + encodeURIComponent(value));
                    }
                });
            }
            if (parts.length) {
                url += '?' + parts.join('&');
            }
            return url;
        }

        return {
            getOverviewUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['view0', params.dataset], {group: params.group});
            },
            getGraphUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['graphstatistics', params.dataset], {group: params.group});
            },
            getGraphPatternUrl: function (pattern, coloredPattern, params) {
                params = params || $route.current.params;
                return buildUri(['graphstatistics', params.dataset, 'pattern', pattern, 'coloredPattern', coloredPattern]);
            },
            getGraphPatternIsoUrl: function (pattern, params) {
                params = params || $route.current.params;
                return buildUri(['graphstatisticsiso', params.dataset, 'pattern', pattern]);
            },
            getGraphPatternGroupUrl: function (pattern, params) {
                params = params || $route.current.params;
                return buildUri(['graphstatistics', params.dataset, 'pattern', pattern], {group: params.group});
            },
            getGraphDetailUrl: function (pattern, detail, params) {
                params = params || $route.current.params;
                return buildUri(['graphstatistics', params.dataset, 'pattern', pattern, detail], {group: params.group});
            },
            getGiantComponentUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['graphstatistics', params.dataset, 'giantComponent'], {group: params.group});
            },
            getGCPatternIsoUrl: function (pattern, params) {
                params = params || $route.current.params;
                return buildUri(['giantcomponentiso', params.dataset, 'pattern', pattern]);
            },
            getGCPatternUrl: function (pattern, coloredPattern, params) {
                params = params || $route.current.params;
                return buildUri(['giantcomponent', params.dataset, 'pattern', pattern, 'coloredPattern', coloredPattern]);
            },
            getChartsUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['charts', params.dataset], {group: params.group});
            },
            getTableUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['view1', params.dataset], {group: params.group});
            },
            getTableDetailUrl: function (detail, params) {
                params = params || $route.current.params;
                return buildUri(['view1', params.dataset, detail], {group: params.group});
            },
            getPredicateUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['properties', params.dataset], {group: params.group});
            },
            getClassViewUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['classes', params.dataset], {group: params.group});
            },
            getInversePredicateUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['inversePredicates', params.dataset], {group: params.group});
            },
            getAssociationRuleUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['associationRules', params.dataset], {group: params.group});
            },
            getSynonymsUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['synonyms', params.dataset], {group: params.group});
            },
            getUniquenessUrl: function (params) {
                params = params || $route.current.params;
                return buildUri(['uniqueness', params.dataset], {group: params.group});
            },
            getGenericUrl: function (view, params) {
                params = params || $route.current.params;
                return buildUri([view, params.dataset], {group: params.group});
            },
            getSimilarGraphPatterns: function (params) {
                params = params || $route.current.params;
                return buildUri(['graphstatistics', params.dataset, 'similarpatterns'], {group: params.group});
            },
            getGraphSimilarPatternUrl: function (pattern, params) {
                params = params || $route.current.params;
                return buildUri(['graphstatistics', params.dataset, 'similarpattern', pattern]);
            }
        }
    }]);

});