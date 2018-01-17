(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('ProductCode', ProductCode);

    ProductCode.$inject = ['$resource'];

    function ProductCode ($resource) {
        var resourceUrl =  'api/product-codes/:id';

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
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);;
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
