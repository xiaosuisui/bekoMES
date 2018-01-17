(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('tvDataConfig', tvDataConfig);

    tvDataConfig.$inject = ['$resource'];

    function tvDataConfig ($resource) {
        var resourceUrl =  'api/tvDataConfig/:id';

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
