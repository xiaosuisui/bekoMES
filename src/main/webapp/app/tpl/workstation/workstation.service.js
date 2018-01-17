(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('Workstation', Workstation);

    Workstation.$inject = ['$resource'];

    function Workstation ($resource) {
        var resourceUrl =  'api/workstations/:id';

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
