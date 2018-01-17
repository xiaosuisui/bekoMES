(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('UserDialogController', UserManagementDialogController);

    UserManagementDialogController.$inject = ['$http','Upload','$timeout', '$scope', '$uibModalInstance', 'entity', 'User','NetData'];

    function UserManagementDialogController ($http, Upload, $timeout, $scope, $uibModalInstance, entity, User, NetData) {
        $scope.uploadImg = '';
        $scope.submitted = false;
        $scope.reader = new FileReader();
        var vm = this;
        vm.clear = clear;
        vm.save = save;
        vm.user = entity;
        vm.img_del = img_del;
        $scope.updateSelection = updateSelection;
        //定义被选中的
        $scope.selected = [];
        $scope.isChecked = isChecked;
        /*返回角色列表*/
        NetData.get('api/roles?page=0&&size=10000&&roleNo=null&&roleName=null').then(function (data) {
            $scope.items = data;
        });
        //判断当前是进入到哪个页面.编辑页面时查出对应的角色列表
        if(entity.id > 0){
            NetData.get("api/getRolesByUserId?id=" + entity.id).then(function (data) {
                angular.fromJson(data).forEach(function (data, index) {
                   $scope.selected.push(data.id);
               })
            });
        }

        //判断是否被选中
        function isChecked(id) {
           return $scope.selected.indexOf(id) >= 0;
        }

        /*单机事件，判断当前是否被选中。如果选中则添加到selected中*/
        function updateSelection($event, id) {
            var data = $event.target.checked ? $scope.selected.push(id) : $scope.selected.splice($scope.selected.indexOf(id),1);
        }

        /*删除图片*/
        function img_del(index) {
            $scope.thumb['imgSrc'] = '';
            vm.user.imageUrl = '';
        }

        $scope.thumb = {};

        /*动态显示上传的图片*/
        $scope.img_upload = function(files)  {
            $scope.reader.readAsDataURL(files[0]);//FileReader方法把图片转成base64
            $scope.reader.onload = function(ev) {
                $scope.$apply(function(){
                    $scope.thumb['imgSrc'] = ev.target.result;//接收base64
                    $scope.thumb['name'] = files[0].name;
                    $scope.thumb['size'] = files[0].size;
                    $scope.thumb['type'] = files[0].type;
                    //同时把imageUrl置为空
                    vm.user.imageUrl = '';
                });
            };
        };

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSaveSuccess (result) {
            vm.isSaving = false;
            $uibModalInstance.close(result);
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function save () {
            if ($scope.editForm.$valid) {
                vm.isSaving = true;
                vm.user.roleIds = $scope.selected.join(",");
                vm.user.image = $scope.thumb['imgSrc'];
                vm.user.fileName = $scope.thumb['name'];
                vm.user.size = $scope.thumb['size'];
                vm.user.type = $scope.thumb['type'];
                if (vm.user.id !== null) {
                    User.update(vm.user, onSaveSuccess, onSaveError);
                } else {
                    User.save(vm.user, onSaveSuccess, onSaveError);
                }
            } else {
                $scope.editForm.submitted = true;
            }
        }
    }
})();
