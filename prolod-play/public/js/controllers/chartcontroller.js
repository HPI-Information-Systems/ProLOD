'use strict';

define(['angular', 'd3'], function () {

    var ChartCtrl = function ($scope, routeBuilder) {
        $scope.updateBreadcrumb([{name: 'charts', url: routeBuilder.getGraphUrl()}]);


        // Pie Chart
        var width1 = 400,
            height1 = 400,
            radius = Math.min(width1, height1) / 2;

        var color = d3.scale.ordinal()
            .range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);

        var arc = d3.svg.arc()
            .outerRadius(radius - 10)
            .innerRadius(radius - 70);

        var pie = d3.layout.pie()
            .sort(null)
            .value(function(d) { return d.population; });

        var svg1 = d3.select("#piechart").append("svg")
            .attr("width", width1)
            .attr("height", height1)
            .append("g")
            .attr("transform", "translate(" + width1 / 2 + "," + height1 / 2 + ")");


        d3.csv("assets/data.csv", function(error, data) {

            data.forEach(function(d) {
                d.population = +d.population;
            });

            var g = svg1.selectAll(".arc")
                .data(pie(data))
                .enter().append("g")
                .attr("class", "arc");

            g.append("path")
                .attr("d", arc)
                .style("fill", function(d) { return color(d.data.age); });

            g.append("text")
                .attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
                .attr("dy", ".35em")
                .style("text-anchor", "middle")
                .text(function(d) { return d.data.age; });

        });

        // Bar Chart
        var margin = {top: 20, right: 20, bottom: 30, left: 40},
            width = 500 - margin.left - margin.right,
            height = 450 - margin.top - margin.bottom;

        var x = d3.scale.ordinal()
            .rangeRoundBands([0, width], .1);

        var y = d3.scale.linear()
            .range([height, 0]);

        var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom");

        var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left")
            .ticks(10, "%");

        var svg = d3.select("#barchart").append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");



        d3.tsv("assets/data.tsv", type, function(error, data) {
            x.domain(data.map(function(d) { return d.letter; }));
            y.domain([0, d3.max(data, function(d) { return d.frequency; })]);

            svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis);

            svg.append("g")
                .attr("class", "y axis")
                .call(yAxis)
                .append("text")
                .attr("transform", "rotate(-90)")
                .attr("y", 6)
                .attr("dy", ".71em")
                .style("text-anchor", "end")
                .text("Frequency");

            svg.selectAll(".bar")
                .data(data)
                .enter().append("rect")
                .attr("class", "bar")
                .attr("x", function(d) { return x(d.letter); })
                .attr("width", x.rangeBand())
                .attr("y", function(d) { return y(d.frequency); })
                .attr("height", function(d) { return height - y(d.frequency); });

        });

        function type(d) {
            d.frequency = +d.frequency;
            return d;
        }
    };
    ChartCtrl.$inject = ['$scope', 'routeBuilder'];

    return ChartCtrl;

});


