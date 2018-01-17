(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('app.main.operation', {
                url: '/operation',
                templateUrl: 'app/tpl/operation/operations.html',
                controller: 'OperationController',
                controllerAs: 'vm',
                permission:"operation",
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('operation').addPart('global');
                        return $translate.refresh();
                    }],
                    deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                            return $ocLazyLoad.load(['app/tpl/operation/operation.controller.js',
                                'app/tpl/operation/operation.service.js','app/services/table.service.js']);
                        }]
                }
            }).state('app.main.operation.new', {
                url: '/new',
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/tpl/operation/operation-dialog.html',
                        controller: 'OperationDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    id: null,
                                    operationName: null,
                                    seqNo: null
                                };
                            },
                            deps: ['$ocLazyLoad',
                                function( $ocLazyLoad ){
                                    return $ocLazyLoad.load(['app/tpl/operation/operation-dialog.controller.js']);
                                }]
                        }
                    }).result.then(function() {
                        $state.go('app.main.operation', null, { reload: 'app.main.operation' });
                    }, function() {
                        $state.go('app.main.operation');
                    });
                }]
            })
            .state('app.main.operation.edit', {
                url: '/{id}/edit',
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/tpl/operation/operation-dialog.html',
                        controller: 'OperationDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Operation', function(Operation) {
                                return Operation.get({id : $stateParams.id}).$promise;
                            }],
                            deps: ['$ocLazyLoad',
                                function( $ocLazyLoad ){
                                    return $ocLazyLoad.load(['app/tpl/operation/operation-dialog.controller.js']);
                                }]
                        }
                    }).result.then(function() {
                        $state.go('app.main.operation', null, { reload: 'app.main.operation' });
                    }, function() {
                        $state.go('^');
                    });
                }]
            })
            .state('app.main.operation.delete', {
                url: '/{id}/delete',
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/tpl/operation/operation-delete-dialog.html',
                        controller: 'OperationDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['Operation', function(Operation) {
                                return Operation.get({id : $stateParams.id}).$promise;
                            }],
                            deps: ['$ocLazyLoad',
                                function( $ocLazyLoad ){
                                    return $ocLazyLoad.load(['app/tpl/operation/operation-delete-dialog.controller.js']);
                                }]
                        }
                    }).result.then(function() {
                        $state.go('app.main.operation', null, { reload: 'app.main.operation' });
                    }, function() {
                        $state.go('^');
                    });
                }]
            });
    }

})();
