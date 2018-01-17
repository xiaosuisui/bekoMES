var app = angular.module('bekoproApp');
app.controller('ProductCodeController',['$http','$scope','$window','ProductCode','TableOption', function($http,$scope,$window,ProductCode,TableOption) {
    var vm = this;
    vm.search=search;
    vm.countRow=true;
    $scope.winHeight=angular.element($window).height();
    $scope.init=init;
    $scope.searchOptions=[
        {value : "productNo", name : "productNo"},
        {value : "orderNo", name : "orderNo"}
    ];
    function init() {
        var height=$scope.winHeight-215;
        angular.element("#uigridDiv").css("height",height+"px");
    }
    $scope.gridOptions = {
        columnDefs: [
            { field: "orderNo",enableCellEdit: false},
            { field: "productNo",enableCellEdit: false},
            { field: "serialNo",enableCellEdit: false},
            { field: "productCode",enableCellEdit: false},
            { field: "status",enableCellEdit: false},
            { field: "createDate",enableCellEdit: false,type: 'datetime', cellFilter: 'date:\'yyyy-MM-dd hh:mm:ss\''},
            { field: "printDate",enableCellEdit: false,type: 'datetime', cellFilter: 'date:\'yyyy-MM-dd hh:mm:ss\''}
        ],

        //---------------api---------------------
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            //分页按钮事件
            gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                if(getPage) {
                    var condition =vm.searchSelectOption;
                    if(condition=="productNo"){
                        getPage(newPage, pageSize,vm.productCodeValue,null);
                    }
                    if(condition=="orderNo"){
                        getPage(newPage, pageSize,null,vm.productCodeValue);
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
        if(condition=="productNo"){
            getPage(1, $scope.gridOptions.paginationPageSize,vm.productCodeValue,null);
        }
        if(condition=="orderNo"){
            getPage(1, $scope.gridOptions.paginationPageSize,null,vm.productCodeValue);
        }if(condition==""||condition==null||angular.element(condition).isUndefined){
            getPage(1, $scope.gridOptions.paginationPageSize,null,null);
        }
    }
    //分页
    var getPage = function(curPage, pageSize,productNo,orderNo) {
        /*获取总记录数*/
        if(angular.isUndefined(productNo)) {
            productNo=null;
        }
        if(angular.isUndefined(orderNo)){
            orderNo=null;
        }
        /*获取总记录数*/
        $http.get('/api/getAllProductCodeByCondition?productNo='+productNo+"&&orderNo="+orderNo).success(function (data) {
            $scope.gridOptions.totalItems=data;
        });
        /*默认按照Id降序排列*/
        $scope.data= ProductCode.query({
            page: curPage-1,
            size: pageSize,
            productNo:productNo,
            orderNo:orderNo
        }, function onSuccess(data,headers) {
            console.log(data);
            $scope.gridOptions.data=data;
        });
    };
    getPage(1, $scope.gridOptions.paginationPageSize,null,null);
}]);

