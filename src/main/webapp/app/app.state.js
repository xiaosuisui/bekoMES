angular.module('bekoproApp')
    .run(
        ['$rootScope', '$state', '$stateParams',
            function ($rootScope,   $state,   $stateParams) {
                $rootScope.$state = $state;
                $rootScope.$stateParams = $stateParams;
            }
        ]
    ).config(['$stateProvider', '$urlRouterProvider', function ($stateProvider,  $urlRouterProvider){
    $urlRouterProvider
        .otherwise('/app/signin');
    $stateProvider
        .state('app',{
            abstract:true,
            url:'/app',
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('global');
                }]
            }
        }).state('app.signin',{
        url:'/signin',
        data:{
            authorities: []
        },
        views:{
            'signin@':{
                templateUrl: 'app/tpl/login/login.html',
                controller: 'AppCtrl',
                controllerAs: 'vm'
            }
        },
        resolve: {
            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                $translatePartialLoader.addPart('login');
                return $translate.refresh();
            }],
            deps: ['$ocLazyLoad',
                function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['app/tpl/login/login.controller.js','app/services/net.data.service.js']);
                }]
        }
    }).state('app.main', {
        url: '/main',
        data: {
            authorities: []
        },
        views: {
            'main@': {
                templateUrl: 'app/tpl/blocks/app.html',
                controller: 'NavbarController',
                controllerAs: 'vm'
            }
        },
        resolve: {
            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                $translatePartialLoader.addPart('main');
                return $translate.refresh();
            }]
        }
    }).state('app.main.dashboard', {
        url: '/dashboard',
        templateUrl: 'app/tpl/dashboard/app_dashboard_v1.html'
    }).state('accessPage',{
        url:'/accessPage',
        views:{
            'signin@':{
                templateUrl: 'app/tpl/login/access.html'
            }
        }
    });
}]);
