(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('failureReasonDeleteController',failureReasonDeleteController);

    failureReasonDeleteController.$inject = ['$uibModalInstance', 'entity', 'failureReason'];

    function failureReasonDeleteController($uibModalInstance, entity, failureReason) {
        var vm = this;

        vm.failureReason = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            failureReason.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
