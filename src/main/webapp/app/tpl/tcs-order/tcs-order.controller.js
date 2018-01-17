var app = angular.module('bekoproApp');
app.controller('TcsOrderController',['$http','$scope','$window','i18nService','TcsOrder','TableOption', function($http,$scope,$window,i18nService,TcsOrder,TableOption) {
    var vm = this;
    vm.countRow=true;
    vm.search=search;
    $scope.winHeight=angular.element($window).height();
    $scope.init=init;
    function init() {
        var height=$scope.winHeight-215;
        angular.element("#uigridDiv").css("height",height+"px");
    }
    //前端的下选择框
    $scope.searchOptions=[
        {value : "tcsOrderName", name : "tcsOrderName"},
        {value : "stationNo", name : "stationNo"}
    ];
    $scope.gridOptions = {
        columnDefs: [
            { field: "tcsOrderName",enableCellEdit: false},
            { field: "stationNo",enableCellEdit: false},
            { field: "consumePartName",enableCellEdit: false},
            { field: "consumePartQuantity",enableCellEdit: false,width:"18%"},
            { field: "state",enableCellEdit: false},
            { field: "startTime",enableCellEdit: false,type: 'datetime', cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\''}
        ],
        //---------------api---------------------
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            //分页按钮事件
            gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                if(getPage) {
                    var condition =vm.searchSelectOption;
                    if(condition=="tcsOrderName"){
                        getPage(newPage, pageSize,vm.tcsOrder,null);
                    }
                    if(condition=="stationNo"){
                        getPage(newPage, pageSize,null,vm.tcsOrder);
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
    //分页查询
    function search() {
        //获取查询条件
        var condition =vm.searchSelectOption;
        if(condition=="tcsOrderName"){
            getPage(1, $scope.gridOptions.paginationPageSize,vm.tcsOrder,null);
        }
        if(condition=="stationNo"){
            getPage(1, $scope.gridOptions.paginationPageSize,null,vm.tcsOrder);
        }if(condition==""||condition==null||angular.element(condition).isUndefined){
            getPage(1, $scope.gridOptions.paginationPageSize,null,null);
        }
    }
    /*获取表单的公共属性*/
    $.extend(true, $scope.gridOptions, TableOption.getTableOption());
    //分页
    var getPage = function(curPage, pageSize,tcsOrderName,stationNo) {
        /*获取总记录数*/
        if(angular.isUndefined(tcsOrderName)) {
            tcsOrderName=null;
        }
        if(angular.isUndefined(stationNo)){
            stationNo=null;
        }
        /*获取总记录数*/
        $http.get('/api/getAllTcsOrdersByCondition?tcsOrderName='+tcsOrderName+"&&stationNo="+stationNo).success(function (data) {
             $scope.gridOptions.totalItems=data;
         });
        /*默认按照Id降序排列*/
        $scope.data= TcsOrder.query({
            page: curPage-1,
            size: pageSize,
            tcsOrderName:tcsOrderName,
            stationNo:stationNo
        }, function onSuccess(data,headers) {
            $scope.gridOptions.data=data;
        });
    };
    getPage(1, $scope.gridOptions.paginationPageSize,null,null);
}]);
