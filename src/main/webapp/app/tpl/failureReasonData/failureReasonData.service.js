(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('failureReasonData', failureReasonData);

    failureReasonData.$inject = ['$resource'];

    function failureReasonData ($resource) {
        var resourceUrl =  'api/failureReasonData/:id';

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
