/*global define */

'use strict';

define(['angular'], function(angular) {

/* Filters */

angular.module('Prolod2.filters', []).
  filter('interpolate', ['version', function(version) {
    return function(text) {
      return String(text).replace(/\%VERSION\%/mg, version);
    }
  }]);

});