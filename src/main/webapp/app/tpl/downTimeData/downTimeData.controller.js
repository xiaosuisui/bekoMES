var app = angular.module('bekoproApp');
app.controller('downTimeDataController',['$http','$scope','$window','downTimeData','TableOption', function($http,$scope,$window,downTimeData,TableOption) {
    var vm = this;
    vm.search=search;
    vm.countRow=true;
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
            { field: "operator",enableCellEdit: false},
            { field: "workstation",enableCellEdit: false},
            { field: "reason",enableCellEdit: false},
            { field: "createTime",enableCellEdit: false,type: 'datetime', cellFilter: 'date:\'yyyy-MM-dd hh:mm:ss\''},
            { field: "endTime",enableCellEdit: false,type: 'datetime', cellFilter: 'date:\'yyyy-MM-dd hh:mm:ss\''},
        ],

        //---------------api---------------------
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            //分页按钮事件
            gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                if(getPage) {
                    var condition =vm.searchSelectOption;
                    if(condition=="workstation"){
                        getPage(newPage, pageSize,vm.downTimeDataValue);
                    }if(condition==""||condition==null||angular.element(condition).isUndefined){
                        getPage(newPage,pageSize,null);
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
        if(condition=="workstation"){
            getPage(1, $scope.gridOptions.paginationPageSize,vm.downTimeDataValue);
        }if(condition==""||condition==null||angular.element(condition).isUndefined){
            getPage(1, $scope.gridOptions.paginationPageSize,null);
        }
    }
    //分页
    var getPage = function(curPage, pageSize,workstation) {
        /*获取总记录数*/
        if(angular.isUndefined(workstation)) {
            workstation=null;
        }
        /*获取总记录数*/
        $http.get('/api/getAllDownTimeDataByCondition?workstation='+workstation).success(function (data) {
            $scope.gridOptions.totalItems=data;
        });
        /*默认按照Id降序排列*/
        $scope.data= downTimeData.query({
            page: curPage-1,
            size: pageSize,
            workstation:workstation,
        }, function onSuccess(data,headers) {
            $scope.gridOptions.data=data;
        });
    };
    getPage(1, $scope.gridOptions.paginationPageSize,null);
}]);

