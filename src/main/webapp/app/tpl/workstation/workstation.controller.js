/*workstation controllers*/
var app = angular.module('bekoproApp');
app.controller('WorkstationController', ['$http', '$scope', '$window', 'Workstation', 'TableOption',
    function($http, $scope, $window, Workstation, TableOption) {
        var vm = this;
        vm.countRow = true;
        vm.search=search;
        $scope.winHeight = angular.element($window).height();
        $scope.init = init;
        $scope.searchOptions=[
            {value : "stationNo", name : "stationNo"},
            {value : "stationName", name : "stationName"}
        ];
        function init() {
            var height = $scope.winHeight - 215;
            angular.element("#uigridDiv").css("height", height + "px");
        }
        $scope.gridOptions = {
            columnDefs: [
                { field: "stationNo"},
                {field:"seqNo"},
                {field:"stationName"},
                {field:"wsDesc"},
                {field:"allocationPercent"}
            ],
            //---------------api---------------------
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
                //分页按钮事件
                gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
                    if(getPage) {
                        var condition =vm.searchSelectOption;
                        if(condition=="stationNo"){
                            getPage(newPage, pageSize,vm.stationNo,null);
                        }
                        if(condition=="stationName"){
                            getPage(newPage, pageSize,null,vm.stationNo);
                        }if(condition==""||condition==null||angular.element(condition).isUndefined){
                            getPage(newPage,pageSize,null,null);
                        }
                    }
                });
                //行选中事件
                $scope.gridApi.selection.on.rowSelectionChanged($scope, function(row, event){
                    if(row){
                        /*获取选中的行数*/
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
        //分页查询
        function search() {
            //获取查询条件
            var condition =vm.searchSelectOption;
            if(condition=="stationNo"){
                getPage(1, $scope.gridOptions.paginationPageSize,vm.stationNo,null);
            }
            if(condition=="stationName"){
                getPage(1, $scope.gridOptions.paginationPageSize,null,vm.stationNo);
            }if(condition==""||condition==null||angular.element(condition).isUndefined){
                getPage(1, $scope.gridOptions.paginationPageSize,null,null);
            }
        }

        var getPage = function(curPage, pageSize,stationNo,stationName) {
            /*获取总记录数*/
            if(angular.isUndefined(stationNo)){
                stationNo=null;
            }
            if(angular.isUndefined(stationName)){
                stationName=null;
            }
            /*获取总记录数*/
            $http.get("/api/getAllWorkstationByCondition?stationNo="+stationNo+"&&stationName="+stationName).success(function (data) {
                $scope.gridOptions.totalItems = data;
             });
            $scope.data = Workstation.query(
                {
                    page: curPage - 1,
                    size: pageSize,
                    stationNo:stationNo,
                    stationName:stationName
                },
                function onSuccess(data, headers) {
                    $scope.gridOptions.data = data;
                }
            );
        };
        getPage(1, $scope.gridOptions.paginationPageSize,null,null);
    }
]);


