/**
 * Created by xiaosui on 2017/6/26.
 */
angular
    .module('bekoproApp')
    .config(orderStateConfig);

orderStateConfig.$inject=['$stateProvider'];
function orderStateConfig($stateProvider) {
    $stateProvider
        .state('app.main.myAccount', {
        url: '/myAccount',
        templateUrl: 'app/tpl/account/myaccount.html',
        controller: 'MyAccountController',
        controllerAs: 'vm',
        resolve: {
            deps: ['$ocLazyLoad',
                function( $ocLazyLoad ){
                    return $ocLazyLoad.load(['app/tpl/account/myaccount.controller.js']);
                }]
        }
    });
}
