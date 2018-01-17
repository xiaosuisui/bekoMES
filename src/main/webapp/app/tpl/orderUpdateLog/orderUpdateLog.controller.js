var app = angular.module('bekoproApp');
app.controller('orderUpdateLogController',['$http','$scope','$window','OrderUpdateLog','TableOption', function($http,$scope,$window,OrderUpdateLog,TableOption) {
    var vm = this;
    vm.search=search;
    vm.countRow=true;
    $scope.winHeight=angular.element($window).height();
    $scope.init=init;
    $scope.searchOptions=[
        {value : "username", name : "username"},
        {value : "operatorType", name : "operatorType"}
    ];
    function init() {
        var height=$scope.winHeight-215;
        angular.element("#uigridDiv").css("height",height+"px");
    }
    $scope.gridOptions = {
        columnDefs: [
            { field: "username",enableCellEdit: false},
            { field: "moduleName",enableCellEdit: false},
            { field: "operatorType",enableCellEdit: false},
            { field: "operatorValue",enableCellEdit: false,width:"35%"},
            { field: "operatorTime",enableCellEdit: false,type: 'datetime', cellFilter: 'date:\'yyyy-MM-dd hh:mm:ss\''},
        ],

        //---------------api---------------------
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            //分页按钮事件
            gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                if(getPage) {
                    var condition =vm.searchSelectOption;
                    if(condition=="username"){
                        getPage(newPage, pageSize,vm.orderUpdateLog,null);
                    }
                    if(condition=="operatorType"){
                        getPage(newPage, pageSize,null,vm.orderUpdateLog);
                    }if(condition==""||condition==null||angular.element(condition).isUndefined){
                        getPage(newPage,pageSize,null,null);
                    };
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
    /*获取表单的公共属性*/
    $.extend(true, $scope.gridOptions, TableOption.getTableOption());
    //分页查询
    function search() {
        //获取查询条件
        var condition =vm.searchSelectOption;
        if(condition=="username"){
            getPage(1, $scope.gridOptions.paginationPageSize,vm.orderUpdateLog,null);
        }
        if(condition=="operatorType"){
            getPage(1, $scope.gridOptions.paginationPageSize,null,vm.orderUpdateLog);
        }if(condition==""||condition==null||angular.element(condition).isUndefined){
            getPage(1, $scope.gridOptions.paginationPageSize,null,null);
        }
    }
    //分页
    var getPage = function(curPage, pageSize,username,operatorType) {
        /*获取总记录数*/
        if(angular.isUndefined(username)) {
            username=null;
        }
        if(angular.isUndefined(operatorType)){
            operatorType=null;
        }
        /*获取总记录数*/
        $http.get('/api/getAllOrderUpdateLogByCondition?username='+username+"&&operatorType="+operatorType).success(function (data) {
            $scope.gridOptions.totalItems=data;
        });
        /*默认按照Id降序排列*/
        $scope.data= OrderUpdateLog.query({
            page: curPage-1,
            size: pageSize,
            username:username,
            operatorType:operatorType
        }, function onSuccess(data,headers) {
            console.log(data);
            $scope.gridOptions.data=data;
        });
    };
    getPage(1, $scope.gridOptions.paginationPageSize,null,null);
}]);

