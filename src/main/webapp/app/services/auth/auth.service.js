(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .factory('Auth', Auth);

    Auth.$inject = ['$rootScope', '$state', '$sessionStorage', '$q', '$translate', 'Principal', 'AuthServerProvider', 'Account',];

    function Auth ($rootScope, $state, $sessionStorage, $q, $translate, Principal, AuthServerProvider, Account) {
        var service = {
            authorize: authorize,
            getPreviousState: getPreviousState,
            login: login,
            logout: logout,
            resetPreviousState: resetPreviousState,
            storePreviousState: storePreviousState,
            updateAccount: updateAccount
        };

        return service;


        function authorize (force) {
            var authReturn = Principal.identity
            (force).then(authThen);

            return authReturn;

            function authThen () {
                /*重构跳转逻辑*/
                var isAuthenticated = Principal.isAuthenticated();
                /*如果用户为登录界面,访问登录界面的时候跳转到主界面*/
                if(isAuthenticated &&$rootScope.toState.name==='app.signin'){
                    console.log("loginPage---->mainPage");
                    $state.go('app.main.dashboard');
                }
                if(!isAuthenticated && $rootScope.toState.name!=='app.signin'){
                    console.log("go accessPage");
                    $state.go('accessPage');
                }
            }
        }
        function login (credentials, callback) {
            var cb = callback || angular.noop;
            var deferred = $q.defer();

            AuthServerProvider.login(credentials)
                .then(loginThen)
                .catch(function (err) {
                    this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));

            function loginThen (data) {
                Principal.identity(true).then(function(account) {
                    if (account!== null) {
                        $translate.use(account.langKey).then(function () {
                            $translate.refresh();
                        });
                    }
                    deferred.resolve(data);
                });
                return cb();
            }

            return deferred.promise;
        }


        function logout () {
            AuthServerProvider.logout();
            Principal.authenticate(null);
        }


        function updateAccount (account, callback) {
            var cb = callback || angular.noop;

            return Account.save(account,
                function () {
                    return cb(account);
                },
                function (err) {
                    return cb(err);
                }.bind(this)).$promise;
        }

        function getPreviousState() {
            var previousState = $sessionStorage.previousState;
            return previousState;
        }

        function resetPreviousState() {
            delete $sessionStorage.previousState;
        }
        function storePreviousState(previousStateName, previousStateParams) {
            var previousState = { "name": previousStateName, "params": previousStateParams };
            $sessionStorage.previousState = previousState;
        }
    }
})();
