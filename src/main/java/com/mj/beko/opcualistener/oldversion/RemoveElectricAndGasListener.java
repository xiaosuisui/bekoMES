package com.mj.beko.opcualistener.oldversion;

import com.mj.beko.domain.Pallet;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.service.PalletService;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author wanghb
 * 拔线和拔气管工位监听器
 */
@Component
public class RemoveElectricAndGasListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveElectricAndGasListener.class);

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Inject
    private PalletService palletService;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        if (!("BekoOpcua.RemoveElectricAndGas.arrive".equals(node) && newValue.getValue().intValue() == 1)) return;
        try {
            //从RFID中读取托盘信息
            NodeId rfidPallet = new NodeId(2, "BekoOpcua.RemoveElectricAndGas.rfidPallet");
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
            Variant readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, rfidPallet);
            String palletNo = (String) readNodeVariant.getValue();

            //根据托盘号获取下底盘条码并放入到队列中,并保存到缓存中
            Pallet pallet = palletService.findPalletByPalletNo(palletNo);
            String bottomPlaceCode = pallet.getBottomPlaceCode();
            ValueOperations<String, Object> bottomBarcodeOperations = redisTemplate.opsForValue();
            if (bottomBarcodeOperations.get("bottomPlateBarcodeQueue") == null ||
                bottomBarcodeOperations.get("barcodeForPrinterQueue") == null) {
                bottomBarcodeOperations.set("bottomPlateBarcodeQueue", new LinkedList<String>());
                bottomBarcodeOperations.set("barcodeForPrinterQueue", new LinkedList<String>());
            }
            Queue<String> bottomPlateBarcodeQueue = (Queue<String>) bottomBarcodeOperations.get("bottomPlateBarcodeQueue");
            Queue<String> barcodeForPrinterQueue = (Queue<String>) bottomBarcodeOperations.get("barcodeForPrinterQueue");
            if (!bottomPlateBarcodeQueue.contains(bottomPlaceCode)
                && !barcodeForPrinterQueue.contains(bottomPlaceCode)) {
                bottomPlateBarcodeQueue.offer(bottomPlaceCode);
                barcodeForPrinterQueue.offer(bottomPlaceCode);

                bottomBarcodeOperations.set("bottomPlateBarcodeQueue", bottomPlateBarcodeQueue);
                bottomBarcodeOperations.set("barcodeForPrinterQueue", barcodeForPrinterQueue);
            }
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
    }
}
