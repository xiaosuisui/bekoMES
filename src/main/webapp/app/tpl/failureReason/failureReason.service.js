(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('failureReason', failureReason);

    failureReason.$inject = ['$resource'];

    function failureReason ($resource) {
        var resourceUrl =  'api/failureReason/:id';

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
