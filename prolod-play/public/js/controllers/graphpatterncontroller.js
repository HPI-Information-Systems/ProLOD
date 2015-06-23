'use strict';

define(['angular', './controllers', 'dimple'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers').controller("GraphPatternCtrl", ['$scope', '$routeParams', '$timeout', 'routeBuilder', 'httpApi', 'colorHash','$modal',
        function ($scope, $routeParams, $timeout, routeBuilder, httpApi, colorHash, $modal) {
            var pattern = $routeParams.pattern;
            $scope.updateBreadcrumb([
                {name: 'Graphs', url: routeBuilder.getGraphUrl()},
                {name: 'Pattern ' + pattern, url: routeBuilder.getGraphPatternUrl(pattern)}
            ]);

            $scope.nodeClick = function (node) {
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'myModalContent.html',
                    controller: 'PopupCtrl',
                    size: 'lg',
                    resolve: {
                        node: function() {
                            return node;
                        }
                    }
                });
            };

            $scope.data = {
                pattern: {}
            };

            $scope.colorFunction = function (d) {
                return colorHash(d.group);
            };

            $scope.loading = true;

            $scope.limit = 10;
            function increaseLimit() {
                $scope.limit += 3;

                if ($scope.limit < $scope.data.pattern.length) {
                    $timeout(increaseLimit, 50);
                }
            }

            httpApi.getGraphPatternStatistics($routeParams.dataset, $routeParams.group, pattern).then(function (data) {
                var stats = data.data.statistics;
                $scope.data.pattern = stats.patterns;

                drawPieChart(stats.classDistribution, colorHash);

                $scope.loading = false;
                increaseLimit();

            });
        }
    ]);

    function drawPieChart(distribution,colorHash) {
        var c = "class", v = "value";
        var keys = Object.keys(distribution);
        var data = [];
        var max = 0;
        for(var i in keys) {
            max = Math.max(max, distribution[keys[i]]);
        }
        for(var i in keys) {
            var obj = {};
            obj[c] = keys[i];
            obj[v] = distribution[keys[i]] || 0;
            if(obj[v] > max/100) {
                data.push(obj);
            }
        }

        var svg = dimple.newSvg("#pie-chart", "100%", "100%");
        var myChart = new dimple.chart(svg, data);
        myChart.setBounds(20, 20, 230, 180);
        myChart.addMeasureAxis("p", "value");
        myChart.addSeries("class", dimple.plot.pie);
        myChart.addLegend(250, 20, 90, 300, "left");

        for(var i in keys) {
            var k = keys[i];
            var color = k;
            if (k == "unknown") {
                color = null;
            }
            myChart.assignColor(k, colorHash(color));
        }
        myChart.draw();
    }
});


