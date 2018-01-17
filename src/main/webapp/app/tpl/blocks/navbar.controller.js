(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$state', 'Auth', 'Principal', '$rootScope', '$interval'];

    function NavbarController ($state, Auth, Principal, $rootScope, $interval) {
        var vm = this;
        /*显示系统时间*/
        vm.clock = {
            now : new Date().toLocaleString()
        };
        var updateClock = function(){
            vm.clock.now = new Date().toLocaleString();
        };
        $interval(updateClock, 1000);
        vm.isNavbarCollapsed = true;
        vm.isAuthenticated = null;
        vm.logout = logout;
        vm.$state = $state;

        /*监听登录成功*/
        $rootScope.$on("authenticationSuccess", function (data) {
            $rootScope.showMessage();
            getAccount();

        });

        getAccount();
        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                if(vm.account.imageUrl==""||vm.account.imageUrl==null){
                    vm.account.imageUrl="a0.jpg";
                }
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }

        function logout() {
            Auth.logout();
            $state.go('app.signin');
        }
    }
})();
