(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.shift', {
            url: '/shift?',
            templateUrl: 'app/tpl/shift/shift.html',
            controller: 'ShiftController',
            controllerAs: 'vm',
            permission:"shift",
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('shift');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/shift/shift.controller.js',
                            'app/tpl/shift/shift.service.js','app/services/table.service.js']);
                    }]
            }
        })
        .state('app.main.shift.new', {
            url: '/new',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/shift/shift-dialog.html',
                    controller: 'ShiftDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                id: null
                            };
                        },
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/shift/shift-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.shift', null, { reload: 'app.main.shift' });
                }, function() {
                    $state.go('app.main.shift');
                });
            }]
        })
        .state('app.main.shift.edit', {
            url: '/{id}/edit',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/shift/shift-dialog.html',
                    controller: 'ShiftDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Shift', function(Shift) {
                            return Shift.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/shift/shift-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.shift', null, { reload: 'app.main.shift' });
                }, function() {
                    $state.go('app.main.shift');
                });
            }]
        })
        .state('app.main.shift.delete', {
            url: '/{id}/delete',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/shift/shift-delete-dialog.html',
                    controller: 'ShiftDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Shift', function(Shift) {
                            return Shift.get({id : $stateParams.id}).$promise;
                        }],
                        deps: ['$ocLazyLoad',
                            function( $ocLazyLoad ){
                                return $ocLazyLoad.load(['app/tpl/shift/shift-delete-dialog.controller.js']);
                            }]
                    }
                }).result.then(function() {
                    $state.go('app.main.shift', null, { reload: 'app.main.shift' });
                }, function() {
                    $state.go('app.main.shift');
                });
            }]
        });
    }

})();
