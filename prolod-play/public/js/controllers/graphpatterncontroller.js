'use strict';

define(['angular', './controllers'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers')
        .controller("GraphPatternCtrl", ['$scope', '$routeParams', 'httpApi', function ($scope, $routeParams, httpApi) {
            $scope.updateView(['graphs', 'pattern']);

            $scope.statistics = {};

            httpApi.getGraphPatternStatistics($routeParams.dataset, [$routeParams.group], $routeParams.pattern).then(function(data) {
                $scope.statistics = data.data.statistics;
            });
        }]);

});


