package com.mj.beko.opcualistener.first;

import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.TestStationData;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.PalletService;
import com.mj.beko.service.TestStationDataService;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author wanghb
 * 打螺丝工位监听器
 */
@Component
@Slf4j
public class ScrewStationLeftListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrewStationLeftListener.class);

    private static final String SCREW1_LEAVE = "\"information\".\"Screwstation1_leave\"";
    private static final String SCREW2_LEAVE = "\"information\".\"Screwstation2_leave\"";
    private static final String SCREW3_LEAVE = "\"information\".\"Screwstation3_leave\"";
    private static final String SCREW4_LEAVE = "\"information\".\"Screwstation4_leave\"";
    private static final String SCREW5_LEAVE = "\"information\".\"Screwstation5_leave\"";

    private static final String SCREW1_ID = "\"information\".\"Screwstation1_ID\"";
    private static final String SCREW2_ID = "\"information\".\"Screwstation2_ID\"";
    private static final String SCREW3_ID = "\"information\".\"Screwstation3_ID\"";
    private static final String SCREW4_ID = "\"information\".\"Screwstation4_ID\"";
    private static final String SCREW5_ID = "\"information\".\"Screwstation5_ID\"";

    private static final String SCREW1_EMPTY = "\"information\".\"Screwstation1_empty\"";
    private static final String SCREW2_EMPTY = "\"information\".\"Screwstation2_empty\"";
    private static final String SCREW3_EMPTY = "\"information\".\"Screwstation3_empty\"";
    private static final String SCREW4_EMPTY = "\"information\".\"Screwstation4_empty\"";
    private static final String SCREW5_EMPTY = "\"information\".\"Screwstation5_empty\"";

    private static final String SCREW1_DATA = "\"information\".\"Screwstation1_Torque_angles\"";
    private static final String SCREW2_DATA = "\"information\".\"Screwstation2_Torque_angles\"";
    private static final String SCREW3_DATA = "\"information\".\"Screwstation3_Torque_angles\"";
    private static final String SCREW4_DATA = "\"information\".\"Screwstation4_Torque_angles\"";
    private static final String SCREW5_DATA = "\"information\".\"Screwstation5_Torque_angles\"";

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private PalletService palletService;

    @Inject
    private TestStationDataService testStationDataService;

    @Inject
    private OrderService orderService;

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        switch (node) {
            case SCREW1_LEAVE:
                log.info("screw01 start do something when left");
                doThingWhenLeft(SCREW1_ID, SCREW1_DATA, SCREW1_EMPTY, "Screw1");
                break;
            case SCREW2_LEAVE:
                log.info("screw02 start do something when left");
                doThingWhenLeft(SCREW2_ID, SCREW2_DATA, SCREW2_EMPTY, "Screw2");
                break;
            case SCREW3_LEAVE:
                log.info("screw03 start do something when left");
                doThingWhenLeft(SCREW3_ID, SCREW3_DATA, SCREW3_EMPTY, "Screw3");
                break;
            case SCREW4_LEAVE:
                log.info("screw04 start do something when left");
                doThingWhenLeft(SCREW4_ID, SCREW4_DATA, SCREW4_EMPTY, "Screw4");
                break;
            case SCREW5_LEAVE:
                log.info("screw05 start do something when left");
                doThingWhenLeft(SCREW5_ID, SCREW5_DATA, SCREW5_EMPTY, "Screw5");
                break;
            default:
                break;
        }
    }

    private void doThingWhenLeft(String rfidNode, String dataNode, String empNode, String stationName){
        //获取托盘号
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
        NodeId rfidPallet = new NodeId(3, rfidNode);
        String palletNo;
        try {
            Variant readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, rfidPallet);
            palletNo = ((UnsignedByte[])readNodeVariant.getValue())[0].intValue() + "";
        } catch (OpcUaClientException e) {
            System.out.println("********************没有读到RFID信息*************************");
            e.printStackTrace();
            return;
        }
        log.info("{} get PalletNo is{}",stationName,palletNo);
        //如果是空托盘，直接放行
        if (!redisTemplate.hasKey(palletNo)) return;
        try {
            HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
            String bottomPlaceCode = cacheDatas.get(palletNo, "bottomPlateBarcode");
            String currentOrderNo = cacheDatas.get(palletNo, "currentOrderNo");
            String currentProductNo = cacheDatas.get(palletNo, "currentProductNo");
            //读取光电信号，如果为空托盘，坏件数量加1，并清除缓存和绑定的数据；否则读取打螺丝的结果、扭力和扭矩
//            NodeId emptyNode = new NodeId(3, empNode);
//            boolean emptyOrNot = opcUaClientTemplate.readNodeVariant(uaClient, emptyNode).booleanValue();
//            if (!emptyOrNot) {
//                //根据orderNo修改坏件数量
//                orderService.updateBrokenNumByOrderNo(currentOrderNo);
//                //删除缓存数据
//                redisTemplate.delete(palletNo);
//                //清除托盘绑定的数据
//                palletService.clearPalletData(palletNo);
//                return;
//            }
            NodeId screwData = new NodeId(3, dataNode);
            Variant variant = opcUaClientTemplate.readNodeVariant(uaClient, screwData);
            UnsignedInteger[] datas = (UnsignedInteger[])(variant.getValue());
            //保存第一个螺丝的数据
            TestStationData testStationData = new TestStationData();
            testStationData.setBarCode(bottomPlaceCode);
            testStationData.setContentType(stationName);
            testStationData.setCreateTime(Timestamp.from(Instant.now()));
            testStationData.setOrderNo(currentOrderNo);
            testStationData.setProductNo(currentProductNo);
            testStationData.setStep("Step1");
            int res1 = datas[0].intValue();
            if (res1 == 0) {
                testStationData.setResult("OK");
            } else {
                testStationData.setResult("NOK");
            }
            String torque = String.format("%.2f", byte2float(intToBytes(datas[1].intValue())));
            String angle = String.format("%.2f", byte2float(intToBytes(datas[2].intValue())));
            log.info("get first screw data,{},{},",torque,angle);
            testStationData.setValue(torque + "N/s|" + angle + "°");
            testStationDataService.save(testStationData);
            //保存第二个螺丝的数据
            TestStationData testStationData2 = new TestStationData();
            testStationData2.setBarCode(bottomPlaceCode);
            testStationData2.setContentType(stationName);
            testStationData2.setCreateTime(Timestamp.from(Instant.now()));
            testStationData2.setOrderNo(currentOrderNo);
            testStationData2.setProductNo(currentProductNo);
            testStationData2.setStep("Step2");
            int res2 = datas[3].intValue();
            if (res2 == 0) {
                testStationData2.setResult("OK");
            } else {
                testStationData2.setResult("NOK");
            }
            String torque2 = String.format("%.2f", byte2float(intToBytes(datas[4].intValue())));
            String angle2 = String.format("%.2f", byte2float(intToBytes(datas[5].intValue())));
            log.info("get second screw data,{},{},",torque2,angle2);
            testStationData2.setValue(torque2 + "N/s|" + angle2 + "°");
            testStationDataService.save(testStationData2);
        } catch (OpcUaClientException e) {
            LOGGER.error("da");
            e.printStackTrace();
        }
    }

    //将整形转换为byte数组
    public byte[] intToBytes( int value ) {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    //将byte数组转换为float
    public float byte2float(byte[] b) {
        int l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        return Float.intBitsToFloat(l);
    }
}