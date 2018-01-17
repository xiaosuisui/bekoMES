(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.cycleTimeTarget', {
            url: '/cycleTimeTarget?',
            templateUrl: 'app/tpl/cycleTimeTarget/cycleTimeTarget.html',
            controller: 'cycleTimeTargetController',
            controllerAs: 'vm',
            permission:"cycleTimeTarget",
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cycleTimeTarget');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/cycleTimeTarget/cycleTimeTarget.controller.js',
                            'app/tpl/cycleTimeTarget/cycleTimeTarget.service.js','app/services/table.service.js']);
                    }]
            }
        })
        .state('app.main.cycleTimeTarget.new', {
            url: '/new',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/cycleTimeTarget/cycleTimeTarget-dialog.html',
                    controller: 'cycleTimeTargetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                productNo: null,
                                lineId: null,
                                id: null,
                                target:null,
                                updateTime:null
                            };
                        },
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/cycleTimeTarget/cycleTimeTarget-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.cycleTimeTarget', null, { reload: 'app.main.cycleTimeTarget' });
                }, function() {
                    $state.go('app.main.cycleTimeTarget');
                });
            }]
        })
        .state('app.main.cycleTimeTarget.edit', {
            url: '/{id}/edit',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/cycleTimeTarget/cycleTimeTarget-dialog.html',
                    controller: 'cycleTimeTargetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['cycleTimeTarget', function(cycleTimeTarget) {
                            return cycleTimeTarget.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/cycleTimeTarget/cycleTimeTarget-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.cycleTimeTarget', null, { reload: 'app.main.cycleTimeTarget' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.cycleTimeTarget.delete', {
            url: '/{id}/delete',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/cycleTimeTarget/cycleTimeTarget-delete-dialog.html',
                    controller: 'cycleTimeTargetDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['cycleTimeTarget', function(cycleTimeTarget) {
                            return cycleTimeTarget.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/cycleTimeTarget/cycleTimeTarget-delete-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.cycleTimeTarget', null, { reload: 'app.main.cycleTimeTarget' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
