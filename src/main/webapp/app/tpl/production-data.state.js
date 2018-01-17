(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.production-data', {
            url: '/production-data',
            data: {
                pageTitle: 'bekoproApp.productionData.home.title'
            },
            templateUrl: 'app/tpl/production-data/production-data.html',
            controller: 'ProductionDataController',
            controllerAs: 'vm',
            permission:'production-data',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('productionData');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/production-data/production-data.controller.js',
                            'app/tpl/production-data/production-data.service.js','app/services/table.service.js']);
                    }]
            }
        })
    }
})();
