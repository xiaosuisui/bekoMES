(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('OperatorDataDialogController', OperatorDataDialogController);

    OperatorDataDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'OperatorData'];

    function OperatorDataDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, OperatorData) {
        var vm = this;

        vm.operatorData = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.operatorData.id !== null) {
                OperatorData.update(vm.operatorData, onSaveSuccess, onSaveError);
            } else {
                OperatorData.save(vm.operatorData, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('bekoproApp:operatorDataUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.operationTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
