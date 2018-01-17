(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('cycleTimeTargetDialogController', cycleTimeTargetDialogController);

    cycleTimeTargetDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'cycleTimeTarget'];

    function cycleTimeTargetDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, cycleTimeTarget) {
        var vm = this;

        vm.cycleTimeTarget = entity;
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
                if (vm.cycleTimeTarget.id !== null) {
                    cycleTimeTarget.update(vm.cycleTimeTarget, onSaveSuccess, onSaveError);
                } else {
                    cycleTimeTarget.save(vm.cycleTimeTarget, onSaveSuccess, onSaveError);
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
