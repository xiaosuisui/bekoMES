/**
 * Created by xiaosui on 2017/6/29.
 */
var app = angular.module('bekoproApp');

app.controller('RoleController', ['$rootScope', '$http', '$scope', '$window', 'Role', 'NetData','TableOption',
    function($rootScope, $http, $scope, $window, Role, NetData, TableOption) {
        var vm = this;
        vm.search=search;
        $scope.winHeight=angular.element($window).height();
        $scope.searchOptions=[
            {value : "roleNo", name : "roleNo"},
            {value : "roleName", name : "roleName"}
        ];
        $scope.init=init;
        function init() {
            var height=$scope.winHeight-215;
            angular.element("#uigridDiv").css("height",height+"px");
        }
        $scope.gridOptions = {
            columnDefs: [
                /*{ field: 'id',enableCellEdit:false,enableColumnMenu: true},*/
                { field:'roleNo',enableCellEdit:false},
                { field: 'name',enableCellEdit:false},
                { field: 'roleDesc',enableCellEdit:false}
                ],
            //---------------api---------------------
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
                //分页按钮事件
                gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
                    if(getPage) {
                        var condition =vm.searchSelectOption;
                        if(condition=="roleNo"){
                            getPage(newPage, pageSize,vm.roleValue,null);
                        }
                        if(condition=="roleName"){
                            getPage(newPage, pageSize,null,vm.roleValue);
                        }if(condition==""||condition==null||angular.element(condition).isUndefined){
                            getPage(newPage,pageSize,null,null);
                        }
                    }
                });
                //行选中事件
                $scope.gridApi.selection.on.rowSelectionChanged($scope, function(row, event){
                    /*通过选中的行数来控制当前按钮的状态,1行为显示,多行隐藏*/
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
            if(condition=="roleNo"){
                getPage(1, $scope.gridOptions.paginationPageSize,vm.roleValue,null);
            }
            if(condition=="roleName"){
                getPage(1, $scope.gridOptions.paginationPageSize,null,vm.roleValue);
            }if(condition==""||condition==null||angular.element(condition).isUndefined){
                getPage(1, $scope.gridOptions.paginationPageSize,null,null);
            }
        }
        var getPage = function(curPage, pageSize,roleNo,roleName) {
            /*获取总记录数*/
            if(angular.isUndefined(roleNo)){
                roleNo=null
            }
            if(angular.isUndefined(roleName)){
                roleName=null
            }
            /*获取总记录数*/
            $http.get('/api/getAllCountRolesByCondition?roleNo='+roleNo+"&&roleName="+roleName).success(function (data) {
                $scope.gridOptions.totalItems=data;
            });
            $scope.data = Role.query(
                {
                    page: curPage - 1,
                    size: pageSize,
                    roleNo:roleNo,
                    roleName:roleName
                },
                function onSuccess(data, headers) {
                    $scope.gridOptions.data = data;
                }
            );
        };
        getPage(1, $scope.gridOptions.paginationPageSize,null,null);
    }
]);

