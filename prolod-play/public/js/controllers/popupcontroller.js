'use strict';

define(['angular', './controllers'], function (angular) {

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

    angular.module('Prolod2.controllers').controller('PopupCtrl', function ($scope, $modalInstance, $routeParams, httpApi, node) {

        var cellTemplate = '<div class="ui-grid-cell-contents" title="{{COL_FIELD}}">{{COL_FIELD}}</div>';

        $scope.model = {
            gridOptions: {
                data: 'model.data',
                rowHeight: 'auto',
                columnDefs: [
                    {name: 'Predicate', field: 'p', type: 'string', cellTemplate: cellTemplate},
                    {name: 'Object', field:'o', type: 'string', cellTemplate: cellTemplate}
                ]
            },
            data: [],
            downloaded: false
        };

        httpApi.getEntityDetail($routeParams.dataset, node.dbId).then(function(data){
                console.log(data);
                $scope.model.data = data.data.entity.triples;
                $scope.triples = data.data.entity.triples.length;
                $scope.uri = data.data.entity.url;
                $scope.label = data.data.entity.label;
                $scope.downloaded = true;
            }
        );


        $scope.ok = function () {
            $modalInstance.close();
        };

    });

});


