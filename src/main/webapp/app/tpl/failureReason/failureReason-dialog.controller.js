(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('failureReasonDialogController', failureReasonDialogController);

    failureReasonDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'failureReason'];

    function failureReasonDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, failureReason) {
        var vm = this;

        vm.failureReason = entity;
        console.log("failureReason"+vm.failureReason.workstation);
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            if ($scope.editForm.$valid) {
                vm.isSaving = true;
                if (vm.failureReason.id !== null) {
                    failureReason.update(vm.failureReason, onSaveSuccess, onSaveError);
                } else {
                    failureReason.save(vm.failureReason, onSaveSuccess, onSaveError);
                }
            } else {
                $scope.editForm.submitted = true;
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('bekoproApp:cycleTimeTarget', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

    }
})();
