(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('cycleTimeTarget', cycleTimeTarget);

    cycleTimeTarget.$inject = ['$resource'];

    function cycleTimeTarget ($resource) {
        var resourceUrl =  'api/cycleTimeTarget/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
