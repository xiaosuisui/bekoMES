/**
 * Created by Administrator on 2017/10/24/024.
 */
(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('ProductRepair', ProductRepair);

    ProductRepair.$inject = ['$resource'];

    function ProductRepair ($resource) {
        var resourceUrl =  'api/product-repair/:id';

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
