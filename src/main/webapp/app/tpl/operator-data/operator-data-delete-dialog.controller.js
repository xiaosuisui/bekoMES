(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('OperatorDataDeleteController',OperatorDataDeleteController);

    OperatorDataDeleteController.$inject = ['$uibModalInstance', 'entity', 'OperatorData'];

    function OperatorDataDeleteController($uibModalInstance, entity, OperatorData) {
        var vm = this;

        vm.operatorData = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            OperatorData.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
