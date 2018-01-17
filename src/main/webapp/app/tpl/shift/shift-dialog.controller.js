(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .controller('ShiftDialogController', ShiftDialogController);

    ShiftDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Shift','TableOption','$uibModal','$http','$state'];

    function ShiftDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Shift,TableOption,$uibModal,$http,$state) {
        var vm = this;
        $scope.detailInit=detailInit;
        vm.shift = entity;
        //通过shiftId查询对应的details infornation,并put到gridData
        if(entity.id!=null){
            $http({
                method: 'GET',
                url: "/api/getDetailByShiftId?id="+entity.id,
            }).then(function(response){
                    $scope.gridOptions.data=response.data;
            })
        }
        vm.clear = clear;
        vm.save = save;
        $scope.remove=remove;
        vm.showButton=true;
        $scope.shiftDetail={};
        $scope.shiftDetailEidt={};
        //选中的行
        $scope.selectedRow={};
        //增加一行
        $scope.addData=addData;
        //编辑一行
        $scope.edit=edit;
        function addData() {
            var modalInstance = $uibModal.open({
                templateUrl : 'modal.html',//script标签中定义的id
                controller : 'modalCtrl',//modal对应的Controller
                resolve : {
                    data : function() {//data作为modal的controller传入的参数
                        return null;//用于传递数据
                    }
                }
            });
            modalInstance.result.then(function (shiftDetail) {
                $scope.newShifDetail = shiftDetail;
                $scope.gridOptions.data.push($scope.newShifDetail);
                $scope.newShifDetail={}
            }, function () {});
        }
        //编辑行
        function edit() {
            var modalInstanceEidt = $uibModal.open({
                templateUrl : 'modal.html',//script标签中定义的id
                controller : 'modalCtrl',//modal对应的Controller
                resolve : {
                    data : function() {//data作为modal的controller传入的参数
                        return $scope.selectedRow;//用于传递数据
                    }
                }
            });
            modalInstanceEidt.result.then(function (shiftDetail) {
                $scope.shiftDetailEidt = shiftDetail;
                var index=searchElement($scope.gridOptions.data,shiftDetail.$$hashKey);
                $scope.gridOptions.data.splice(index,1,$scope.shiftDetailEidt);
            }, function () {});
        }
        //删除行
        function remove() {
            var index=searchElement($scope.gridOptions.data,$scope.selectedRow.$$hashKey);
            $scope.gridOptions.data.splice(index,1);
            vm.showButton=true;
        }
        //从data中遍历查找hashKey，并删除
        function searchElement(data,hashKey) {
            var flag=true;
            var result=0;
            angular.forEach(data,function (value,index) {
                if(angular.equals(value.$$hashKey.toString(),hashKey) && flag){
                    flag=false;
                    result=index;
                }
            });
            return result;
        }
        //init detail page height
        function detailInit() {
            var height=250;
            angular.element("#detailGrid").css("height",height+"px");
        }
        $scope.gridOptions = {
            columnDefs: [
                { field: 'name',enableCellEdit:false},
                { field: 'contentType',enableCellEdit:false},
                { field: 'startTime',enableCellEdit:false,type:"date", cellFilter: 'date:\'HH:mm:ss\''},
                { field: 'endTime',enableCellEdit:false,type:"date", cellFilter: 'date:\'HH:mm:ss\''},
                { field : 'countTime',enableCellEdit:false},
                { field: 'description',enableCellEdit:true}
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
                        }if(condition==""||condition==null||angular.element(condition).isUndefined){
                            getPage(newPage,pageSize,null,null);
                        }
                    }
                });
                //行选中事件
                $scope.gridApi.selection.on.rowSelectionChanged($scope, function(row, event){
                    /*通过选中的行数来控制当前按钮的状态,1行为显示,多行隐藏*/
                    if(row){
                       /* vm.showButton= gridApi.selection.getSelectedCount()==1?false:true;*/
                        var selectedRows=gridApi.selection.getSelectedGridRows();
                        vm.showButton=selectedRows.length==1?false:true;
                        if(selectedRows!="" &&selectedRows.length>0){
                            $scope.selectedRow=selectedRows[0].entity;
                        }if(selectedRows.length==0){
                            $scope.selectedRow={};
                        }
                    }
                });
            }
        }
        /*获取表单的公共属性*/
        $.extend(true, $scope.gridOptions, TableOption.getTableOption());
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
        function save () {
            vm.shift.operatorShiftDetails=[]
            if ($scope.editForm.$valid) {
                vm.isSaving = true;
                console.log($scope.gridOptions.data);
               for(var i=0;i<$scope.gridOptions.data.length;i++){
                   vm.shift.operatorShiftDetails.push($scope.gridOptions.data[i]);
               }
                if(vm.shift.id!=null){
                    $http({
                        method  : 'put',
                        url     : '/api/shifts',
                        params :{id:vm.shift.id,name:vm.shift.name,description:vm.shift.description,active:vm.shift.active,operatorShiftDetails:vm.shift.operatorShiftDetails}  // 传递数据作为字符串，从前台传到后台
                    }).success(function(data) {//这里的data，就是后台传递过来的数据jsonArray
                        $uibModalInstance.close(data);
                    }).error(function(data,status,headers,config){
                        alert("error");
                    });
                }
                if(vm.shift.id==null){
                    $http({
                        method  : 'post',
                        url     : '/api/shifts',
                        params :{name:vm.shift.name,description:vm.shift.description,active:vm.shift.active,operatorShiftDetails:vm.shift.operatorShiftDetails}  // 传递数据作为字符串，从前台传到后台
                    }).success(function(data) {//这里的data，就是后台传递过来的数据jsonArray
                        $uibModalInstance.close(data);
                    }).error(function(data,status,headers,config){
                        alert("error");
                    });
                }
            } else {
                $scope.editForm.submitted = true;
            }
        }
        function onSaveSuccess (result) {
            $scope.$emit('bekoproApp:palletUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }
        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
//模态框对应的Controller
app.controller('modalCtrl', function($scope, $uibModalInstance, data,toastr) {
    $scope.shiftDetail= data;
    if( $scope.shiftDetail!=null &&angular.isDefined($scope.shiftDetail)){
        $scope.shiftDetail.startTime= new Date("1970-01-01 "+$scope.shiftDetail.startTime);
        $scope.shiftDetail.endTime= new Date("1970-01-01 "+$scope.shiftDetail.endTime);
    }
    $scope.types=[{value:"workTime"},{value:"breakTime"}]
    $scope.shiftType=[{value:"morningShift"},{value:"middleShift"},{value:"eveningShift"}]
    $scope.reasons=[];
    //在这里处理要进行的操作
    $scope.ok = function() {
        var countTime =$("#countTime").val();
        //剔除晚班跨域24点的情况
        if($scope.shiftDetail.name=='eveningShift' && $scope.shiftDetail.contentType=='workTime'){
            //表明跨域了24点(00:00)
            if($scope.shiftDetail.endTime<$scope.shiftDetail.startTime){
                //把时间转换成字符串
                $scope.shiftDetail.startTime=formatDateTime($scope.shiftDetail.startTime);
                $scope.shiftDetail.endTime=formatDateTime($scope.shiftDetail.endTime);
                var firstVal=countMiniuateTime($scope.shiftDetail.startTime,"24:00");
                var secondVal=countMiniuateTime("00:00",$scope.shiftDetail.endTime);
                countTime=firstVal+secondVal;
                $scope.shiftDetail.countTime=countTime;
            }else{
                $scope.shiftDetail.countTime=$("#countTime").val();
            }
        }else{
            //获取统计时间,判断其他shift时的情况
            $scope.shiftDetail.countTime=$("#countTime").val();
            $scope.shiftDetail.startTime=formatDateTime($scope.shiftDetail.startTime);
            $scope.shiftDetail.endTime=formatDateTime($scope.shiftDetail.endTime);
        }
        if(countTime==null||countTime=="0"||countTime<=0){
            toastr.error('<span data-translate="login.form.rememberme" class="text">invalid time config</span>')
            return;
        }
        $uibModalInstance.close($scope.shiftDetail);
        $scope.shiftDetail={}
    };
    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    }
    var formatDateTime = function (date) {
        var h = date.getHours();
        var minute = date.getMinutes();
        minute = minute < 10 ? ('0' + minute) : minute;
        return h+':'+minute;
    };
    function  countMiniuateTime(startTime,endTime) {
        var endTimeStr=endTime.split(":");
        var startTimeStr=startTime.split(":");
        //判断首位是否包含0
        if(endTimeStr[0].indexOf("0")==0){
            endTimeStr[0]=endTimeStr[0].substr(1);
        }
        if(endTimeStr[1].indexOf("0")==0){
            endTimeStr[1]=endTimeStr[1].substr(1);
        }
        if(startTimeStr[0].indexOf("0")==0){
            startTimeStr[0]=startTimeStr[0].substr(1);
        }
        if(startTimeStr[1].indexOf("0")==0){
            startTimeStr[1]=startTimeStr[1].substr(1);
        }
        var endTimeCount=parseInt(endTimeStr[0] * 60)+parseInt(endTimeStr[1]);
        var startTimeCount=parseInt(startTimeStr[0] * 60)+parseInt(startTimeStr[1]);
        return endTimeCount-startTimeCount;
    }
})