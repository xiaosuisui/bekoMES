(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.tcs-order', {
            url: '/tcsOrder',
            templateUrl: 'app/tpl/tcs-order/tcs-orders.html',
            controller: 'TcsOrderController',
            controllerAs: 'vm',
            permission:'tcs-order',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tcsOrder').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/tcs-order/tcs-order.controller.js',
                            'app/tpl/tcs-order/tcs-order.service.js','app/services/table.service.js']);
                    }]
            }
        });
    }
})();
