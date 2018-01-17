/**
 * Created by Ricardo on 2017/8/22.
 * 前端唯一性校验的指令
 */
angular.module('bekoproApp')
    .directive('ensureUnique',['$http', function ($http) {
        return{
            require: 'ngModel',
            link: function (scope, ele ,attrs, c) {
                scope.$watch(attrs.ngModel, function (data, type) {
                    if (!data || data == type) return;
                    $http({
                        method:'post',
                        url: '/api/check/' + attrs.ensureUnique,
                        params: {'field':attrs.ensureUnique, 'value':data},
                    }).success(function (data) {
                        c.$setValidity('unique', data);
                    }).error(function (data, status, headers, cfg) {
                        c.$setValidity('unique', false)
                    })
                });
            }
        }

    }]);