/**
 * Created by Ricardo on 2017/8/21.
 */
(function() {
    'use strict';

    angular
        .module('bekoproApp')
        .controller('PasswordController', PasswordController);

    PasswordController.$inject = ['$http'];

    function PasswordController ($http) {
        var vm =this;
        vm.save = save;
        function save() {
            //待完成的方法,请求后台验证
      /*      $http({
                method:'post',
                url: '/api/check/' + attrs.ensureUnique,
                params: {'field': attrs.ensureUnique,'value':data},
            }).success(function (data) {
                c.$setValidity('unique', data);
            }).error(function (data, status, headers, cfg) {
                c.$setValidity('unique', false)
            })*/
            $http({
                method:'get',
                url:'api/changePassword',
                // headers:{'Content-Type':'application/json'},
                // data:JSON.stringify({'oldPassword':vm.tmpPassArgs.oldPassword,'newPassword':vm.tmpPassArgs.newPassword,'confirmPassword':vm.tmpPassArgs.confirmPassword}),
                params:{'oldPassword':vm.tmpPassArgs.
                    oldPassword,'newPassword':vm.tmpPassArgs.newPassword,'confirmPassword':vm.tmpPassArgs.confirmPassword},
            }).success(function (data,status,headers,config) {
                // alert(data);
                if(data == false){
                    alert("输入密码与原密码不一致！");
                }else{
                    alert("密码修改成功！");
                }
            }).error(function (data, status, headers, cfg) {
                alert(status);
            })
        }

    }
})();