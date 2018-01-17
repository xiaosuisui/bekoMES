(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.failureReason', {
            url: '/failureReason?',
            templateUrl: 'app/tpl/failureReason/failureReason.html',
            controller: 'failureReasonController',
            controllerAs: 'vm',
            permission:"failureReason",
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('failureReason');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/failureReason/failureReason.controller.js',
                            'app/tpl/failureReason/failureReason.service.js','app/services/table.service.js']);
                    }]
            }
        })
        .state('app.main.failureReason.new', {
            url: '/new',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/failureReason/failureReason-dialog.html',
                    controller: 'failureReasonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                workstation: null,
                                reason: null,
                                type: null,
                                description:null
                            };
                        },
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/failureReason/failureReason-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.failureReason', null, { reload: 'app.main.failureReason' });
                }, function() {
                    $state.go('app.main.failureReason');
                });
            }]
        })
        .state('app.main.failureReason.edit', {
            url: '/{id}/edit',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/failureReason/failureReason-dialog.html',
                    controller: 'failureReasonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['failureReason', function(failureReason) {
                            return failureReason.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/failureReason/failureReason-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.failureReason', null, { reload: 'app.main.failureReason' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.failureReason.delete', {
            url: '/{id}/delete',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/failureReason/failureReason-delete-dialog.html',
                    controller: 'failureReasonDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['failureReason', function(failureReason) {
                            return failureReason.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/failureReason/failureReason-delete-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.failureReason', null, { reload: 'app.main.failureReason' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
