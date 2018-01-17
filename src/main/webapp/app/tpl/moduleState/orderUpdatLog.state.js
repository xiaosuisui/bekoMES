(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];
    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.orderUpdateLog', {
            url: '/orderUpdateLog',
            templateUrl: 'app/tpl/orderUpdateLog/orderUpdateLog.html',
            controller: 'orderUpdateLogController',
            controllerAs: 'vm',
            permission: 'orderUpdateLog',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('orderUpdateLog').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/orderUpdateLog/orderUpdateLog.controller.js',
                            'app/tpl/orderUpdateLog/orderUpdateLog.service.js','app/services/table.service.js']);
                    }]
            }
        });
    }
})();
