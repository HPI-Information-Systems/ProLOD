'use strict';

angular.module('bgDirectives', [])
  .directive('bgSplitter', ['$window', function($window) {
    return {
      restrict: 'E',
      replace: true,
      transclude: true,
      scope: {
        orientation: '@'
      },      
      template: '<div class="split-panes {{orientation}}" ng-transclude></div>',
      controller: ['$scope', function ($scope) {
        $scope.panes = [];
        
        this.addPane = function(pane){
          if ($scope.panes.length > 1) 
            throw 'splitters can only have two panes';
          $scope.panes.push(pane);
          return $scope.panes.length;
        };
      }],
      link: function(scope, element, attrs) {
        var handler = angular.element('<div class="split-handler"></div>');
        var pane1 = scope.panes[0];
        var pane2 = scope.panes[1];
        var vertical = scope.orientation === 'vertical';
        var pane1Min = pane1.minSize || 0;
        var pane2Min = pane2.minSize || 0;
        var drag = false;
        var lastPos = -1;
        var lastSize = -1;

        pane1.elem.after(handler);

        function setVertical(pos, height) {
          if (pos < pane1Min) return;
          if (height - pos < pane2Min) return;

          handler.css('top', pos + 'px');
          pane1.elem.css('height', pos + 'px');
          pane2.elem.css('top', pos + 'px');

          lastPos = pos;
          lastSize = height;
        }

        function setHorizontal(pos, width) {
          if (pos < pane1Min) return;
          if (width - pos < pane2Min) return;

          handler.css('left', pos + 'px');
          pane1.elem.css('width', pos + 'px');
          pane2.elem.css('left', pos + 'px');

          lastPos = pos;
          lastSize = width;
        }

        element.bind('mousemove', function (ev) {
          if (!drag) return;
          
          var bounds = element[0].getBoundingClientRect();
          var pos = 0;
          
          if (vertical) {
            var height = bounds.bottom - bounds.top;
            pos = ev.clientY - bounds.top;

            setVertical(pos, height);
          } else {
            var width = bounds.right - bounds.left;
            pos = ev.clientX - bounds.left;

            setHorizontal(pos, width);
          }
        });
    
        handler.bind('mousedown', function (ev) { 
          ev.preventDefault();
          drag = true; 
        });
    
        angular.element(document).bind('mouseup', function (ev) {
          drag = false;
        });

        angular.element($window).bind('resize', function() {
          if(lastPos === -1) // not moved yet
            return;

          var bounds = element[0].getBoundingClientRect();

          if (vertical) {
            var height = bounds.bottom - bounds.top;
            var pos = lastPos / lastSize * height;
            setVertical(pos, height);
          } else {
            var width = bounds.right - bounds.left;
            var pos = lastPos / lastSize * width;
            setHorizontal(pos, width);
          }
        })
      }
    };
  }])
  .directive('bgPane', function () {
    return {
      restrict: 'E',
      require: '^bgSplitter',
      replace: true,
      transclude: true,
      scope: {
        minSize: '='
      },
      template: '<div class="split-pane{{index}}" ng-transclude></div>',
      link: function(scope, element, attrs, bgSplitterCtrl) {
        scope.elem = element;
        scope.index = bgSplitterCtrl.addPane(scope);
      }
    };
  });
