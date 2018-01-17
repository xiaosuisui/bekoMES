(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('PalletDialogController', PalletDialogController);

    PalletDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Pallet'];

    function PalletDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Pallet) {
        var vm = this;

        vm.pallet = entity;
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
                if (vm.pallet.id !== null) {
                    Pallet.update(vm.pallet, onSaveSuccess, onSaveError);
                } else {
                    Pallet.save(vm.pallet, onSaveSuccess, onSaveError);
                }
            } else {
                $scope.editForm.submitted = true;
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('bekoproApp:palletUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
