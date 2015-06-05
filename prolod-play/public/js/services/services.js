/*global define */

'use strict';

define(['angular'], function(angular) {

/* Services */

// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('Prolod2.services', [])
    .value('version', '0.1')
    .value('Events', Object.freeze({
        VIEWCHANGED: 'VIEWCHANGED'
    }));

});

