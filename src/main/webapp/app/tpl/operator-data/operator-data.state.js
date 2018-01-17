(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('operator-data', {
            parent: 'entity',
            url: '/operator-data?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'bekoproApp.operatorData.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/operator-data/operator-data.html',
                    controller: 'OperatorDataController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('operatorData');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('operator-data-detail', {
            parent: 'operator-data',
            url: '/operator-data/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'bekoproApp.operatorData.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/operator-data/operator-data-detail.html',
                    controller: 'OperatorDataDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('operatorData');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'OperatorData', function($stateParams, OperatorData) {
                    return OperatorData.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'operator-data',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('operator-data-detail.edit', {
            parent: 'operator-data-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/operator-data/operator-data-dialog.html',
                    controller: 'OperatorDataDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['OperatorData', function(OperatorData) {
                            return OperatorData.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('operator-data.new', {
            parent: 'operator-data',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/operator-data/operator-data-dialog.html',
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
                    $state.go('operator-data', null, { reload: 'operator-data' });
                }, function() {
                    $state.go('operator-data');
                });
            }]
        })
        .state('operator-data.edit', {
            parent: 'operator-data',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/operator-data/operator-data-dialog.html',
                    controller: 'OperatorDataDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['OperatorData', function(OperatorData) {
                            return OperatorData.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('operator-data', null, { reload: 'operator-data' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('operator-data.delete', {
            parent: 'operator-data',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/operator-data/operator-data-delete-dialog.html',
                    controller: 'OperatorDataDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['OperatorData', function(OperatorData) {
                            return OperatorData.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('operator-data', null, { reload: 'operator-data' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
