'use strict';

define(['angular', './controllers'], function (angular) {

    angular.module('Prolod2.controllers')
        .controller("UniquenessCtrl", ['$scope', '$routeParams', 'routeBuilder', 'httpApi', function ($scope, $routeParams, routeBuilder, httpApi) {
            $scope.updateBreadcrumb([
                {name:'Key Discovery', url: routeBuilder.getUniquenessUrl()},
            ]);

            // table data
            $scope.model = {
                gridOptions: {
                    data: 'model.data',
                    //columnDefs: [...]
                },
                data: []
            };

            httpApi.getUniqueness($routeParams.dataset, $routeParams.group).then(function (evt) {
                $scope.model.data = evt.data.data;

                // draw chart
                var svg = dimple.newSvg("#keyness-chart", 800, 400);

                var data = [
                    {id: 1, property: "rdf:type", cluster: "person", uniqueness: 0.31, density: 0.1},
                    {id: 2, property: "foaf:name", cluster: "person", uniqueness: 0.26, density: 0.3},
                    {id: 3, property: "dbpedia:bla", cluster: "person", uniqueness: 0.21, density: 0.8},
                    {id: 4, property: "rdf:type", cluster: "car", uniqueness: 0.23, density: 0.24},
                    {id: 5, property: "dbpedia:bla", cluster: "car", uniqueness: 0.61, density: 0.2}
                ];

                var myChart = new dimple.chart(svg, data);
                myChart.setBounds(60, 30, 500, 330);
                myChart.addMeasureAxis("x", "density");
                myChart.addMeasureAxis("y", "uniqueness");
                // first parameters should be unique as dimple only shows unique values
                // they are shown in the tooltip
                // the last one is for the legend
                myChart.addSeries(["id", "property", "cluster"], dimple.plot.bubble);
                myChart.addLegend(580, 30, 50, 350, "left");
                myChart.draw();
            });


        }]);
});


