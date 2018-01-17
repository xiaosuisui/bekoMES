
var app = angular.module('bekoproApp');

app.controller('OperatorDataController',['$http','$scope','$window','OperatorData','TableOption', function($http,$scope,$window,OperatorData,TableOption) {
    var vm = this;
    vm.countRow=true;
    vm.search=search;
    $scope.searchOptions=[
        {value : "operator", name : "operator"},
        {value : "workstation", name : "workstation"}
    ];
    $scope.winHeight=angular.element($window).height();
    $scope.init=init;
    function init() {
        var height=$scope.winHeight-215;
        angular.element("#uigridDiv").css("height",height+"px");
    }
    $scope.gridOptions = {
        columnDefs: [
            { field: "operator",enableCellEdit: false},
            { field: "operation",enableCellEdit: false},
            { field: "operationTime",enableCellEdit: false,type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\''},
            {field:"workstation",enableCellEdit:false}
        ],
        //---------------api---------------------
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            //分页按钮事件
            gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                if(getPage) {
                    var condition =vm.searchSelectOption;
                    if(condition=="operator"){
                        getPage(newPage, pageSize,vm.operatorData,null);
                    }
                    if(condition=="workstation"){
                        getPage(newPage, pageSize,null,vm.operatorData);
                    }if(condition==""||condition==null||angular.element(condition).isUndefined){
                        getPage(newPage,pageSize,null,null);
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
        if(condition=="operator"){
            getPage(1, $scope.gridOptions.paginationPageSize,vm.operatorData,null);
        }
        if(condition=="workstation"){
            getPage(1, $scope.gridOptions.paginationPageSize,null,vm.operatorData);
        }if(condition==""||condition==null||angular.element(condition).isUndefined){
            getPage(1, $scope.gridOptions.paginationPageSize,null,null);
        }
    }
    //分页
    var getPage = function(curPage, pageSize,operator,workstation) {
        /*获取总记录数*/
        if(angular.isUndefined(operator)){
            operator=null
        }
        if(angular.isUndefined(workstation)){
            workstation=null
        }
        $http.get('/api/getAllOperatorLoginByCondition?operator='+operator+"&&workstation="+workstation).success(function (data) {
            $scope.gridOptions.totalItems=data;
        });
        /*默认按照Id降序排列*/
        $scope.data= OperatorData.query({
            page: curPage-1,
            size: pageSize,
            operator:operator,
            workstation:workstation
        }, function onSuccess(data,headers) {
            $scope.gridOptions.data=data;
        });
    };
    getPage(1, $scope.gridOptions.paginationPageSize,null,null);
}]);

