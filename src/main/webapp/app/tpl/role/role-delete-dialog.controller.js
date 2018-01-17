(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('RoleDeleteController',RoleDeleteController);

    RoleDeleteController.$inject = ['$uibModalInstance', 'entity', 'Role', '$http'];

    function RoleDeleteController($uibModalInstance, entity, Role, $http) {
        var vm = this;

        vm.role = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        //用来检测用户是否绑定准备的角色
        vm.roleBindUser = false;
        checkRoleHasBindUser();

        //根据角色查询是否绑定用户
        function checkRoleHasBindUser() {
            $http.get('/api/checkRoleHasBindUser/' + entity.id).success(function (data) {
                vm.roleBindUser = data;
            });
        }

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Role.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
