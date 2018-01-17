/*workstation controllers*/
var app = angular.module('bekoproApp');

app.controller('OperationController',['$http', '$scope', '$window', 'i18nService', 'Operation', 'TableOption',
    function($http, $scope, $window, i18nService, Operation, TableOption) {
        var vm = this;
        vm.countRow = true;
        /*获取工位列表*/
        $scope.winHeight = angular.element($window).height();
        $scope.init = init;

        function init() {
            var height = $scope.winHeight - 160;
            angular.element("#uigridDiv").css("height", height + "px");
        }

        // 国际化；
        i18nService.setCurrentLang("en");

        $scope.gridOptions = {
            columnDefs: [
                { field: "seqNo"},
                {field:"operationName"},
                {field:"workstation", cellTemplate:"<span>{{row.entity.workstation.stationName}}</span>"}
            ],
            //---------------api---------------------
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
                //分页按钮事件
                gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
                    if(getPage) {
                        getPage(newPage, pageSize);
                    }
                });
                //行选中事件
                $scope.gridApi.selection.on.rowSelectionChanged($scope, function(row, event){
                    if(row){
                        vm.countRow = gridApi.selection.getSelectedCount() == 1 ? false : true;
                        var selectedRows = gridApi.selection.getSelectedGridRows();
                        if(selectedRows != ""){
                            $scope.testRow = selectedRows[0].entity;
                        }
                    }
                });
            }
        };

        /*获取表单的公共属性*/
        $.extend(true, $scope.gridOptions, TableOption.getTableOption());

        var getPage = function(curPage, pageSize) {
            /*获取总记录数*/
            $http.get('/api/getAllOperationCount').success(function (data) {
                $scope.gridOptions.totalItems = data;
             });
            $scope.data = Operation.query({
                page: curPage - 1,
                size: pageSize,
            }, function onSuccess(data, headers) {
                $scope.gridOptions.data = data;;
            });
        };

        getPage(1, $scope.gridOptions.paginationPageSize);
}]);
