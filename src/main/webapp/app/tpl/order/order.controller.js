    var app = angular.module('bekoproApp');
    app.controller('OrderController',['$http','$scope','$window','Order','TableOption','$state', function($http,$scope,$window,Order,TableOption,$state) {
        var vm = this;
        vm.currentSate=$state.current.name;
        //app.main.order===0123,production==4
        vm.search=search;
        $scope.winHeight=angular.element($window).height();
        $scope.searchOptions=[
            {value : "productNo", name : "productNo"},
            {value : "orderNo", name : "orderNo"}
        ];
        $scope.init=init;
        function init() {
            var height=$scope.winHeight-215;
            angular.element("#uigridDiv").css("height",height+"px");
        }
        $scope.gridOptions = {
            columnDefs: [
                { field: "orderNo",enableCellEdit: false},
                { field: "productNo",enableCellEdit: false},
                { field: "quantity",enableCellEdit: false},
                { field: "operationDateTime",displayName:"operationTime",enableCellEdit: false,type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\''},
                { field: "endDate",enableCellEdit: false,type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\''},
                { field: "onlineNumber",enableCellEdit: false},
                { field: "status",cellTemplate:"<div class='ui-grid-cell-contents ng-binding ng-scope'>{{row.entity.status=='0'?'未开始':row.entity.status=='1'?'进行中':row.entity.status=='2'?'已完成有返修':'完成'}}</div>"}
            ],
            //---------------api---------------------
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
                //分页按钮事件
                gridApi.pagination.on.paginationChanged($scope,function(newPage, pageSize) {
                    if(getPage) {
                        debugger;
                        var condition =vm.searchSelectOption;
                        if(condition=="orderNo"){
                            getPage(newPage, pageSize,vm.orderNo,null,vm.currentSate);
                        }
                        if(condition=="productNo"){
                            getPage(newPage, pageSize,null,vm.orderNo,vm.currentSate);
                        }if(condition==""||condition==null||angular.element(condition).isUndefined){
                            getPage(newPage,pageSize,null,null);
                        }
                    }
                });
                //行选中事件
                $scope.gridApi.selection.on.rowSelectionChanged($scope,function(row,event){
                    if(row){
                        vm.countRow= gridApi.selection.getSelectedCount()==1?false:true;
                        var selectedRows=gridApi.selection.getSelectedGridRows();
                        if(selectedRows!=""){
                            $scope.orderRow=selectedRows[0].entity;
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
            if(condition=="orderNo"){
                getPage(1, $scope.gridOptions.paginationPageSize,vm.orderNo,null,vm.currentSate);
            }
            if(condition=="productNo"){
                getPage(1, $scope.gridOptions.paginationPageSize,null,vm.orderNo,vm.currentSate);
            }if(condition==""||condition==null||angular.element(condition).isUndefined){
                getPage(1, $scope.gridOptions.paginationPageSize,null,null,vm.currentSate);
            }
        }
        //分页
        var getPage = function(curPage, pageSize,orderNo,productNo) {
            /*获取总记录数*/
            if(angular.isUndefined(productNo)){
                productNo=null
            }
            if(angular.isUndefined(orderNo)){
                orderNo=null;
            }
            $http.get('/api/getAllOrdersByCondition?orderNo='+orderNo+"&&productNo="+productNo+"&&currentState="+vm.currentSate).success(function (data) {
                $scope.gridOptions.totalItems=data;
            });
            /*默认按照Id降序排列*/
           $scope.data= Order.query({
                page: curPage-1,
                size: pageSize,
               orderNo: orderNo,
               productNo:productNo,
               currentState:vm.currentSate
            }, function onSuccess(data,headers) {
                 $scope.gridOptions.data=data;
            });
        };
        getPage(1, $scope.gridOptions.paginationPageSize,null,null,vm.currentSate);
    }]);
