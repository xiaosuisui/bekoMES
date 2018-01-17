/**
 * Created by xiaosui on 2017/7/13.
 */
(function() {
    'use strict';
    angular
        .module('bekoproApp')
        .factory('TableOption', TableOption);
    TableOption.$inject = ['$rootScope'];
    function TableOption($rootScope) {
        var service = {
          getTableOption:getTableOption
        };
        return service;
    }
    function getTableOption() {
        var options={
            enableSorting: true, //是否排序
            useExternalSorting: false, //是否使用自定义排序规则
            enableGridMenu: true, //是否显示grid 菜单
            showGridFooter: false, //是否显示grid footer
            enableHorizontalScrollbar :  1, //grid水平滚动条是否显示, 0-不显示  1-显示
            enableVerticalScrollbar : 1, //grid垂直滚动条是否显示, 0-不显示  1-显示
            //-------- 分页属性 ----------------
            enablePagination: true, //是否分页，默认为true
            enablePaginationControls: true, //使用默认的底部分页
            paginationPageSizes: [10,15,20,50,100], //每页显示个数可选项
            paginationCurrentPage:1, //当前页码
            paginationPageSize: 15, //每页显示个数
            totalItems : 0, // 总数量
            useExternalPagination: true,//是否使用分页按钮
            //----------- 选中 ----------------------
            enableFooterTotalSelected: true, // 是否显示选中的总数，默认为true, 如果显示，showGridFooter 必须为true
            enableFullRowSelection : true, //是否点击行任意位置后选中,默认为false,当为true时，checkbox可以显示但是不可选中
            enableRowHeaderSelection : true, //是否显示选中checkbox框 ,默认为true
            enableRowSelection : true, // 行选择是否可用，默认为true;
            enableSelectAll : true, // 选择所有checkbox是否可用，默认为true;
            enableSelectionBatchEvent : true, //默认true
            modifierKeysToMultiSelect: false ,//默认false,为true时只能 按ctrl或shift键进行多选, multiSelect 必须为true;
            multiSelect: true ,// 是否可以选择多个,默认为true;
            noUnselect: false,//默认false,选中后是否可以取消选中
            selectionRowHeaderWidth:30 ,//默认30 ，设置选择列的宽度
            enableCellEditOnFocus:true,
        };
        return options;
    }
})();

