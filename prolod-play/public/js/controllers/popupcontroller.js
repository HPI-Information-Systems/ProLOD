'use strict';

define(['angular', './controllers'], function (angular) {

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

    angular.module('Prolod2.controllers').controller('PopupCtrl', function ($scope, $modalInstance, $routeParams, httpApi, node) {
        httpApi.getEntityDetail($routeParams.dataset, node.uri).then(function(data){
                console.log(data);
                $scope.triples = data.data.entity.triples;
                $scope.uri = data.data.entity.url;
                $scope.label = data.data.entity.label;

            }
        );


        $scope.ok = function () {
            $modalInstance.close();
        };

    });

});


