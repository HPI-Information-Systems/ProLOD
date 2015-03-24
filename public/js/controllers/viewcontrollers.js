/*global define */

'use strict';

define(function () {

  /* Controllers */

  var controllers = {};


  controllers.IndexViewCtrl = function ($scope, $routeParams) {
    $scope.updateView(['index'], $routeParams);
  };
  controllers.IndexViewCtrl.$inject = ['$scope', '$routeParams'];


  controllers.MyCtrl0 = function ($scope) {
    $scope.updateView(['view0']);

  };
  controllers.MyCtrl0.$inject = ['$scope'];


  controllers.TableViewCtrl = function ($scope) {
    $scope.updateView(['view1']);

    $scope.url = function (detail) {
      return $scope.makeUrl({view: ['view1', detail]})
    };

    var data = [
      {
        firstName: 'Cox',
        lastName: 'Carney',
        age: 12
      },
      {
        firstName: 'Peter',
        lastName: 'Lustig',
        age: 30
      }
    ];

    $scope.model = {
      gridOptions: {
        data: 'model.data',
        columnDefs: [
          {
            name: '', width: 20, field: 'firstName', enableSorting: false,
            cellTemplate: '<div class="ui-grid-cell-contents"><a href="#{{grid.appScope.url(COL_FIELD)}}"><i class="glyphicon glyphicon-zoom-in"/></a></div>'
          },
          {name: 'firstName'},
          {name: 'lastName'},
          {name: 'age'}
        ]
      },
      data: data
    }
  };
  controllers.TableViewCtrl.$inject = ['$scope'];

  controllers.TableDetailViewCtrl = function ($scope, $routeParams) {
    $scope.updateView(['view1', $routeParams.detail]);

    $scope.model = {
      name: $routeParams.detail
    }
  };
  controllers.TableDetailViewCtrl.$inject = ['$scope', '$routeParams'];


  controllers.ChartsViewCtrl = function ($scope) {
    $scope.updateView(['view2']);

    $scope.chart0 = {
      labels: ["Download Sales", "In-Store Sales", "Mail-Order Sales"],
      data: [300, 500, 800]

    };
    $scope.chart1 = {
      labels: ['2006', '2007', '2008', '2009', '2010', '2011', '2012'],
      series: ['Series A', 'Series B'],
      data: [
        [65, 59, 80, 81, 56, 55, 40],
        [28, 48, 40, 19, 86, 27, 90]
      ]
    }
  };
  controllers.ChartsViewCtrl.$inject = ['$scope'];


  controllers.Table2View = function ($scope) {
    $scope.updateView(['view3']);

    $scope.model = {
      gridOptions: {
        data: 'model.data'
      },
      data: [
        {
          property: 'instanceOf',
          object: 'Human',
          p: 0.4
        },
        {
          property: 'dateOfBirth',
          object: '1.1.12',
          p: 0.2
        },
        {
          property: 'first name',
          object: 'peter',
          p: 0.1
        }
      ]
    }
  };
  controllers.Table2View.$inject = ['$scope'];

  controllers.GraphCtrl = function ($scope) {
      var width = 960,
          height = 500;

      var color = d3.scale.category20();

      var force = d3.layout.force()
          .charge(-120)
          .linkDistance(30)
          .size([width, height]);

      var svg = d3.select("#graph").append("svg")
          .attr("width", width)
          .attr("height", height);

      d3.json("miserables.json", function(error, graph) {
          force
              .nodes(graph.nodes)
              .links(graph.links)
              .start();

          var link = svg.selectAll(".link")
              .data(graph.links)
              .enter().append("line")
              .attr("class", "link")
              .style("stroke-width", function(d) { return Math.sqrt(d.value); });

          var node = svg.selectAll(".node")
              .data(graph.nodes)
              .enter().append("circle")
              .attr("class", "node")
              .attr("r", 5)
              .style("fill", function(d) { return color(d.group); })
              .call(force.drag);

          node.append("title")
              .text(function(d) { return d.name; });

          force.on("tick", function() {
              link.attr("x1", function(d) { return d.source.x; })
                  .attr("y1", function(d) { return d.source.y; })
                  .attr("x2", function(d) { return d.target.x; })
                  .attr("y2", function(d) { return d.target.y; });

              node.attr("cx", function(d) { return d.x; })
                  .attr("cy", function(d) { return d.y; });
          });
      });

  };
    controllers.GraphCtrl.$inject = ['$scope'];

  return controllers;

});