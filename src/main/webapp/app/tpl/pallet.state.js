(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.pallet', {
            url: '/pallet?',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'bekoproApp.pallet.home.title'
            },
            templateUrl: 'app/tpl/pallet/pallets.html',
            controller: 'PalletController',
            controllerAs: 'vm',
            permission:"pallets",
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('pallet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/pallet/pallet.controller.js',
                            'app/tpl/pallet/pallet.service.js','app/services/table.service.js']);
                    }]
            }
        })
        .state('pallet-detail', {
            parent: 'pallet',
            url: '/pallet/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'bekoproApp.pallet.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/pallet/pallet-detail.html',
                    controller: 'PalletDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('pallet');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Pallet', function($stateParams, Pallet) {
                    return Pallet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'pallet',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('pallet-detail.edit', {
            parent: 'pallet-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/pallet/pallet-dialog.html',
                    controller: 'PalletDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Pallet', function(Pallet) {
                            return Pallet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.pallet.new', {
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/pallet/pallet-dialog.html',
                    controller: 'PalletDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                palletName: null,
                                palletNo: null,
                                id: null
                            };
                        },
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/pallet/pallet-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.pallet', null, { reload: 'app.main.pallet' });
                }, function() {
                    $state.go('app.main.pallet');
                });
            }]
        })
        .state('app.main.pallet.edit', {
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/pallet/pallet-dialog.html',
                    controller: 'PalletDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Pallet', function(Pallet) {
                            return Pallet.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/pallet/pallet-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.pallet', null, { reload: 'app.main.pallet' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.pallet.delete', {
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/pallet/pallet-delete-dialog.html',
                    controller: 'PalletDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Pallet', function(Pallet) {
                            return Pallet.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/pallet/pallet-delete-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.pallet', null, { reload: 'app.main.pallet' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
