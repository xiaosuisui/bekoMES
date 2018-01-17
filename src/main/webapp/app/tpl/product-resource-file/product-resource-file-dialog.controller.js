(function () {
    'use strict';
    angular
        .module('bekoproApp')
        .controller('ProductResourceFileDialogController', ProductResourceFileDialogController);
    ProductResourceFileDialogController.$inject = ['Upload', '$timeout', '$http', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProductResourceFile'];
    function ProductResourceFileDialogController(Upload, $timeout, $http, $scope, $stateParams, $uibModalInstance, entity, ProductResourceFile) {
        var fileStr;
        //获取工位信息列表
        $http.get("/api/allWorkstations")
            .then(function (response) {
                $scope.workstations = response.data;
            });

        $scope.uploadImg = '';
        $scope.reader = new FileReader();
        var vm = this;
        vm.productResourceFile = entity;
        vm.clear = clear;
        vm.save = save;
        vm.img_del = img_del;
        /*删除图片*/
        function img_del(index) {
            $scope.thumb['fileSrc'] = '';
            $scope.thumb['name'] = '';
            vm.productResourceFile.storageLocation = '';
            document.getElementById('field_file').value='';
        }

        $scope.thumb = {};
        if (vm.productResourceFile.type=='Picture') {
            $scope.realSrc = 'upload/prfs/' + vm.productResourceFile.productNo + '/' + vm.productResourceFile.workstationId + '/' + vm.productResourceFile.type + '/' + vm.productResourceFile.storageLocation;
        } else {
            $scope.realSrc = '';
        }
        /*动态显示上传的图片*/
        $scope.img_upload = function (files) {
            $scope.reader.readAsDataURL(files[0]);//FileReader方法把图片转成base64
            $scope.reader.onload = function (ev) {
                $scope.$apply(function () {
                    $scope.thumb['fileSrc'] = ev.target.result;//接收base64
                    $scope.thumb['name'] = files[0].name;
                    //同时把storageLocation置为空
                    vm.productResourceFile.storageLocation = '';
                });
            };
            if (chekoutFileIsNUll()){
                $scope.editForm.submitted = true;
            } else {
                $scope.editForm.submitted = false;
            }
        };
        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            if ($scope.editForm.$valid) {
                vm.isSaving = true;
                vm.productResourceFile.uploadFile = $scope.thumb['fileSrc'];
                vm.productResourceFile.fileName = $scope.thumb['name'];
                //文件非空校验,为空时显示校验不通过
                if (chekoutFileIsNUll() && vm.productResourceFile.storageLocation=='') {
                    $scope.editForm.submitted = true;
                    return;
                }
                if (vm.productResourceFile.id !== null) {
                    ProductResourceFile.update(vm.productResourceFile, onSaveSuccess, onSaveError);
                } else {
                    ProductResourceFile.save(vm.productResourceFile, onSaveSuccess, onSaveError);
                }
            } else {
                $scope.editForm.submitted = true;
            }
        }

        function onSaveSuccess(result) {
            $scope.$emit('bekoproApp:productUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }

        //判断上传文件是否为空
        function  chekoutFileIsNUll() {
            fileStr =document.getElementById('field_file').value;
            if (fileStr.length>0) {
                return false;
            }else {
                return true;
            }
        }
    }
})();
