(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('app.main.operator-data', {
            url: '/operator-data?',
            data: {
                pageTitle: 'bekoproApp.operatorData.home.title'
            },
            templateUrl: 'app/tpl/operator-data/operator-data.html',
            controller: 'OperatorDataController',
            controllerAs: 'vm',
            permission: 'operator-data',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('operatorData');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/operator-data/operator-data.controller.js',
                            'app/tpl/operator-data/operator-data.service.js','app/services/table.service.js']);
                    }]
            }
        })
        .state('operator-data.new', {
            parent: 'operator-data',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/tpl/operator-data/operator-data-dialog.html',
                    controller: 'OperatorDataDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                operator: null,
                                operation: null,
                                operationTime: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('app.main.operator-data', null, { reload: 'app.main.operator-data' });
                }, function() {
                    $state.go('app.main.operator-data');
                });
            }]
        });
    }
})();
