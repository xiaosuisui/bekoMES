
var app = angular.module('bekoproApp');

app.controller('ShiftController',['$http','$scope','$window','Shift','TableOption', function($http,$scope,$window,Shift,TableOption) {
    var vm = this;
    vm.countRow=true;
    vm.search=search;
    $scope.winHeight=angular.element($window).height();
    $scope.init=init;
    $scope.searchOptions=[
        {value : "name", name : "name"},
    ];

    function init() {
        var height=$scope.winHeight-215;
        angular.element("#uigridDiv").css("height",height+"px");
    }
    $scope.gridOptions = {
        columnDefs: [{ field: 'name'},
            { field: "description"},
            { field:"active"}
/*
            {field:"state",cellTemplate:"<div class='ui-grid-cell-contents ng-binding ng-scope'>{{row.entity.state=='A'?'正常':row.entity.state=='B'?'下线':'空盘'}}</div>>"}
*/
        ],
        //---------------api---------------------
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            //分页按钮事件
            gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                if(getPage) {
                    /*getPage(newPage, pageSize);*/
                    var condition =vm.searchSelectOption;
                    if(condition=="name"){
                        getPage(newPage, pageSize,vm.shiftName,null);
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
        if(condition=="name"){
            getPage(1, $scope.gridOptions.paginationPageSize,vm.shiftName);
        }if(condition==""||condition==null||angular.element(condition).isUndefined){
            getPage(1, $scope.gridOptions.paginationPageSize,null);
        }
    }
    /*获取表单的公共属性*/
    $.extend(true, $scope.gridOptions, TableOption.getTableOption());
    //分页
    var getPage = function(curPage, pageSize,name) {
        /*获取总记录数*/
        if(angular.isUndefined(name)) {name=null;}
        /*获取总记录数*/
        $http.get("/api/getAllPalletsByCondition?name="+name).success(function (data) {
            $scope.gridOptions.totalItems=data;
        });
        /*默认按照Id降序排列*/
        $scope.data= Shift.query({
            page: curPage-1,
            size: pageSize,
            name: name
        }, function onSuccess(data,headers) {
            $scope.gridOptions.data=data;
        });
    };
    getPage(1, $scope.gridOptions.paginationPageSize,null);
}]);
