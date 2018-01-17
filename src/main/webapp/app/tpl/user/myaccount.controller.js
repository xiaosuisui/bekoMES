(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('MyAccountController', SettingsController);

    SettingsController.$inject = ['Principal', 'Auth'];

    function SettingsController (Principal, Auth) {
        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.user = null;
        vm.success = null;

        /**
         * Store the "settings account" in a separate variable, and not in the shared "account" variable.
         */
        var copyAccount = function (account) {
            return {
                activated: account.activated,
                email: account.email,
                firstName: account.firstName,
                langKey: account.langKey,
                lastName: account.lastName,
                login: account.login
            };
        };

        Principal.identity().then(function(account) {
            vm.user = copyAccount(account);
        });

        function save () {
            Auth.updateAccount(vm.user).then(function() {
                vm.error = null;
                vm.success = 'OK';
                Principal.identity(true).then(function(account) {
                    vm.user = copyAccount(account);
                });
            }).catch(function() {
                vm.success = null;
                vm.error = 'ERROR';
            });
        }
    }
})();
