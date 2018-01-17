(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('ProductResourceFileDeleteController',ProductResourceFileDeleteController);

    ProductResourceFileDeleteController.$inject = ['$uibModalInstance', 'entity', 'ProductResourceFile'];

    function ProductResourceFileDeleteController($uibModalInstance, entity, ProductResourceFile) {
        var vm = this;

        vm.productResourceFile = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ProductResourceFile.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
