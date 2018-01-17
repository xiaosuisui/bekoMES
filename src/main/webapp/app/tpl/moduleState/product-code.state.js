(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];
    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.productCcode', {
            url: '/product-code',
            templateUrl: 'app/tpl/product-code/product-codes.html',
            controller: 'ProductCodeController',
            controllerAs: 'vm',
            permission:'productCode',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('productCode').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/product-code/product-code.controller.js',
                            'app/tpl/product-code/product-code.service.js','app/services/table.service.js']);
                    }]
            }
        });
    }
})();
