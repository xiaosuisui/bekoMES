(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('cycleTimeTargetDeleteController',cycleTimeTargetDeleteController);

    cycleTimeTargetDeleteController.$inject = ['$uibModalInstance', 'entity', 'cycleTimeTarget'];

    function cycleTimeTargetDeleteController($uibModalInstance, entity, cycleTimeTarget) {
        var vm = this;

        vm.cycleTimeTarget = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            cycleTimeTarget.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
