package com.mj.beko.opcualistener;

import com.mj.beko.opcua.OpcUaClientConnectionListener;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaSubscribeNodes;
import com.mj.beko.opcualistener.first.*;
import com.mj.beko.opcualistener.second.BurnerSupportAndCapArriveListener;
import com.mj.beko.opcualistener.second.BurnerSupportAndCapLackOfMaterialListener;
import com.mj.beko.opcualistener.second.BurnerSupportAndCapLeftListener;
import com.mj.beko.opcualistener.third.*;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanghb
 */
@Component
public class OpcUaConnectionListener implements OpcUaClientConnectionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpcUaConnectionListener.class);

    private static final Map<String, MonitoredDataItemListener> LISTENER_MAP = new ConcurrentHashMap<>();

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private OpcUaSubscribeNodes opcUaSubscribeNodes;

    /** 一段PLC **/
    @Inject
    private BottomPlateArriveAndLeftListener bottomPlateArriveAndLeftListener;

    @Inject
    private TopPlateArriveAndLeftListener topPlateArriveAndLeftListener;

    @Inject
    private ScrewStationLeftListener screwStationLeftListener;

    @Inject
    private AirtightAndFluxAndElectricArriveListener airtightAndFluxAndElectricArriveListener;

    @Inject
    private AirtightAndFluxAndElectricLeftListener airtightAndFluxAndElectricLeftListener;

    @Inject
    private Repair1ArriveAndLeftListener repair1ArriveAndLeftListener;

    /** 二段PLC **/
    @Inject
    private BurnerSupportAndCapLackOfMaterialListener burnerSupportAndCapLackOfMaterialListener;

    @Inject
    private BurnerSupportAndCapArriveListener burnerSupportAndCapArriveListener;

    @Inject
    private BurnerSupportAndCapLeftListener burnerSupportAndCapLeftListener;

    //由于节点不够用，改成轮询操作
//    @Inject
//    private KnobsBoxLackOfMaterialAndArriveListener knobsBoxLackOfMaterialAndArriveListener;

    /** 三段PLC **/
    @Inject
    private FireAndVisionAndRemoveGasArriveListener fireAndVisionAndRemoveGasArriveListener;

    @Inject
    private FireAndVisionAndRemoveGasLeftListener fireAndVisionAndRemoveGasLeftListener;

    @Inject
    private Repair2ArriveAndLeftListener repair2ArriveAndLeftListener;

//    @Inject
//    private EPSArriveListener epsArriveListener;

    @Inject
    private EPSLackOfMaterialListener epsLackOfMaterialListener;

    @Inject
    private UpEpsPutDownFinishedListener upEpsPutDownFinishedListener;
    @Inject
    private ProductPutFinishedListener productPutFinishedListener;
    @Inject
    private ReadPrinterLabelListener readPrinterLabelListener;
    @Inject
    private PackagingLabelMatchListener packagingLabelMatchListener;

    @Inject
    private LastPrinterStartListener lastPrinterStartListener;
    @Autowired
    private RobotPlaceProductDownListener robotPlaceProductDownListener;

    @Override
    public void onConnected(int plcNo, UaClient uaClient) {
        LOGGER.info("---------->>>>> opcua client connect success <<<<<-----------");
        //将PLC Client与对于的节点监听集合绑定到一起，用于相应PLC的订阅
        Map<MonitoredDataItemListener, List<String>> listenerMap = new HashMap<>();
        switch (plcNo) {
            case 0:
                listenerMap.put(bottomPlateArriveAndLeftListener, opcUaSubscribeNodes.getBottomPlateArriveAndLeft());
                listenerMap.put(topPlateArriveAndLeftListener, opcUaSubscribeNodes.getTopPlateArriveAndLeft());
                listenerMap.put(screwStationLeftListener, opcUaSubscribeNodes.getScrewStationLeft());
                listenerMap.put(airtightAndFluxAndElectricArriveListener, opcUaSubscribeNodes.getAirtightAndFluxAndElectricArrive());
                listenerMap.put(airtightAndFluxAndElectricLeftListener, opcUaSubscribeNodes.getAirtightAndFluxAndElectricLeft());
                listenerMap.put(repair1ArriveAndLeftListener, opcUaSubscribeNodes.getRepair1ArriveAndLeft());
                break;
            case 1:
                listenerMap.put(burnerSupportAndCapLackOfMaterialListener, opcUaSubscribeNodes.getBurnerSupportAndCapLackOfMaterial());
                listenerMap.put(burnerSupportAndCapArriveListener, opcUaSubscribeNodes.getBurnerSupportAndCapArrive());
                listenerMap.put(burnerSupportAndCapLeftListener, opcUaSubscribeNodes.getBurnerSupportAndCapLeft());
//                listenerMap.put(knobsBoxLackOfMaterialAndArriveListener, opcUaSubscribeNodes.getKnobsBoxLackOfMaterialAndArrive());
                break;
            case 2:
                listenerMap.put(fireAndVisionAndRemoveGasArriveListener, opcUaSubscribeNodes.getFireAndVisionAndRemoveGasArrive());
                listenerMap.put(fireAndVisionAndRemoveGasLeftListener, opcUaSubscribeNodes.getFireAndVisionAndRemoveGasLeft());
//                listenerMap.put(epsArriveListener, opcUaSubscribeNodes.getEpsArrive());
                listenerMap.put(epsLackOfMaterialListener, opcUaSubscribeNodes.getEpsLackOfMaterial());
                listenerMap.put(repair2ArriveAndLeftListener, opcUaSubscribeNodes.getRepair2ArriveAndLeft());
                listenerMap.put(upEpsPutDownFinishedListener, opcUaSubscribeNodes.getUpEpsPutDownFinished());
                listenerMap.put(lastPrinterStartListener, opcUaSubscribeNodes.getLastPrinterStart());
                //把产品放置完成的listener放入到map中
                listenerMap.put(productPutFinishedListener,opcUaSubscribeNodes.getProductPutFinished());
                listenerMap.put(robotPlaceProductDownListener,opcUaSubscribeNodes.getRobotPlaceProductDown());
                listenerMap.put(readPrinterLabelListener,opcUaSubscribeNodes.getReadPrinterLabel());//读取贴标机条码信号
                listenerMap.put(packagingLabelMatchListener,opcUaSubscribeNodes.getPackagingLabelMatch());//读取34贴标的信号
                break;
            default:
                LOGGER.error("************PLC个数设置错误*****************");
                break;
        }
        subscribeNodesValue(uaClient, listenerMap);
    }

    private synchronized void subscribeNodesValue(UaClient uaClient, Map<MonitoredDataItemListener, List<String>> map) {
        try {
            for (Map.Entry<MonitoredDataItemListener, List<String>> entry : map.entrySet()) {
                MonitoredDataItemListener listener = entry.getKey();
                List<String> strList = entry.getValue();
                for (String nodeIdStr : strList) {
                    if (LISTENER_MAP.containsKey(nodeIdStr + ":" + listener.getClass().toString())) {
                        return;
                    }
                    LOGGER.debug("add listener:{}", nodeIdStr + ":" + listener.getClass().toString());
                    opcUaClientTemplate.subscribeNodeValue(uaClient, new NodeId(3, nodeIdStr), listener);
                    LISTENER_MAP.put(nodeIdStr + ":" + listener.getClass().toString(), listener);
                }
            }
        } catch (OpcUaClientException e) {
            LOGGER.error("OpcUa Client Exception when subscribeNodesValue", e);
        }
    }
}
