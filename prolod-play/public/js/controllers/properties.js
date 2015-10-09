'use strict';

define(['angular', './controllers'], function (angular) {

    angular.module('Prolod2.controllers')
        .controller("PropertiesCtrl", ['$scope', '$routeParams', 'routeBuilder', 'httpApi', 'colorHash', function ($scope, $routeParams, routeBuilder, httpApi, colorHash) {

            $scope.updateBreadcrumb([
                {name:'Properties', url: routeBuilder.getPredicateUrl()},
            ]);

            var propertyTemplate = [
                '<div class="ui-grid-cell-contents" tooltip="{{ row.entity.property }}"  tooltip-append-to-body="true" tooltip-placement="top" popupDelay="300">',
                '       {{ COL_FIELD }}',
                '</div>'
            ].join('\n');

            // table data
            $scope.model = {
                gridOptions: {
                    data: 'model.data',
                    columnDefs: [
                        {name: "Property", field: "url", type: "string", cellTemplate: propertyTemplate},
                        {name: "Occurrences", field: "occurences", type: "int"},
                        {name: "Percentage", field: "percentage", type: "float", cellFilter: 'number: 2'}
                    ]
                },
                data: []
            };

            httpApi.getProperties($routeParams.dataset, $routeParams.group).then(function (evt) {
                $scope.model.data = evt.data.data;
                var data = evt.data.data;

                // bar chart
                var svgBar = dimple.newSvg("#properties-chart", 650, 400);
                var barChart = new dimple.chart(svgBar, data);
                barChart.setBounds(350, 30, 250, 330);
                var x = barChart.addMeasureAxis("x", "occurences");
                var y = barChart.addCategoryAxis("y", "url");
                //var y = barChart.addAxis("y", "url");
                y.addOrderRule("occurences");
                barChart.addSeries("occurences", dimple.plot.bar);
                barChart.draw();

                // pie chart
                var svgPie = dimple.newSvg("#properties-piechart", "100%", "100%");

                var c = "url", v = "value";
                var propertyDist = [];
                for(var i in data) {
                    var obj = {};
                    console.log(data[i]);
                    obj[c] = data[i].url;
                    obj[v] = data[i].percentage;
                    propertyDist.push(obj);
                }

                var pieChart = new dimple.chart(svgPie, propertyDist);
                pieChart.setBounds(5, 5, 150, 150);
                pieChart.addMeasureAxis("p", "value");
                pieChart.addSeries("url", dimple.plot.pie);
                pieChart.addLegend(180, 5, 300, 150, "left");
                pieChart.draw();
            });
        }]);
});


