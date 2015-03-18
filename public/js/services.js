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
      getSources: function() {
        return $http.get('sources');
      },
      getView1: function() {
        //TODO...
      }
    }
  }]);

});