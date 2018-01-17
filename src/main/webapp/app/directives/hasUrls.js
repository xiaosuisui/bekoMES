/**
 * Created by xiaosui on 2017/7/7.
 */
/*增加控制界面显示和隐藏的指令*/
angular.module('bekoproApp')
    .directive('hasUrls',['$rootScope','$window',function ($rootScope,$window) {
        return {
            restrict: 'AE',
            link: function (scope, element, attrs) {
                var url=attrs.hasUrls;
                function showOrRemoveElement() {
                    var datas=$window.localStorage['url'];
                    /*如果没有分配权限的话,则默认赋予为最大权限*/
                    if(datas.length===0){
                        $rootScope.show=true;
                        element.show();
                    }else{
                        console.log(datas.indexOf(url));
                        datas.indexOf(url)>-1?element.show():element.hide();
                    }
                }
                showOrRemoveElement();
                scope.$on('urlDataChange',showOrRemoveElement);

            }
        }
    }]);
