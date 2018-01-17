(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('OrderDialogController', OrderDialogController);

    OrderDialogController.$inject = ['$rootScope','$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Order'];

    function OrderDialogController ($rootScope,$timeout, $scope, $stateParams, $uibModalInstance, entity, Order) {
        var vm = this;
            if(entity.id !== null){
                Date.prototype.Format = function (fmt) {
                    var o = {
                        "M+": this.getMonth() + 1, //月份
                        "d+": this.getDate(), //日
                        "h+": this.getHours(), //小时
                        "m+": this.getMinutes(), //分
                        "s+": this.getSeconds(), //秒
                        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
                        "S": this.getMilliseconds() //毫秒
                    };
                    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
                    for (var k in o)
                        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
                    return fmt;
                }
                entity.operationDateTime1 = new Date(entity.operationDateTime).Format("yyyy-MM-dd hh:mm:ss")

            }
        vm.order = entity;
        vm.clear = clear;
        vm.save = save;
        vm.datePickerOpenStatus = {};
        vm.openCalendar=openCalendar;

            $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            if ($scope.editForm.$valid) {
                vm.isSaving = true;
                if (vm.order.id !== null) {
                    vm.order.operationDateTime = new Date(vm.order.operationDateTime1).getTime();
                    Order.update(vm.order, onSaveSuccess, onSaveError);
                } else {
                    vm.order.operationDateTime = new Date(vm.order.operationDateTime1).getTime();
                    Order.save(vm.order, onSaveSuccess, onSaveError);
                }
            } else {
                $scope.editForm.submitted = true;
            }
        }
        function onSaveSuccess (result) {
            $scope.$emit('bekoproApp:orderUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }
        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

    }
})();
