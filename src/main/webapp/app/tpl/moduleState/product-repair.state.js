/**
 * Created by Administrator on 2017/10/24/024.
 */
(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];
    function stateConfig($stateProvider) {
        $stateProvider
            .state('app.main.productRepair', {
                url: '/product-repair',
                templateUrl: 'app/tpl/product-repair/product-repairs.html',
                controller: 'ProductRepairController',
                controllerAs: 'vm',
                permission:'productRepair',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('productRepair').addPart('global');
                        return $translate.refresh();
                    }],
                    deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/product-repair/product-repair.controller.js',
                            'app/tpl/product-repair/product-repair.service.js','app/services/table.service.js']);
                    }]
                }
            });
    }
})();

