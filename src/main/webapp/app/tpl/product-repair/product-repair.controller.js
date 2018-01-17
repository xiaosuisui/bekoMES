/**
 * Created by Administrator on 2017/10/24/024.
 */
var app = angular.module('bekoproApp');
app.controller('ProductRepairController',['$http','$scope','$window','i18nService','ProductRepair','TableOption', function($http,$scope,$window,i18nService,ProductRepair,TableOption) {
    var vm = this;
    vm.countRow=true;
    vm.search=search;
    $scope.winHeight=angular.element($window).height();
    $scope.init=init;
    $scope.searchOptions=[
        {value : "productNo", name : "productNo"}
    ];
    function init() {
        var height=$scope.winHeight-215;
        angular.element("#uigridDiv").css("height",height+"px");
    }
    // 国际化；
    i18nService.setCurrentLang("en");
    $scope.gridOptions = {
        columnDefs: [
            { field: "bottomPlaceCode",enableCellEdit: false, displayName:"bekoproApp.productRepair.bottomPlaceCode"},
            { field: "productNo",enableCellEdit: false, displayName:"bekoproApp.productRepair.productNo"},
            { field: "repairReason",enableCellEdit: false, displayName:"bekoproApp.productRepair.repairReason"},
            { field: "state",enableCellEdit: false, displayName:"bekoproApp.productRepair.state"},
            { field: "startTime",enableCellEdit: false,type: 'datetime', cellFilter: 'date:\'yyyy-MM-dd hh:mm:ss\''},
            { field: "endTime",enableCellEdit: false,type: 'datetime', cellFilter: 'date:\'yyyy-MM-dd hh:mm:ss\''}
        ],

        //---------------api---------------------
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            //分页按钮事件
            gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                if(getPage) {
                    var condition =vm.searchSelectOption;
                    if(condition=="productNo"){
                        getPage(newPage, pageSize,vm.productRepair);
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
    /*获取表单的公共属性*/
    $.extend(true, $scope.gridOptions, TableOption.getTableOption());
    //分页查询
    function search() {
        //获取查询条件
        var condition =vm.searchSelectOption;
        if(condition=="productNo"){
            getPage(1, $scope.gridOptions.paginationPageSize,vm.productRepair);
        }if(condition==""||condition==null||angular.element(condition).isUndefined){
            getPage(1, $scope.gridOptions.paginationPageSize,null);
        }
    }
    //分页
    var getPage = function(curPage, pageSize,productNo) {
        /*获取总记录数*/
        $http.get('/api/getAllProductRepaireByCondition?productNo='+productNo).success(function (data) {
            $scope.gridOptions.totalItems=data;
        });
        /*默认按照Id降序排列*/
        $scope.data= ProductRepair.query({
            page: curPage-1,
            size: pageSize,
            productNo:productNo
        }, function onSuccess(data,headers) {
            $scope.gridOptions.data=data;
        });
    };
    getPage(1, $scope.gridOptions.paginationPageSize,null);
}]);


