'use strict';

define(['angular', './controllers'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers')
        .controller("PanelCtrl", ['$scope', function ($scope) {
            $scope.model = {
                test: 1
            };
        }]);

});


