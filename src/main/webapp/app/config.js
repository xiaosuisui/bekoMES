/**
 * Created by Ricardo on 2017/8/12.
 */
/*config*/
var app= angular.module('bekoproApp')
    .config(['$controllerProvider','$compileProvider','$filterProvider','$provide','$logProvider',
        function($controllerProvider,$compileProvider,$filterProvider,$provide,$logProvider){
            /*lazy controller,directive and service*/
            app.controller=$controllerProvider.register;
            app.directive=$compileProvider.directive;
            app.filter=$filterProvider.register;
            app.factory=$provide.factory;
            app.service=$provide.service;
            app.constant=$provide.constant;
            app.value=$provide.value;
        }
    ]);
app.config(function(toastrConfig) {
    angular.extend(toastrConfig, {
        allowHtml: true,
        closeButton: true,
        autoDismiss: false,
        containerId: 'toast-container',
        timeOut: 2000,
        maxOpened: 0,
        newestOnTop: true,
        positionClass: 'toast-bottom-right',

        preventDuplicates: false,
        preventOpenDuplicates: false,
        target: 'body'
    });
});