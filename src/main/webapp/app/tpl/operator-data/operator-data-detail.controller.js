(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('OperatorDataDetailController', OperatorDataDetailController);

    OperatorDataDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'OperatorData'];

    function OperatorDataDetailController($scope, $rootScope, $stateParams, previousState, entity, OperatorData) {
        var vm = this;

        vm.operatorData = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('bekoproApp:operatorDataUpdate', function(event, result) {
            vm.operatorData = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
