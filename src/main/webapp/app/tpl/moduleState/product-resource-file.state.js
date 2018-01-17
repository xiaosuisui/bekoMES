(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .config(stateConfig);
    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.productResourceFile', {
            url: '/product-resource-file',
            templateUrl: 'app/tpl/product-resource-file/product-resource-files.html',
            controller: 'ProductResourceFileController',
            controllerAs: 'vm',
            permission: 'productResourceFile',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('productResourceFile').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/product-resource-file/product-resource-file.controller.js',
                            'app/tpl/product-resource-file/product-resource-file.service.js','app/services/table.service.js']);
                    }]
            }
        })
        .state('app.main.productResourceFile.new', {
            url: '/new',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/product-resource-file/product-resource-file-dialog.html',
                    controller: 'ProductResourceFileDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                productNo: null,
                                workstationId: null,
                                type: null,
                                storageLocation: null,
                                id: null
                            };
                        },
                        deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/product-resource-file/product-resource-file-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.productResourceFile', null, { reload: 'app.main.productResourceFile' });
                }, function() {
                    $state.go('app.main.productResourceFile');
                });
            }]
        })
        .state('app.main.productResourceFile.edit', {
            url: '/{id}/edit',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/product-resource-file/product-resource-file-dialog.html',
                    controller: 'ProductResourceFileDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProductResourceFile', function(ProductResourceFile) {
                            return ProductResourceFile.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/product-resource-file/product-resource-file-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.productResourceFile', null, { reload: 'app.main.productResourceFile' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.productResourceFile.delete', {
            url: '/{id}/delete',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/product-resource-file/product-resource-file-delete-dialog.html',
                    controller: 'ProductResourceFileDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProductResourceFile', function(ProductResourceFile) {
                            return ProductResourceFile.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/product-resource-file/product-resource-file-delete-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.productResourceFile', null, { reload: 'app.main.productResourceFile' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }
})();
