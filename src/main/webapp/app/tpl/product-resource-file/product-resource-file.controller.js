
var app = angular.module('bekoproApp');

app.controller('ProductResourceFileController',['$http','$scope','$window','i18nService','ProductResourceFile','TableOption', function($http,$scope,$window,i18nService,ProductResourceFile,TableOption) {
    var vm = this;
    vm.countRow=true;
    vm.search=search;
    $scope.winHeight=angular.element($window).height();
    $scope.init=init;
    $scope.searchOptions=[
        {value : "productNo", name : "productNo"},
    ];
    function init() {
        var height=$scope.winHeight-215;
        angular.element("#uigridDiv").css("height",height+"px");
    }
    // 国际化；
    i18nService.setCurrentLang("en");
    $scope.gridOptions = {
        columnDefs: [
            {field: "productNo"},
            {field:"workstationId"},
            // {field:"type",cellTemplate:"<div class='ui-grid-cell-contents ng-binding ng-scope'>{{row.entity.type=='PDF'?'PDF':row.entity.type=='Excel'?'Excel':row.entity.type=='Word'?'Word':row.entity.type=='Video'?'Video':'Picture'}}</div>"},
            {field:"type"},
            {field:"storageLocation"}
        ],
        //---------------api---------------------
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            //分页按钮事件
            gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                if(getPage) {
                    var condition =vm.searchSelectOption;
                    if(condition=="productNo"){
                        getPage(newPage, pageSize,vm.fileResource);
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
            getPage(1, $scope.gridOptions.paginationPageSize,vm.fileResource);
        }if(condition==""||condition==null||angular.element(condition).isUndefined){
            getPage(1, $scope.gridOptions.paginationPageSize,null);
        }
    }
    var getPage = function(curPage, pageSize,productNo) {
        /*获取总记录数*/
        if(angular.isUndefined(productNo)) {
            productNo=null;
        }
        /*获取总记录数*/
        $http.get('/api/getAllProductResouresByCondition?productNo='+productNo).success(function (data) {
          $scope.gridOptions.totalItems=data;
         });
        $scope.data= ProductResourceFile.query({
            page: curPage-1,
            size: pageSize,
            productNo:productNo
        }, function onSuccess(data,headers) {
            $scope.gridOptions.data=data;
        });
    };
    getPage(1, $scope.gridOptions.paginationPageSize,null);
}]);

