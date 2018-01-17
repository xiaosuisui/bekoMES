/**
 * Created by xiaosui on 2017/6/8.
 * 注册用户相关的路由
 */
angular.module('bekoproApp').config(userStateConfig);
userStateConfig.$inject=['$stateProvider'];
function userStateConfig($stateProvider) {
    $stateProvider
        .state('app.main.user',{
            url: '/user',
            templateUrl: 'app/tpl/user/user.html',
            controller: 'userController',
            controllerAs: 'vm',
            permission:"user",
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user').addPart('global');return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad){
                        return $ocLazyLoad.load(['app/tpl/user/user-controller.js','app/tpl/user/user.service.js','app/services/table.service.js']);
                       }]
            }})
        .state('app.main.user.new', {
            url: '/new',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
            $uibModal.open({
                templateUrl: 'app/tpl/user/user-dialog.html',
                controller: 'UserDialogController',
                controllerAs: 'vm',
                backdrop: 'static',
                size: 'lg',
                resolve: {
                    entity: function () {
                        return {
                            id: null, login: null, firstName: null, lastName: null, email: null,
                            activated: true, roleIds:null};
                    },
                    deps: ['$ocLazyLoad',
                        function( $ocLazyLoad){
                            return $ocLazyLoad.load(['app/tpl/user/user-dialog.controller.js',
                            'app/services/net.data.service.js']);
                        }],
                }}).result.then(function() {
                        $state.go('app.main.user', null, { reload: 'app.main.user'});
                    }, function() {$state.go('app.main.user');
                });}]
        })
        .state('app.main.user.delete', {
            url: '/{login}/delete',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                     templateUrl: 'app/tpl/user/user-delete-dialog.html',
                     controller: 'UserDeleteController',
                     controllerAs: 'vm',
                size: 'md',
                resolve: {
                    entity: ['User',function(User) {return User.get({login : $stateParams.login}).$promise;}],
                    deps: ['$ocLazyLoad',
                        function( $ocLazyLoad ){return $ocLazyLoad.load(['app/tpl/user/user-delete-dialog.controller.js']);}]
                }}).result.then(function() {
                         $state.go('app.main.user', null, { reload: 'app.main.user' });}, function() {$state.go('^');});
            }]
        })
        .state('app.main.user.edit', {
            url: '/{login}/edit',
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
            $uibModal.open({
                templateUrl: 'app/tpl/user/user-dialog.html',
                controller: 'UserDialogController',
                controllerAs: 'vm',
                backdrop: 'static',
                size: 'lg',
                resolve: {
                    entity: ['User', function(User) {
                        return User.get({login : $stateParams.login}).$promise;
                    }],
                    deps: ['$ocLazyLoad',
                        function( $ocLazyLoad ){
                            return $ocLazyLoad.load(['app/tpl/user/user-dialog.controller.js','app/services/net.data.service.js']);
                        }]
                }}).result.then(function() {
                      $state.go('app.main.user', null, { reload: 'app.main.user' });
                  },
                function() {$state.go('^');});
            }]
        })
        .state('app.main.myAccount', {
            url: '/myAccount',
            templateUrl: 'app/tpl/user/myaccount.html',
            controller: 'MyAccountController',
            controllerAs: 'vm',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user').addPart('global');return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/user/myaccount.controller.js']);
                    }]
            }
        })
        .state('app.main.password', {
            url: '/changePassword',
            templateUrl: 'app/tpl/user/password.html',
            controller: 'PasswordController',
            controllerAs: 'vm',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user').addPart('global');return $translate.refresh();
                }],
                deps: ['$ocLazyLoad',
                    function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['app/tpl/user/password.controller.js']);
                    }]
            }
        });
}
