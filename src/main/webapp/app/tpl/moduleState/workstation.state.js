/**
 * Created by xiaosui on 2017/6/8.
 * 注册workstation相关模块
 */
(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.workstation', {
            url: '/workstation',
            templateUrl: 'app/tpl/workstation/workstations.html',
            controller: 'WorkstationController',
            controllerAs: 'vm',
            permission: 'workstation',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('workstation').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/workstation/workstation.controller.js',
                            'app/tpl/workstation/workstation.service.js','app/services/table.service.js']);
                    }]
            }
        })
        .state('app.main.workstation.new', {
            url: '/new',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/workstation/workstation-dialog.html',
                    controller: 'WorkstationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                stationNo: null,
                                stationName: null,
                                desc: null,
                                id: null
                            };
                        },
                        deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/workstation/workstation-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.workstation', null, { reload: 'app.main.workstation' });
                }, function() {
                    $state.go('app.main.workstation');
                });
            }]
        })
        .state('app.main.workstation.edit', {
            url: '/{id}/edit',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/workstation/workstation-dialog.html',
                    controller: 'WorkstationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Workstation', function(Workstation) {
                            return Workstation.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/workstation/workstation-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.workstation', null, { reload: 'app.main.workstation' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.workstation.delete', {
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/workstation/workstation-delete-dialog.html',
                    controller: 'WorkstationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Workstation', function(Workstation) {
                            return Workstation.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/workstation/workstation-delete-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.workstation', null, { reload: 'app.main.workstation' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }
})();
