(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('WorkstationDeleteController',WorkstationDeleteController);

    WorkstationDeleteController.$inject = ['$uibModalInstance', 'entity', 'Workstation'];

    function WorkstationDeleteController($uibModalInstance, entity, Workstation) {
        var vm = this;

        vm.workstation = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Workstation.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
