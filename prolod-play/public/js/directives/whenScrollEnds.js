"use strict";

define(['angular', 'd3', './directives'], function (angular, d3) {

    angular.module('Prolod2.directives').directive('whenScrollEnds', function () {
        return {
            restrict: "A",
            link: function (scope, element, attrs) {
                var threshold = 70;

                element.scroll(function () {
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
    });
});