(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('OperationDialogController', OperationDialogController);

    OperationDialogController.$inject = ['$http', '$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Operation'];

    function OperationDialogController ($http, $timeout, $scope, $stateParams, $uibModalInstance, entity, Operation) {
        var vm = this;
        vm.operation = entity;
        vm.clear = clear;
        vm.save = save;
        getSelectWorkstation();

        function getSelectWorkstation() {
            var baseUrl="api/workstations";
            $http.get(baseUrl).success(function (data) {
                if (data) {
                    data = angular.fromJson(data);
                    vm.workstations = data;
                }
            });
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            if($scope.editForm.$valid) {
                vm.isSaving = true;
                debugger;
                if (vm.operation.id !== null) {
                    Operation.update(vm.operation, onSaveSuccess, onSaveError);
                } else {
                    Operation.save(vm.operation, onSaveSuccess, onSaveError);
                }
            }else{
                $scope.editForm.submitted = true;
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('bekoproApp:operationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
