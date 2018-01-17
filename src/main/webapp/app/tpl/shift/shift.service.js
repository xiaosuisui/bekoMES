(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('Shift', Shift);

    Shift.$inject = ['$resource'];

    function Shift ($resource) {
        var resourceUrl =  'api/shifts/:id';

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
