package com.mj.beko.opcua;

import com.mj.beko.constants.OpcUaProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wanghb
 */
@Data
public class OpcUaSubscribeNodes {

    private List<String> bottomPlateArriveAndLeft;  //下底盘工位到位("为了安全采用RFID信号点")和放行监听点
    private List<String> topPlateArriveAndLeft;  //上底盘工位到位("为了安全采用RFID信号点")和放行监听点
    private List<String> screwStationLeft; //打螺丝工位
    private List<String> airtightAndFluxAndElectricArrive; //气密、流量和电测试的到位信号
    private List<String> airtightAndFluxAndElectricLeft; //气密、流量和电测试的放行信号
    private List<String> repair1ArriveAndLeft; //一段返修到位、放行

    private List<String> burnerSupportAndCapLackOfMaterial; //2段PLC机器人缺料信号
    private List<String> burnerSupportAndCapArrive;
    private List<String> burnerSupportAndCapLeft;
    //由于节点不够用，改成轮询操作
//    private List<String> knobsBoxLackOfMaterialAndArrive; //旋钮箱子缺料和到料信息

    private List<String> fireAndVisionAndRemoveGasArrive; //3段燃烧、视觉和拔气工位到位信号
    private List<String> fireAndVisionAndRemoveGasLeft; //3段燃烧、视觉和拔气工位放行信号
    private List<String> epsLackOfMaterial; //EPS缺料信号
//    private List<String> epsArrive; //EPS到位信号
    private List<String> repair2ArriveAndLeft; //一段返修到位、放行
    private List<String> lastStation; //最后一个工位产品下线信号
    private List<String> upEpsPutDownFinished;  //上泡沫放置完成信号
    private List<String> lastPrinterStart;  //最后两台打印机触发信号
    //机器人把上泡沫放置完成的信号
    private List<String> productPutFinished;
    //robot place product finished
    private List<String> robotPlaceProductDown;
    //读取贴标机条码的信号
    private List<String> readPrinterLabel;
    //读取贴标机0304的信号
    private  List<String> packagingLabelMatch;

    //将配置的opcUa节点列表解析成符合OPCUA节点规则的列表
    private List<String> getRealSubscribeNodesList (String node) {
        List<String> nodeList = new ArrayList<String>();
        if (node.contains("NodeBase")) {
            String[] machineNoList = new String[0];
            String[] list = node.split("-");
            String nodeBase = list[0].trim().substring(list[0].trim().indexOf("#") + 1).trim();
            if (node.contains("No#")) {
                machineNoList = list[1].trim().substring(list[1].trim().indexOf("#") + 1).split(",");
            }
            String[] subVarList = list[list.length-1].trim().substring(list[list.length - 1].trim().indexOf("#") + 1).split(",");
            if( machineNoList.length != 0 ) {
                for(String machineNo : machineNoList) {
                    for(String subvar : subVarList) {
                        nodeList.add(nodeBase + machineNo.trim() + "." + subvar.trim());
                    }
                }
            } else {
                for(String subvar : subVarList) {
                    nodeList.add(nodeBase + "." + subvar.trim());
                }
            }
        }
        return nodeList;
    }

    //将所有配置的OPCUA节点属性解析成符合OPCUA节点规范的列表
    public OpcUaSubscribeNodes(OpcUaProperties opcUaProperties) {
        List<Map<String, String>> plcList = opcUaProperties.getPlcList();
        this.bottomPlateArriveAndLeft = getRealSubscribeNodesList(plcList.get(0).get("BottomPlateArriveAndLeft"));
        this.topPlateArriveAndLeft = getRealSubscribeNodesList(plcList.get(0).get("TopPlateArriveAndLeft"));
        this.screwStationLeft = getRealSubscribeNodesList(plcList.get(0).get("ScrewStationLeft"));
        this.airtightAndFluxAndElectricArrive = getRealSubscribeNodesList(plcList.get(0).get("AirtightAndFluxAndElectricArrive"));
        this.airtightAndFluxAndElectricLeft = getRealSubscribeNodesList(plcList.get(0).get("AirtightAndFluxAndElectricLeft"));
        this.repair1ArriveAndLeft = getRealSubscribeNodesList(plcList.get(0).get("Repair1ArriveAndLeft"));

        this.burnerSupportAndCapLackOfMaterial = getRealSubscribeNodesList(plcList.get(1).get("BurnerSupportAndCapLackOfMaterial"));
        this.burnerSupportAndCapArrive = getRealSubscribeNodesList(plcList.get(1).get("BurnerSupportAndCapArrive"));
        this.burnerSupportAndCapLeft = getRealSubscribeNodesList(plcList.get(1).get("BurnerSupportAndCapLeft"));
//        this.knobsBoxLackOfMaterialAndArrive = getRealSubscribeNodesList(plcList.get(1).get("KnobsBoxLackOfMaterialAndArrive"));

        this.fireAndVisionAndRemoveGasArrive = getRealSubscribeNodesList(plcList.get(2).get("FireAndVisionAndRemoveGasArrive"));
        this.fireAndVisionAndRemoveGasLeft = getRealSubscribeNodesList(plcList.get(2).get("FireAndVisionAndRemoveGasLeft"));
        this.epsLackOfMaterial = getRealSubscribeNodesList(plcList.get(2).get("EPSLackOfMaterial"));
//        this.epsArrive = getRealSubscribeNodesList(plcList.get(2).get("EPSArrive"));
        this.repair2ArriveAndLeft = getRealSubscribeNodesList(plcList.get(2).get("Repair2ArriveAndLeft"));
        this.lastStation = getRealSubscribeNodesList(plcList.get(2).get("LastStation"));
        this.upEpsPutDownFinished = getRealSubscribeNodesList(plcList.get(2).get("UpEpsPutDownFinished"));
        this.lastPrinterStart = getRealSubscribeNodesList(plcList.get(2).get("LastPrinterStart"));
        //产品放置完成信号
        this.productPutFinished=getRealSubscribeNodesList(plcList.get(2).get("ProductPutFinished"));
        //robot place product finished
        this.robotPlaceProductDown=getRealSubscribeNodesList(plcList.get(2).get("RobotPlaceProductDown"));
        //读取贴标机条码的信号
        this.readPrinterLabel=getRealSubscribeNodesList(plcList.get(2).get("ReadPrinterLabel"));
        //读取3 4printer的信号
        this.packagingLabelMatch=getRealSubscribeNodesList(plcList.get(2).get("PackagingLabelMatch"));
    }
}
