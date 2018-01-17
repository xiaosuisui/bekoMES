(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('tvDataConfigDeleteController',tvDataConfigDeleteController);

    tvDataConfigDeleteController.$inject = ['$uibModalInstance', 'entity', 'tvDataConfig'];

    function tvDataConfigDeleteController($uibModalInstance, entity, tvDataConfig) {
        var vm = this;

        vm.tvDataConfig = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            tvDataConfig.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
