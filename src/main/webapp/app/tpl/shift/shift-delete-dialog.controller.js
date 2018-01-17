(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('ShiftDeleteController',ShiftDeleteController);

    ShiftDeleteController.$inject = ['$uibModalInstance', 'entity', 'Shift'];

    function ShiftDeleteController($uibModalInstance, entity, Shift) {
        var vm = this;

        vm.Shift = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Shift.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
