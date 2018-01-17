(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .config(stateConfig);
    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.product', {
            url: '/product',
            templateUrl: 'app/tpl/product/products.html',
            controller: 'ProductController',
            controllerAs: 'vm',
            permission:"product",
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('product').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/product/product.controller.js',
                            'app/tpl/product/product.service.js','app/services/table.service.js']);
                    }]
            }
        })
        .state('app.main.product.new', {
            url: '/new',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/product/product-dialog.html',
                    controller: 'ProductDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                productNo: null,
                                productName: null,
                                type: null,
                                qrcode: null,
                                id: null
                            };
                        },
                        deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/product/product-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.product', null, { reload: 'app.main.product' });
                }, function() {
                    $state.go('app.main.product');
                });
            }]
        })
        .state('app.main.product.edit', {
            url: '/{id}/edit',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/product/product-dialog.html',
                    controller: 'ProductDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Product', function(Product) {
                            return Product.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/product/product-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.product', null, { reload: 'app.main.product' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.product.delete', {
            url: '/{id}/delete',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/product/product-delete-dialog.html',
                    controller: 'ProductDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Product', function(Product) {
                            return Product.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/product/product-delete-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.product', null, { reload: 'app.main.product' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }
})();
