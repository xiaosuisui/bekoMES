(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('Operation', Operation);

    Operation.$inject = ['$resource'];

    function Operation ($resource) {
        var resourceUrl =  'api/operations/:id';

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
