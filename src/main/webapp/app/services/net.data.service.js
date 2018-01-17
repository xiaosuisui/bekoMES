/**
 * Created by xiaosui on 2017/6/29.
 */
(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('NetData', NetData);
    NetData.$inject = ['$http','$q'];
    function NetData($http,$q) {
        return {
            get: function (url, param) {
                var deffer = $q.defer();
                $http.get(url, {param: param}).success(function (response) {
                    deffer.resolve(response);  // 声明执行成功，即http请求数据成功，可以返回数据了
                }).error(function (data) {
                    //错误信息
                    deffer.reject(data);   // 声明执行失败，即服务器返回错误
                });
                return deffer.promise;
            },
            post: function (url, data,config) {
                var deffer = $q.defer();
                $http.post(url, data,config).success(function (response) {
                    deffer.resolve(response);  // 声明执行成功，即http请求数据成功，可以返回数据了
                }).error(function (data, status, headers, config) {
                    //错误信息
                    deffer.reject(data);   // 声明执行失败，即服务器返回错误
                });
                return deffer.promise;
            }
        }
    }
})();
