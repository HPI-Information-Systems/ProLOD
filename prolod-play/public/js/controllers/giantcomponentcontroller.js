'use strict';

define(['angular', './controllers'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers')
        .controller("GiantComponentCtrl", ['$scope', '$routeParams', 'routeBuilder', 'httpApi', function ($scope, $routeParams, routeBuilder, httpApi) {

            $scope.test = "this is a test";



        }]);
});


