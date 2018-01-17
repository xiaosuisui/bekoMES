var app = angular.module('bekoproApp');

app.controller('userController', ['$http', '$scope', '$window', 'User', 'TableOption',
    function($http, $scope, $window, User, TableOption) {
        var vm = this;
        vm.countRow = true;
        vm.search=search;
        $scope.winHeight = angular.element($window).height();
        $scope.init = init;
        $scope.searchOptions=[
            {value : "login", name : "login"},
            {value : "email", name : "email"}
        ];

        function init() {
            var height = $scope.winHeight - 215;
            angular.element("#uigridDiv").css("height", height + "px");
        }
        //分页查询
        function search() {
            //获取查询条件
            var condition =vm.searchSelectOption;
            if(condition=="login"){
                getPage(1, $scope.gridOptions.paginationPageSize,vm.loginValue,null);
            }
            if(condition=="email"){
                getPage(1, $scope.gridOptions.paginationPageSize,null,vm.loginValue);
            }if(condition==""||condition==null||angular.element(condition).isUndefined){
                getPage(1, $scope.gridOptions.paginationPageSize,null,null);
            }
        }
        $scope.gridOptions = {
            columnDefs: [
                { field: "login", enableCellEdit: false},
                { field: "email", enableCellEdit: false},
                { field: "activated", enableCellEdit: false},
                { field: "firstName", enableCellEdit: false},
                { field: "lastName", enableCellEdit: false},
          /*      {field:'roles',enableCellEdit: false,cellTemplate:"<span ng-repeat='value in row.entity.roles'>{{value.name}}<br></span>"},*/
            ],
            //---------------api---------------------
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
                //分页按钮事件
                gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
                    if(getPage) {
                        var condition =vm.searchSelectOption;
                        if(condition=="login"){
                            getPage(newPage, pageSize,vm.loginValue,null);
                        }
                        if(condition=="email"){
                            getPage(newPage, pageSize,null,vm.loginValue);
                        }if(condition==""||condition==null||angular.element(condition).isUndefined){
                            getPage(newPage,pageSize,null,null);
                        }
                    }
                });
                //行选中事件
                $scope.gridApi.selection.on.rowSelectionChanged($scope, function(row, event){
                    if(row){
                        /*获取选中的行数*/
                        vm.countRow = gridApi.selection.getSelectedCount() == 1 ? false : true;
                        var selectedRows = gridApi.selection.getSelectedGridRows();
                        if(selectedRows != ""){
                            $scope.testRow = selectedRows[0].entity;
                        }
                    }
                });
            }
        };
        /*获取表单的公共属性*/
        $.extend(true, $scope.gridOptions, TableOption.getTableOption());

        var getPage = function(curPage, pageSize,loginName,email) {
            /*获取总记录数*/
            if(angular.isUndefined(loginName)){
                loginName=null
            }
            if(angular.isUndefined(email)){
                email=null;
            }
            /*获取总记录数*/
            $http.get('/api/getAllUsersByCondition?login='+loginName+"&& email="+email).success(function (data) {
                $scope.gridOptions.totalItems = data;
            });
            $scope.data = User.query({
                page: curPage - 1,
                size: pageSize,
                loginName: loginName,
                email: email
            }, function onSuccess(data, headers) {
                $scope.gridOptions.data = data;
            });
        };
        getPage(1, $scope.gridOptions.paginationPageSize,null,null);
    }
]);
