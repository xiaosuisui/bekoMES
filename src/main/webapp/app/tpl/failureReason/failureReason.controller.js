
var app = angular.module('bekoproApp');

app.controller('failureReasonController',['$http','$scope','$window','failureReason','TableOption', function($http,$scope,$window,failureReason,TableOption) {
    var vm = this;
    vm.countRow=true;
    vm.search=search;
    $scope.winHeight=angular.element($window).height();
    $scope.init=init;
    $scope.searchOptions=[
        {value : "workstation", name : "workstation"},
    ];
    function init() {
        var height=$scope.winHeight-215;
        angular.element("#uigridDiv").css("height",height+"px");
    }
    $scope.gridOptions = {
        columnDefs: [
            {field:"workstation",cellTemplate:"<div class='ui-grid-cell-contents ng-binding ng-scope'>{{row.entity.workstation=='rotary'?'knobeStation':row.entity.workstation=='oven'?'burning':row.entity.workstation}}</div>>"},
            { field: "reason"},
            { field: "type"},
            {field:"description"},
            { field: "createTime",type: 'datetime', cellFilter: 'date:\'yyyy-MM-dd hh:mm:ss\''}],
        //---------------api---------------------
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            //分页按钮事件
            gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                if(getPage) {
                    /*getPage(newPage, pageSize);*/
                    var condition =vm.searchSelectOption;
                    if(condition=="workstation"){
                        getPage(newPage, pageSize,vm.failureReasonValue);
                    }if(condition==""||condition==null||angular.element(condition).isUndefined){
                        getPage(newPage,pageSize,null);
                    }
                }
            });
            //行选中事件
            $scope.gridApi.selection.on.rowSelectionChanged($scope,function(row,event){
                if(row){
                    vm.countRow= gridApi.selection.getSelectedCount()==1?false:true;
                    var selectedRows=gridApi.selection.getSelectedGridRows();
                    if(selectedRows!=""){
                        $scope.testRow=selectedRows[0].entity;
                    }
                }
            });
        }
    };
    //分页查询
    function search() {
        //获取查询条件
        var condition =vm.searchSelectOption;
        if(condition=="workstation"){
            getPage(1, $scope.gridOptions.paginationPageSize,vm.failureReasonValue);
        }if(condition==""||condition==null||angular.element(condition).isUndefined){
            getPage(1, $scope.gridOptions.paginationPageSize,null);
        }
    }
    /*获取表单的公共属性*/
    $.extend(true, $scope.gridOptions, TableOption.getTableOption());
    //分页
    var getPage = function(curPage, pageSize,workstation) {
        /*获取总记录数*/
        if(angular.isUndefined(workstation)) {
            workstation=null;
        }
        /*获取总记录数*/
        $http.get("/api/getFailureReasonByCondition?workstation="+workstation).success(function (data) {
            $scope.gridOptions.totalItems=data;
        });
        /*默认按照Id降序排列*/
        $scope.data= failureReason.query({
            page: curPage-1,
            size: pageSize,
            workstation:workstation,
        }, function onSuccess(data,headers) {
            $scope.gridOptions.data=data;
        });
    };
    getPage(1, $scope.gridOptions.paginationPageSize,null);
}]);
