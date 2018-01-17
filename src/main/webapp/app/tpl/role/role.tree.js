/**
 * Created by Ricardo on 2017/11/4.
 */
$('#tree').data('jstree', false);//清空数据，必须
var data=[
    {
        'text' : 'All Menu',
        'state' : {
            'opened' :true,
            'selected' : false
        },
        'children' : [
            { 'id':'/product','text' : 'ProductManagement'},
            { 'id':'/orders','text' : 'OrderManagement'},
            {'id':'/pallets','text':'Pallets'},
            {'id':'/operation','text':'Operation'},
            { 'id':'/workstation','text' : 'Workstation'},
            { 'id':'/orderUpdateLog','text' : 'OrderUpdateLog'},
            {'id':'/orderProduction','text':'OrderProduction'},
            {'id':'/role','text':'Role'},
            { 'id':'/user','text' : 'User'},
            { 'id':'/tcs-order','text' : 'TcsOrder'},
            {'id':'/production-data','text':'ProductionData'},
            {'id':'/operator-data','text':'OperatorData'},
            { 'id':'/productResourceFile','text' : 'ProductResource'},
            { 'id':'/productRepair','text' : 'ProductRepair'},
            {'id':'/productCode','text':'ProductCode'}
        ]}
        ];
$('#tree').jstree({
    'plugins' : [ "checkbox" ], //出现选择框
    'checkbox': { cascade: "", three_state: true }, //不级联
    'core': {
        'data': data,
        "themes": {
            "responsive": false
        }
    }
}).bind('loaded.jstree', loadedfunction);
//树控件的变化事件处理
$('#tree').on("changed.jstree", function (e, data) {
    console.dir(data);

});
function loadedfunction(data){
    console.dir("data"+data);
}
