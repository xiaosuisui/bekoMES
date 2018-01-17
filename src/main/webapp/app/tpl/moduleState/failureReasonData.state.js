(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];
    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.failureReasonData', {
            url: '/failureReasonData',
            templateUrl: 'app/tpl/failureReasonData/failureReasonData.html',
            controller: 'failureReasonDataController',
            controllerAs: 'vm',
            permission: 'failureReasonData',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('failureReasonData').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/failureReasonData/failureReasonData.controller.js',
                            'app/tpl/failureReasonData/failureReasonData.service.js','app/services/table.service.js']);
                    }]
            }
        });
    }
})();
