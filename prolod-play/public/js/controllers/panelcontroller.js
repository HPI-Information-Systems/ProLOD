'use strict';

define(['angular'], function (angular) {

    angular.module('Prolod2.controllers1', [])
        .controller("PanelCtrl", ['$scope', function ($scope) {
            $scope.model = {
                test: 1
            };
        }]);


});


