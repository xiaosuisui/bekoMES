(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('RoleDialogController', RoleDialogController);

    RoleDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Role','NetData','$window'];

    function RoleDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Role,NetData,$window) {
        var vm = this;

        vm.role = entity;
        vm.clear = clear;
        vm.save = save;
        //通过roleId查出对应的节点,并checked
        function getCheckedNodeByRoleId(roleId) {
           NetData.get("/api/getCheckMenuUrlByRoleId?roleId="+roleId).then(function (data) {
               var ref = $('#tree').jstree(true);
              $.each(data,function (i,e) {
                  ref.check_node(e);
              });
           });
        }
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
            if (vm.role.id!=null){
                getCheckedNodeByRoleId(vm.role.id);

            }
        });

        function clear (){
            $uibModalInstance.dismiss('cancel');
        }
        function save() {
            if($scope.editForm.$valid){
                var url = "api/saveRoleMenus";
                //修改
                if(vm.role.id!==null) {
                    var data = ({"roleId": vm.role.id,"name": vm.role.name,"roleNo":vm.role.roleNo,"roleDesc":vm.role.roleDesc, "menuIds": getAllCheckedNodeId().join(",")});
                    var postCfg = {
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        transformRequest: function (data) {
                            return $.param(data);
                        }
                    };
                    NetData.post(url, data, postCfg).then(function (data) {
                        $scope.$emit('bekoproApp:roleUpdate', true);
                        $uibModalInstance.close(true);
                        vm.isSaving = false;
                    })
                }else{
                    //新增
                    var data= ({"roleId": vm.role.id,"name": vm.role.name,"roleNo":vm.role.roleNo,"roleDesc":vm.role.roleDesc,"menuIds": getAllCheckedNodeId().join(",")});
                    var postCfg = {
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        transformRequest: function (data) {
                            return $.param(data);
                        }
                    };
                    NetData.post(url, data, postCfg).then(function (data) {
                        $scope.$emit('bekoproApp:roleUpdate', true);
                        $uibModalInstance.close(true);
                        vm.isSaving = false;
                    })
                }
            }else{
                alert("dddd");
                $scope.editForm.submitted = true;
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('bekoproApp:roleUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }
        //获取所有的被选中的节点名称
        function getAllCheckedNodeId() {
            var ref=$("#tree").jstree(true);
            return ref.get_checked();
        }
    }
})();
