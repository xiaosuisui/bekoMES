/**
 * Created by xiaosui on 2017/6/29.
 */
(function() {
    'use ustrict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.role', {
            url: '/role',
            templateUrl: 'app/tpl/role/roles.html',
            controller: 'RoleController',
            controllerAs: 'vm',
            permission:'role',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('role').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/role/role.controller.js',
                            'app/tpl/role/role.service.js',,'app/services/net.data.service.js'
                        ,'app/services/table.service.js']);
                    }]
            }
        })
        .state('role-detail', {
            parent: 'role',
            url: '/role/{id}',
            views: {
                'content@': {
                    templateUrl: 'app/entities/role/role-detail.html',
                    controller: 'RoleDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('role');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Role', function($stateParams, Role) {
                    return Role.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'role',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('role-detail.edit', {
            parent: 'role-detail',
            url: '/detail/edit',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/role/role-dialog.html',
                    controller: 'RoleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Role', function(Role) {
                            return Role.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.role.new', {
            url: '/new',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/role/role-dialog.html',
                    controller: 'RoleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        },
                        deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/role/role-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.role', null, { reload: 'app.main.role' });
                }, function() {
                    $state.go('app.main.role');
                });
            }]
        })
        .state('app.main.role.edit', {
            url: '/{id}/edit',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/role/role-dialog.html',
                    controller: 'RoleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Role', function(Role) {
                            return Role.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/role/role-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.role', null, { reload: 'app.main.role' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('app.main.role.delete', {
            url: '/{id}/delete',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/role/role-delete-dialog.html',
                    controller: 'RoleDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Role', function(Role) {
                            return Role.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/role/role-delete-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.role', null, { reload: 'app.main.role' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }
})();
