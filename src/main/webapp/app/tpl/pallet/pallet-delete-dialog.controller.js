(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('PalletDeleteController',PalletDeleteController);

    PalletDeleteController.$inject = ['$uibModalInstance', 'entity', 'Pallet'];

    function PalletDeleteController($uibModalInstance, entity, Pallet) {
        var vm = this;

        vm.pallet = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Pallet.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
