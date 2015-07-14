'use strict';

define(['angular', './controllers', 'dimple'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers').controller("GraphCtrl", ['$scope', '$window', '$routeParams', '$timeout', 'routeBuilder', 'httpApi',
        function ($scope, $window, $routeParams, $timeout, routeBuilder, httpApi, $modal) {
            $scope.updateBreadcrumb([{name: 'Graphs', url: routeBuilder.getGraphUrl()}]);

            $scope.data = {
                pattern: {},
                chart: {}
            };

            $scope.colorFunction = function (d) {
                return 'white';
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
                    increase(5, 3);
                }
            };

            httpApi.getGraphStatistics($routeParams.dataset, $routeParams.group).then(function (data) {
                var stats = data.data.statistics;
                $scope.data.pattern = stats.patterns;

                $scope.loading = false;

                drawChart(stats.nodeDegreeDistribution, $scope, $window);

                $scope.stats = stats;
            });
        }
    ]);

    // Pass in an axis object and an interval.
    var cleanAxis = function (axis, oneInEvery) {
        // This should have been called after draw, otherwise do nothing
        if (axis.shapes.length > 0) {
            // Leave the first label
            var del = 0;
            // If there is an interval set
            if (oneInEvery > 1) {
                // Operate on all the axis text
                axis.shapes.selectAll("text").each(function (d) {
                    // Remove all but the nth label
                    if (del % oneInEvery !== 0) {
                        this.remove();
                        // Find the corresponding tick line and remove
                        axis.shapes.selectAll("line").each(function (d2) {
                            if (d === d2) {
                                this.remove();
                            }
                        });
                    }
                    del += 1;
                });
            }
        }
    };

    function drawChart(distribution, $scope, $window) {
        var xaxis = "node degree", yaxis = "number of nodes", order = "order";

        var keys = Object.keys(distribution).map(function(i) { return parseInt(i, 10)});
        if(!keys.length) {
            return;
        }
        //var max = Math.max.apply(null, keys);
        var data = [];
        //for(var i=1; i<max; i++) {
        var MAX_BAR_CHARTS = 200;
        var MIN_BAR_CHARTS = 4;

        var additional = 0;
        for(var i in keys) {
            var obj = {};
            obj[xaxis] = i;
            obj[yaxis] = distribution[i] || 0;
            obj[order] = i;
            if (obj[yaxis] > 0 && obj[xaxis] < MAX_BAR_CHARTS) { // limit number of charts for readability
                data.push(obj);
            } else {
                additional += obj[yaxis];
            }
        }
        for(i=keys.length; i<=MIN_BAR_CHARTS; i++) {
            obj = {};
            obj[xaxis] = new Array(i+1).join(" "); // repeat spaces to create unique labels for x-axis
            obj[yaxis] = 0;
            obj[order] = i;
            data.push(obj);
        }
        if(additional > 0) {
            obj = {};
            obj[xaxis] = ">=" + MAX_BAR_CHARTS;
            obj[yaxis] = additional;
            obj[order] = MAX_BAR_CHARTS;
            data.push(obj);
        }


        var width = 500, height = 300;
        var svg = dimple.newSvg("#distribution-chart", "100%", "100%");
        //svg.attr("preserveAspectRatio", "xMinYMin meet");
        //svg.attr("viewBox", "0 0 " + width + " " + height);
        var myChart = new dimple.chart(svg, data);
        var border = 70;
        myChart.setMargins(border, 5, 5, border);
        var x = myChart.addCategoryAxis("x", xaxis);
        x.addOrderRule(order);
        var y = myChart.addMeasureAxis("y", yaxis);
        y.tickFormat = "d";
        myChart.addSeries(null, dimple.plot.bar);
        myChart.draw();

        var maxLabels = 50;
        cleanAxis(x, Math.ceil(data.length / maxLabels));

        var redraw = function () {
            myChart.draw(0, true);
        };

        angular.element($window).on('resize', redraw);

        $scope.$on('$destroy', function() {
            angular.element($window).off('resize', redraw);
        })
    }

});
