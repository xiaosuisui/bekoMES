(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('Pallet', Pallet);

    Pallet.$inject = ['$resource'];

    function Pallet ($resource) {
        var resourceUrl =  'api/pallets/:id';

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
