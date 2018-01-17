(function() {
    'use strict';

    angular
        .module('bekoproApp', [
            'ngAnimate',
            'ngStorage',
            'tmh.dynamicLocale',
            'pascalprecht.translate',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'oc.lazyLoad',
            'infinite-scroll',
            'toastr',
            // jhipster-needle-angularjs-add-module JHipster will add new module here,
            'ui.load',
            'ui.grid','ui.grid.selection','ui.grid.edit','ui.grid.rowEdit','ui.grid.edit',
            'ui.grid.exporter','ui.grid.pagination','ui.grid.resizeColumns','ui.grid.autoResize'
        ])
        .run(run);

    run.$inject = ['stateHandler', 'translationHandler'];

    function run(stateHandler, translationHandler) {
        stateHandler.initialize();
        translationHandler.initialize();
    }
})();
