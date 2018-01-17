(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.tvDataConfig', {
            url: '/tvDataConfig?',
            templateUrl: 'app/tpl/tvDataConfig/tvDataConfig.html',
            controller: 'tvDataConfigController',
            controllerAs: 'vm',
            permission:"tvDataConfig",
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tvDataConfig');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/tvDataConfig/tvDataConfig.controller.js',
                            'app/tpl/tvDataConfig/tvDataConfig.service.js','app/services/table.service.js']);
                    }]
            }
        })
        .state('app.main.tvDataConfig.new', {
            url: '/new',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/tvDataConfig/tvDataConfig-dialog.html',
                    controller: 'tvDataConfigDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                tvName: null,
                                pageName: null,
                                isShow: null,
                                id:null,
                            };
                        },
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/tvDataConfig/tvDataConfig-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.tvDataConfig', null, { reload: 'app.main.tvDataConfig' });
                }, function() {
                    $state.go('app.main.tvDataConfig');
                });
            }]
        })
        .state('app.main.tvDataConfig.edit', {
            url: '/{id}/edit',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/tvDataConfig/tvDataConfig-dialog.html',
                    controller: 'tvDataConfigDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['tvDataConfig', function(tvDataConfig) {
                            return tvDataConfig.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/tvDataConfig/tvDataConfig-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.tvDataConfig', null, { reload: 'app.main.tvDataConfig' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.tvDataConfig.delete', {
            url: '/{id}/delete',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/tvDataConfig/tvDataConfig-delete-dialog.html',
                    controller: 'tvDataConfigDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['tvDataConfig', function(tvDataConfig) {
                            return tvDataConfig.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/tvDataConfig/tvDataConfig-delete-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.tvDataConfig', null, { reload: 'app.main.tvDataConfig' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
