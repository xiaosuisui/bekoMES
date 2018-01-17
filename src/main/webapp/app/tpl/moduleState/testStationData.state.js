(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];
    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.testStationData', {
            url: '/testStationData',
            templateUrl: 'app/tpl/testStationData/testStationData.html',
            controller: 'testStationController',
            controllerAs: 'vm',
            permission: 'testStationData',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('testStationData').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/testStationData/testStation.controller.js',
                            'app/tpl/testStationData/testStation.service.js','app/services/table.service.js']);
                    }]
            }
        });
    }
})();
