'use strict';

define(['angular', './controllers', 'dimple'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers').controller("GCPatternCtrl", ['$scope', '$routeParams', '$timeout', 'routeBuilder', 'httpApi', 'colorHash','$modal',
        function ($scope, $routeParams, $timeout, routeBuilder, httpApi, colorHash, $modal) {
            var pattern = $routeParams.pattern;
            var coloredPattern = $routeParams.coloredPattern;

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

            $scope.limit = 34;

            $scope.increaseLimit = function() {
                if ($scope.limit < $scope.data.pattern.length) {
                    //console.log("Increasing limit");
                    var increase =  function(times, amount) {
                        $scope.limit += amount;
                        times--;
                        if(times > 0) {
                            setTimeout(increase, 100, times, amount);
                        }
                    };
                    increase(3, 5);
                }
            };

            httpApi.getGCPatternStatistics($routeParams.dataset, $routeParams.group, pattern, coloredPattern).then(function (data) {
                var stats = data.data.statistics;
                $scope.data.pattern = stats.patterns;

                drawPieChart(stats.classDistribution, colorHash);

                $scope.stats = stats;

                $scope.loading = false;

                $scope.isoGroup = stats.patterns[0].isoGroup
                $scope.id = stats.patterns[0].id
                var isoGroup = $scope.isoGroup
                var id = $scope.id
                var breadCrumbMenu = [
                    {name: 'Graphs', url: routeBuilder.getGraphUrl()},
                    {name:'Giant Component', url: routeBuilder.getGiantComponentUrl()},
                    {name: 'Pattern ' + id, url: routeBuilder.getGCPatternIsoUrl(id)},
                    {name: 'Colored Pattern ' + isoGroup, url: routeBuilder.getGCPatternUrl(id, isoGroup)}
                ];
                if ($routeParams.group) {
                    breadCrumbMenu.push({name: 'Class ' + $routeParams.group, url: routeBuilder.getGraphPatternGroupUrl(pattern)});
                }
                $scope.updateBreadcrumb(breadCrumbMenu);

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
        myChart.setBounds(5, 5, 150, 150);
        myChart.addMeasureAxis("p", "value");
        myChart.addSeries("class", dimple.plot.pie);
        myChart.addLegend(180, 5, 100, 150, "left");

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


