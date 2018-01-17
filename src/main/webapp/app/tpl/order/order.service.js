(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('Order', Order);

    Order.$inject = ['$resource'];

    function Order ($resource) {
        var resourceUrl =  'api/orders/:id';

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
            'update': { method:'PUT' },
        });
    }
})();
