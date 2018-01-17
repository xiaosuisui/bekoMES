(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('WorkstationDialogController', WorkstationDialogController);

    WorkstationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Workstation'];

    function WorkstationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Workstation) {
        var vm = this;

        vm.workstation = entity;
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
                if (vm.workstation.id !== null) {
                    Workstation.update(vm.workstation, onSaveSuccess, onSaveError);
                } else {
                    Workstation.save(vm.workstation, onSaveSuccess, onSaveError);
                }
            }else{
                $scope.editForm.submitted = true;
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('bekoproApp:workstationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
