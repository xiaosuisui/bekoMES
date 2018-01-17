/**
 * Created by Ricardo on 2017/8/22.
 */
angular.module('bekoproApp')
    .directive('pwdMatch',function () {
        return{
            restrict: "A",
            require: 'ngModel',
            link: function(scope,element,attrs,ctrl) {
                /*监控newPassword*/
                var tageCtrl = scope.$eval(attrs.pwdMatch);
                tageCtrl.$parsers.push(function(viewValue){
                    console.log(viewValue);
                    ctrl.$setValidity('pwdmatch', viewValue == ctrl.$viewValue);
                    return viewValue;
                 });
                /*监控ConfirmPassword*/
                ctrl.$parsers.push(function(viewValue){
                    if(viewValue == tageCtrl.$viewValue){
                        ctrl.$setValidity('pwdmatch', true);
                        return viewValue;
                    } else{
                        ctrl.$setValidity('pwdmatch', false);
                        return undefined;
                    }
                });
            }
        }
    });