(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('tvDataConfigDialogController', tvDataConfigDialogController);

    tvDataConfigDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'tvDataConfig'];

    function tvDataConfigDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, tvDataConfig) {
        var vm = this;
        $scope.change=change;
        function change(data) {
            if(data=="tvScreen001"){
                $scope.pageNames=[{id:"screen01","name":"screen01"},{id:"welcomePage01","name":"welcomePage01"},{id:"welcomePage02","name":"welcomePage02"}]
            }
            if(data=="tvScreen002"){
                $scope.pageNames=[{id:"screen02","name":"screen02"},{id:"welcomePage01","name":"welcomePage01"},{id:"welcomePage02","name":"welcomePage02"}]
            }
            if(data=="tvScreen003"){
                $scope.pageNames=[{id:"screen03","name":"screen03"},{id:"welcomePage01","name":"welcomePage01"},{id:"welcomePage02","name":"welcomePage02"}]
            }
        }

        vm.tvDataConfig = entity;
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
                if (vm.tvDataConfig.id !== null) {
                    tvDataConfig.update(vm.tvDataConfig, onSaveSuccess, onSaveError);
                } else {
                    tvDataConfig.save(vm.tvDataConfig, onSaveSuccess, onSaveError);
                }
            } else {
                $scope.editForm.submitted = true;
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('bekoproApp:cycleTimeTarget', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

    }
})();
