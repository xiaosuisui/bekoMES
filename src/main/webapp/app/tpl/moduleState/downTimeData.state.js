(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];
    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.downTimeData', {
            url: '/downTimeData',
            templateUrl: 'app/tpl/downTimeData/downTimeData.html',
            controller: 'downTimeDataController',
            controllerAs: 'vm',
            permission: 'downTimeData',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('downTimeData').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/downTimeData/downTimeData.controller.js',
                            'app/tpl/downTimeData/downTimeData.service.js','app/services/table.service.js']);
                    }]
            }
        });
    }
})();
