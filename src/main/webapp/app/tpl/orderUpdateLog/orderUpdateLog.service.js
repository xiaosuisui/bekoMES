(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('OrderUpdateLog', OrderUpdateLog);

    OrderUpdateLog.$inject = ['$resource'];

    function OrderUpdateLog ($resource) {
        var resourceUrl =  'api/orderUpdateLog/:id';

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
