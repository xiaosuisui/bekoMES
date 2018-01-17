(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('OrderDeleteController',OrderDeleteController);

    OrderDeleteController.$inject = ['$uibModalInstance', 'entity', 'Order'];

    function OrderDeleteController($uibModalInstance, entity, Order) {
        var vm = this;

        vm.order = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Order.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
