'use strict';

define(['angular', './controllers', 'dimple'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers').controller("GraphPatternCtrl", ['$scope', '$routeParams', '$timeout', 'routeBuilder', 'httpApi', 'colorHash','$modal',
        function ($scope, $routeParams, $timeout, routeBuilder, httpApi, colorHash, $modal) {
            var pattern = $routeParams.pattern;
            var breadCrumbMenu = [
                {name: 'Graphs', url: routeBuilder.getGraphUrl()},
                {name: 'Pattern ' + pattern, url: routeBuilder.getGraphPatternUrl(pattern)}
            ];
            if ($routeParams.group) {
                breadCrumbMenu.push({name: 'Class ' + $routeParams.group, url: routeBuilder.getGraphPatternGroupUrl(pattern)});
            }
            $scope.updateBreadcrumb(breadCrumbMenu);

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

            $scope.limit = 28;

            $scope.increaseLimit = function() {
                if ($scope.limit < $scope.data.pattern.length) {
                    //console.log("Increasing limit");
                    $scope.limit += 14;
                }
            }

            httpApi.getGraphPatternStatistics($routeParams.dataset, $routeParams.group, pattern).then(function (data) {
                var stats = data.data.statistics;
                $scope.data.pattern = stats.patterns;

                drawPieChart(stats.classDistribution, colorHash);

                $scope.stats = stats;

                $scope.loading = false;

            });
        }
    ])
        .directive('whenScrollEnds', function() {
        return {
            restrict: "A",
            link: function(scope, element, attrs) {
                var threshold = 70;

                element.scroll(function() {
                    var visibleHeight = element.height();
                    var scrollableHeight = element.prop('scrollHeight');
                    //console.log("Visibile height element: "+visibleHeight);
                    //console.log("Scrollable height element: "+scrollableHeight);
                    var hiddenContentHeight = scrollableHeight - visibleHeight;

                    if (hiddenContentHeight - element.scrollTop() <= threshold) {
                        // Scroll is almost at the bottom. Loading more rows
                        //console.log("End of list");
                        scope.$apply(attrs.whenScrollEnds);
                    }
                });
            }
        };
    });;

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


