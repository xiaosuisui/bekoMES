/**
 * Created by xiaosui on 2017/6/8.
 * 注册订单模块相关的路径跳转
 */
angular
    .module('bekoproApp')
    .config(orderStateConfig);

orderStateConfig.$inject=['$stateProvider'];
function orderStateConfig($stateProvider) {
    $stateProvider
        .state('app.main.orders', {
            url: '/orders',
            templateUrl: 'app/tpl/order/orders.html',
            controller: 'OrderController',
            controllerAs: 'vm',
            permission:"/orders",
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('order').addPart('global');
                    return $translate.refresh();
                }],
                deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/order/order.controller.js',
                            'app/tpl/order/order.service.js','app/services/table.service.js']);
                    }]
            }
        }).state('app.main.orders.new', {
            url: '/new',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                 $uibModal.open({
                   templateUrl: 'app/tpl/order/order-dialog.html',
                   controller: 'OrderDialogController',
                   controllerAs: 'vm',
                   backdrop: 'static',
                   size: 'lg',
                   resolve: {
                    entity: function () {
                        return {
                            orderNo: null,
                            id: null
                        };
                    },
                    deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                            return $ocLazyLoad.load(['app/tpl/order/order-dialog.controller.js']);
                        }]
                }
            }).result.then(function() {
                $state.go('app.main.orders', null, { reload: 'app.main.orders' });
            }, function() {
                $state.go('app.main.orders');
            });
        }]
    }) .state('app.main.orders.delete', {
        url: '/{id}/delete',
        onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
            $uibModal.open({
                templateUrl: 'app/tpl/order/order-delete-dialog.html',
                controller: 'OrderDeleteController',
                controllerAs: 'vm',
                size: 'md',
                resolve: {
                    entity: ['Order', function(Order) {
                        return Order.get({id : $stateParams.id}).$promise;
                    }],
                    deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                            return $ocLazyLoad.load(['app/tpl/order/order-delete-dialog.controller.js']);
                        }]

                }
            }).result.then(function() {
                $state.go('app.main.orders', null, { reload: 'app.main.orders' });
            }, function() {
                $state.go('^');
            });
        }]
    }) .state('app.main.orders.edit', {
        url: '/{id}/edit',
        onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
            $uibModal.open({
                templateUrl: 'app/tpl/order/order-dialog.html',
                controller: 'OrderDialogController',
                controllerAs: 'vm',
                backdrop: 'static',
                size: 'lg',
                resolve: {
                    entity: ['Order', function(Order) {
                        return Order.get({id : $stateParams.id}).$promise;
                    }],
                    deps: ['$ocLazyLoad',
                        function( $ocLazyLoad ){
                            return $ocLazyLoad.load(['app/tpl/order/order-dialog.controller.js']);
                        }]
                }
            }).result.then(function() {
                $state.go('app.main.orders', null, { reload: 'app.main.orders' });
            }, function() {
                $state.go('^');
            });
        }]
    })
    .state('app.main.orderProduction', {
        url: '/orderProduction?',
        templateUrl: 'app/tpl/order/orders.html',
        controller: 'OrderController',
        controllerAs: 'vm',
        permission:'orderProduction',
        resolve: {
            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('order').addPart('global');
                return $translate.refresh();
            }],
            deps: ['$ocLazyLoad', function( $ocLazyLoad ){
                return $ocLazyLoad.load(['app/tpl/order/order.controller.js',
                    'app/tpl/order/order.service.js','app/services/table.service.js']);
            }]
        }
    });
}
