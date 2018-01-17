(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('ProductResourceFile', ProductResourceFile);

    ProductResourceFile.$inject = ['$resource'];

    function ProductResourceFile ($resource) {
        var resourceUrl =  'api/productResourceFiles/:id';

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
