'use strict';

define(['angular', './controllers'], function (angular) {

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

    angular.module('Prolod2.controllers').controller('PopupCtrl', function ($scope, $modalInstance, items) {

        $scope.items = items;
        $scope.selected = {
            item: $scope.items[0]
        };

        $scope.ok = function () {
            $modalInstance.close($scope.selected.item);
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });

});


