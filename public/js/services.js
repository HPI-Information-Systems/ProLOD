/*global define */

'use strict';

define(['angular'], function(angular) {

/* Services */

// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('Prolod2.services', []).
  value('version', '0.1').
  factory('httpApi', ['$http', function($http) {
    return {
      getDatasets: function() {
        return $http.get('datasets');
      },
      getTable1: function() {
        return $http.get('persons');
      },
      getTable1Detail: function(id) {
        return $http.get('persons/' + encodeURIComponent(id));
      }
    }
  }]);

});