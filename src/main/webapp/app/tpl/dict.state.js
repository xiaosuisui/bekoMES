(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.dict', {
            url: '/dict',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'bekoproApp.dict.home.title'
            },
            templateUrl: 'app/tpl/dict/dicts.html',
            controller: 'DictController',
            controllerAs: 'vm',
            permission:'url7',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('dict');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/dict/dict.controller.js',
                            'app/tpl/dict/dict.service.js','app/services/net.data.service.js',
                        'app/tpl/dict-item/dict-item.service.js']);
                    }]
            }
        })
        .state('app.main.dict.new', {
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/dict/dict-dialog.html',
                    controller: 'DictDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                dictNo: null,
                                dictName: null,
                                id: null
                            };
                        },
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad){
                                return $ocLazyLoad.load(['app/tpl/dict/dict-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.dict', null, { reload: 'app.main.dict' });
                }, function() {
                    $state.go('app.main.dict');
                });
            }]
        })
        .state('app.main.dict.edit', {
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/dict/dict-dialog.html',
                    controller: 'DictDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Dict', function(Dict) {
                            return Dict.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad){
                                return $ocLazyLoad.load(['app/tpl/dict/dict-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.dict', null, { reload: 'app.main.dict' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.dict.delete', {
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/dict/dict-delete-dialog.html',
                    controller: 'DictDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Dict', function(Dict) {
                            return Dict.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad){
                                return $ocLazyLoad.load(['app/tpl/dict/dict-delete-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.dict', null, { reload: 'app.main.dict' });
                }, function() {
                    $state.go('^');
                });
            }]
        }) .state('app.main.dict.newItem', {
            url: '{id}/newItem',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/dict-item/dict-item-dialog.html',
                    controller: 'DictItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                value: null,
                                value1: null,
                                id: null
                            };
                        },
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad){
                                return $ocLazyLoad.load(['app/tpl/dict-item/dict-item-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.dict', null, { reload: 'app.main.dict' });
                }, function() {
                    $state.go('app.main.dict');
                });
            }]
        }) .state('app.main.dict.editItem', {
            url: '/{id}/editItem',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/dict-item/dict-item-dialog.html',
                    controller: 'DictItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DictItem', function(DictItem) {
                            return DictItem.get({id : $stateParams.id}).$promise;
                        }]
                    },
                    deps: ['$ocLazyLoad',
                        function( $ocLazyLoad){
                            return $ocLazyLoad.load(['app/tpl/dict-item/dict-item-dialog.controller.js']);
                        }]
                }).result.then(function() {
                    $state.go('app.main.dict', null, { reload: 'app.main.dict' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
