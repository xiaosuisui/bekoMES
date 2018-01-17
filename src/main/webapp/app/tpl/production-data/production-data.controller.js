
var app = angular.module('bekoproApp');

app.controller('ProductionDataController',['$http','$scope','$window','ProductionData','TableOption', function($http,$scope,$window,ProductionData,TableOption) {
    var vm = this;
    vm.countRow=true;
    vm.search=search;
    $scope.winHeight=angular.element($window).height();
    $scope.init=init;
    //条件选择
    $scope.searchOptions=[
        {value : "productNo", name : "productNo"},
        {value : "barCode", name : "barCode"}
    ];
    function init() {
        var height=$scope.winHeight-215;
        angular.element("#uigridDiv").css("height",height+"px");
    }
    $scope.gridOptions = {
        columnDefs: [
            { field: "barCode",enableCellEdit: false},
            { field: "orderNo",enableCellEdit: false},
            { field: "productNo",enableCellEdit: false},
            { field: "operator",enableCellEdit: false},
            { field: "station",enableCellEdit: false},
            { field: "contentType",enableCellEdit: false},
            { field: "value",enableCellEdit: false},
            { field: "createTime",enableCellEdit: false,type: 'datetime', cellFilter: 'date:\'yyyy-MM-dd hh:mm:ss\''}
        ],

        //---------------api---------------------
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            //分页按钮事件
            gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                if(getPage) {
                    var condition =vm.searchSelectOption;
                    if(condition=="productNo"){
                        getPage(newPage, pageSize,vm.productionData,null);
                    }
                    if(condition=="barCode"){
                        getPage(newPage, pageSize,null,vm.productionData);
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
    var datas=TableOption.getTableOption();
    $.extend(true, $scope.gridOptions, datas);
    //分页查询
    function search() {
        //获取查询条件
        var condition =vm.searchSelectOption;
        if(condition=="productNo"){
            getPage(1, $scope.gridOptions.paginationPageSize,vm.productionData,null);
        }
        if(condition=="barCode"){
            getPage(1, $scope.gridOptions.paginationPageSize,null,vm.productionData);
        }if(condition==""||condition==null||angular.element(condition).isUndefined){
            getPage(1, $scope.gridOptions.paginationPageSize,null,null);
        }
    }
    //分页
    var getPage = function(curPage, pageSize,productNo,barCode) {
        /*获取总记录数*/
        if(angular.isUndefined(productNo)) {
            productNo=null;
        }
        if(angular.isUndefined(barCode)){
            barCode=null;
        }
        /*获取总记录数*/
        $http.get('/api/getAllProductionDataByCondition?productNo='+productNo+"&&barCode="+barCode).success(function (data) {
            $scope.gridOptions.totalItems=data;
        });

        /*默认按照Id降序排列*/
        $scope.data= ProductionData.query({
            page: curPage-1,
            size: pageSize,
            productNo:productNo,
            barCode:barCode
        }, function onSuccess(data,headers) {
            $scope.gridOptions.data=data;
        });
    };
    getPage(1, $scope.gridOptions.paginationPageSize,null,null);
}]);
