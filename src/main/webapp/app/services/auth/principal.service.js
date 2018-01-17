(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .factory('Principal', Principal);

    Principal.$inject = ['$q', 'Account'];

    function Principal ($q, Account) {
        var _identity,
            _authenticated = false;

        var service = {
            authenticate: authenticate,
            identity: identity,
            isAuthenticated: isAuthenticated,
            isIdentityResolved: isIdentityResolved
        };

        return service;

        function authenticate (identity) {
           /* console.log("console failure callback");*/
            _identity = identity;
            _authenticated = identity !== null;
        }

        function identity (force) {
            console.log("second Step")
            var deferred = $q.defer();

            if (force === true) {
                _identity = undefined;
            }

            // check and see if we have retrieved the identity data from the server.
            // if we have, reuse it by immediately resolving
            if (angular.isDefined(_identity)) {
                deferred.resolve(_identity);

                return deferred.promise;
            }

            // retrieve the identity data from the server, update the identity object, and then resolve.
            Account.get().$promise
                .then(getAccountThen)
                .catch(getAccountCatch);

            return deferred.promise;

            function getAccountThen (account) {
                if(account.data==="" || account.data.login==="anonymoususer"){
                    _authenticated=false;
                    _identity=false;
                    deferred.resolve(_identity);
                }else{
                    _identity = account.data;
                    _authenticated = true;
                    deferred.resolve(_identity);
                }
            }
            function getAccountCatch () {
                _identity = null;
                _authenticated = false;
                deferred.resolve(_identity);
            }
        }

        function isAuthenticated () {
            return _authenticated;
        }

        function isIdentityResolved () {
            return angular.isDefined(_identity);
        }
    }
})();
