(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .controller('ProductDialogController', ProductDialogController);
    ProductDialogController.$inject = ['Upload','$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Product'];
    function ProductDialogController (Upload,$timeout, $scope, $stateParams, $uibModalInstance, entity, Product) {
        $scope.uploadImg = '';
        $scope.reader = new FileReader();
        var vm = this;
        vm.product = entity;
        vm.clear = clear;
        vm.save = save;
        vm.img_del=img_del;
        /*删除图片*/
        function img_del(index) {
            $scope.thumb['imgSrc'] = '';
            vm.product.picPath = '';
        }
        $scope.thumb = {};
        /*动态显示上传的图片*/
        $scope.img_upload = function(files)  {
            $scope.reader.readAsDataURL(files[0]);//FileReader方法把图片转成base64
            $scope.reader.onload = function(ev) {
                $scope.$apply(function(){
                    $scope.thumb['imgSrc'] = ev.target.result;//接收base64
                    $scope.thumb['name'] = files[0].name;
                    //同时把picPath置为空
                    vm.product.picPath = '';
                });
            };
        };
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            if ($scope.editForm.$valid) {
                vm.isSaving = true;
                vm.product.image = $scope.thumb['imgSrc'];
                vm.product.fileName = $scope.thumb['name'];
                if (vm.product.id !== null) {
                    Product.update(vm.product, onSaveSuccess, onSaveError);
                } else {
                    Product.save(vm.product, onSaveSuccess, onSaveError);
                }
            } else {
                $scope.editForm.submitted = true;
            }
        }
        function onSaveSuccess (result) {
            $scope.$emit('bekoproApp:productUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }
        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
