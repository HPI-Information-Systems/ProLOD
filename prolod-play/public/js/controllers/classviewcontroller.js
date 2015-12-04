'use strict';

define(['angular', './controllers', 'd3'], function (angular) {
    // controller for the lower panel
    angular.module('Prolod2.controllers').controller("ClassViewCtrl", ['$scope', '$window', '$routeParams', 'routeBuilder', 'httpApi',
       function ($scope, $window, $routeParams, routeBuilder, httpApi) {
           $scope.updateBreadcrumb([{name: 'Classes', url: routeBuilder.getClassViewUrl()}]);

           var width = 960,
               height = 700,
               radius = Math.min(width, height) / 2;

           var x = d3.scale.linear()
               .range([0, 2 * Math.PI]);

           var y = d3.scale.sqrt()
               .range([0, radius]);

           var color = d3.scale.category20c();

           var svg = d3.select("#class-sun-burst").append("svg")
               .attr("width", width)
               .attr("height", height)
               .append("g")
               .attr("transform", "translate(" + width / 2 + "," + (height / 2 + 10) + ")");

           var partition = d3.layout.partition()
               .value(function(d) { return d.size; });

           var arc = d3.svg.arc()
               .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
               .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
               .innerRadius(function(d) { return Math.max(0, y(d.y)); })
               .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });

           var text = svg.append("text")
               .attr("x", 0)
               .attr("y", 0)
               .attr("dy", "0.35em");

           d3.json("/server/classes/" + $routeParams.dataset, function(error, root) {
               if (error) throw error;

               var path = svg.selectAll("path")
                   .data(partition.nodes(root.classes))
                   .enter().append("path")
                   .attr("d", arc)
                   .style("fill", getColor)
                   .on("click", click)
                   .on("mouseover", mouseover)
                   .on("mouseout", mouseout);

               function click(d) {
                   path.transition()
                       .duration(750)
                       .attrTween("d", arcTween(d));
               }

               function getColor(d) {
                   return color((d.children ? d : d.parent).name);
               }
               
               function mouseover(d) {
                   var percentage = (100 * d.size / root.classes.size).toPrecision(3);
                   text.html("<tspan style='font-size: 30px' x=0 dy='-20'>" + (d.size) + "</tspan>"
                             + "<tspan style='font-size: 15px' x=0 dy='25'>" + d.name + "</tspan>"
                             + "<tspan style='font-size: 30px' x=0 dy='35'>" + percentage + "%</tspan>");

                   d3.selectAll("path")
                       .filter(function(d1) { return d === d1; })
                       .style("opacity", 0.5);
               }
               function mouseout(d) {
                   text.html("");

                   d3.selectAll("path")
                       .filter(function(d1) { return d === d1; })
                       .style("opacity", 1);
               }

           });

           d3.select(self.frameElement).style("height", height + "px");

           // Interpolate the scales!
           function arcTween(d) {
               var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
                   yd = d3.interpolate(y.domain(), [d.y, 1]),
                   yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
               return function(d, i) {
                   return i
                       ? function(t) { return arc(d); }
                       : function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
               };
           }

       }]);
});


