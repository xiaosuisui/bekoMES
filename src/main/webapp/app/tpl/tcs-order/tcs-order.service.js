(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('TcsOrder', TcsOrder);

    TcsOrder.$inject = ['$resource'];

    function TcsOrder ($resource, DateUtils) {
        var resourceUrl =  'api/tcs-orders/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.startTime = DateUtils.convertLocalDateFromServer(data.startTime);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.startTime = DateUtils.convertLocalDateToServer(copy.startTime);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.startTime = DateUtils.convertLocalDateToServer(copy.startTime);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
