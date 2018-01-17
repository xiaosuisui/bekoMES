/**
 * Created by xiaosui on 2017/6/1.
 */
angular.module('bekoproApp').controller('AppCtrl', ['$scope', '$rootScope', '$state', '$timeout', 'Auth', 'NetData', '$window','toastr',
    function ($scope, $rootScope, $state, $timeout, Auth, NetData, $window,toastr) {
        var vm = this;
        vm.login = login;
        $rootScope.showMessage = showMessage;

        function showMessage() {
            if(window.Notification && Notification.permission !== "denied") {
                Notification.requestPermission(function(status) {    // 请求权限
                    if(status === 'granted') {
                        // 弹出一个通知
                        var n = new Notification('Title', {
                            body : 'I am a Notification',
                            icon : 'app/image/a0.jpg'
                        });
                        // 两秒后关闭通知
                        setTimeout(function() {
                            n.close();
                        }, 10000);
                    }
                });
            }
        }

        /*登录*/
        function login (event) {
            event.preventDefault();
            Auth.login({
                username: vm.username,
                password: vm.password,
                rememberMe: vm.rememberMe
            }).then(function () {
                vm.authenticationError = false;
                //如果验证通过的话,则就跳转到登录界面
                $rootScope.$broadcast('authenticationSuccess');
                /*获取该用户的访问菜单的权限*/
                NetData.get("api/getUserAccessUrls/").then(
                    function (data) {
                        //把数据保存到本地共享
                        $window.localStorage['url'] = data;
                        $rootScope.$broadcast('urlDataChange');
                    }
                );
                $state.go('app.main.dashboard');
            }).catch(function () {
                vm.authenticationError = true;
                toastr.error('<span data-translate="login.form.rememberme" class="text">login or password invalid</span>', 'Error');
            });
        }

        /*动态设置宽度的指令*/
        $scope.doThis = function () {
            var imageWidth = 0;
            //预读一遍图片的宽度
            imageWidth = $("#image").css('width').split("px")[0];
            /*获取当前父类div的宽度*/
            var divWidth = $("#div").css('width').split("px")[0];
            /*计算left偏移量*/
            var offsetLeft = (divWidth - imageWidth) / 2;
            /*动态添加偏移*/
            $("#content").css("left", offsetLeft + "px");
            $("#content").css("width", imageWidth + "px");
        };
}]);

angular.module('bekoproApp').directive('imageonload', function () {
    return {
        restrict: 'A', link: function (scope, element, attrs) {
            element.bind('load', function () {
                //call the function that was passed
                scope.$apply(attrs.imageonload);
            });
        }
    };
});

/*折叠窗口的指令*/
angular.module('bekoproApp').directive('resize',['$window',function ($window) {
    return function (scope, element) {
        var w = angular.element($window);
        scope.getWindowDimensions = function () {
            return { 'h': w.height(), 'w': w.width() };
        };
        scope.$watch(scope.getWindowDimensions, function (newValue, oldValue) {
            scope.windowHeight = newValue.h;
            scope.windowWidth = newValue.w;
            scope.doThis();

        }, true);
        w.bind('resize', function () {
            scope.$apply();
        });
    }
}]);
