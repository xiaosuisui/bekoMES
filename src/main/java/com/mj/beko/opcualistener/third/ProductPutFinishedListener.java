package com.mj.beko.opcualistener.third;
import com.mj.beko.codeScanner.GetBarcode;
import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.ProductCode;
import com.mj.beko.domain.StationCycleTime;
import com.mj.beko.domain.dto.PrintLabelDto;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.service.PalletService;
import com.mj.beko.service.ProductCodeService;
import com.mj.beko.service.StationCycleTimeService;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Ricardo on 2018/1/6.
 * 产品放置完成信号
 */
@Slf4j
@Component
public class ProductPutFinishedListener implements MonitoredDataItemListener {
    @Inject
    private TaskExecutor taskExecutor;
    @Inject
    private GetBarcode getBarcode;
    @Inject
    private SimpMessagingTemplate template;
    @Inject
    private PalletService palletService;
    @Inject
    private HttpTemplate  httpTemplate;
    @Inject
    private ProductCodeService productCodeService;
    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;
    @Inject
    private RedisTemplate redisTemplate;
    @Autowired
    private StationCycleTimeService stationCycleTimeService;

    private static final String PRINTLABEL = "/GasAutomationApi/api/Product/PrintProductLabel";
    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        log.info("come into robot catch product information,");
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        //机器人抓取产品后,调用扫描枪，获取下底盘条码
        taskExecutor.execute(() -> callBarCodeForBottomPlateBarCode());
    }
    //调用扫描枪,获取下底盘条码
    @Async("taskExecutor")
    public void callBarCodeForBottomPlateBarCode(){
        Map<String,String> map =new HashMap<String,String>();
        Map<String,String> infoMap =new HashMap<String,String>();
        String barcode = getBarcode.getBottomPlateBarCode();
        //机器人抓取产品,下底盘条码扫到的为空(扫描到的下底盘条码错误)
        if(barcode==null||barcode.length()<18){
            log.info("get bottomPlateBarCode failure");
            infoMap.put("result","printerError");
            infoMap.put("type","1");
            infoMap.put("reason","cant get barCode，value is"+barcode);
            template.convertAndSend("/topic/lineLeaderScreen/printerError",infoMap);
            return;
        }else if(barcode!=null &&barcode.length()==18){
            log.info("robot catch product,get bottomPlateBarCode,value is {}",barcode);
            //扫到条码后判断系统中有没有该条码的信息
            ProductCode productCode = productCodeService.getProductCodeByBottomPlateBarCode(barcode);
            //如果查到系统中已经生成该条码的信息,则不在请求打印机的Api(此按钮不需要处理,only tips)
            if(productCode!=null){
                infoMap.put("result","printerError");
                infoMap.put("type","4");
                infoMap.put("reason","bottomPlateCode already exist,value is,"+barcode);
                template.convertAndSend("/topic/lineLeaderScreen/printerError",infoMap);
                return;
            }
            //查询下底盘条码对应的产品信息(productNo和orderNo)
            Pallet pallet=palletService.getPalletByBottomPlateCode(barcode);
           //if cant get pallet info, 则从 工位的cycleTime表中查询
            if(pallet==null){
                List<StationCycleTime> stationCycleTimes =stationCycleTimeService.getOneStationCycleTimeByBottomPlateStation(barcode);
                //如果工位时间表也实在查不到了。(only tips)
                if(stationCycleTimes==null ||stationCycleTimes.size()==0){
                    log.info("IMes system cant search this bottomPlateInforMation,please check data");
                    infoMap.put("result","printerError");
                    infoMap.put("type","2");
                    infoMap.put("reason","MES system can not find product match bottomPlate infomation,value is"+barcode);
                    template.convertAndSend("/topic/lineLeaderScreen/printerError",infoMap);
                    return;
                }else if(stationCycleTimes!=null&&stationCycleTimes.size()>0){
                    //通过下底盘条码查询产品信息,获取productNo
                    String productNo=stationCycleTimes.get(0).getProductNo();
                    //获取 OrderNo
                     String orderNo=stationCycleTimes.get(0).getOrderNo();
                    log.info("search productNo,orderNo infomation from stationcycletime for printer,value is,{},{}",productNo,orderNo);
                    //如果查不到productId信息
                    if(productNo==null||"".equals(productNo)){
                        infoMap.put("result","printerError");
                        infoMap.put("type","2");
                        infoMap.put("reason","MES system can not find product match bottomPlate infomation,value is"+barcode);
                        template.convertAndSend("/topic/lineLeaderScreen/printerError",infoMap);
                        return;
                    }
                    //如果从工位时间表中能找到产品信息,则推送信息到打印机printer01
                     String palletNo="0";
                    taskExecutor.execute(() -> printer1(palletNo,productNo,orderNo,barcode));
                }
            }else if(pallet!=null){
                //从托盘信息中查找productNo
                String productNo=pallet.getProductNo();
                //从托盘信息中查找orderNo
                String orderNo=pallet.getCurrentOrderNo();
                //获取对应的托盘号
                String palletNo=pallet.getPalletNo();
                if(productNo==null||"".equals(productNo)){
                    infoMap.put("result","printerError");
                    infoMap.put("type","2");
                    infoMap.put("reason","MES system can not find product match bottomPlate infomation,value is "+barcode);
                    template.convertAndSend("/topic/lineLeaderScreen/printerError",infoMap);
                    return;
                }
                //则给打印机发送打印的指令
                taskExecutor.execute(() -> printer1(palletNo,productNo,orderNo,barcode));
            }
        }
    }
    //调用打印机
    public void printer1(String palletNo,String productNo, String orderNo, String bottomPlateBarcode) {
        Map<String,String> infoMap =new HashMap<String,String>();
        log.info("try to make a request to printer01,productNo is,{},orderNo is,{},bottomPlateCode is,{},palletNo is,{}",productNo,orderNo,bottomPlateBarcode,palletNo);
        PrintLabelDto printLabelDto = new PrintLabelDto();
        printLabelDto.setProductNo(productNo);
        printLabelDto.setLine("110");
        printLabelDto.setTagType(4);
        printLabelDto.setQuantity(1);
        printLabelDto.setPrinter(1);
        printLabelDto.setSerial("");
        printLabelDto.setOrder("12345678");
        String serial = "";
        ResponseEntity<String> responseEntity;
        int i = 0;
        while ("".equals(serial) || (i < 6 && serial.length() > 20)) {
            responseEntity = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                    + PRINTLABEL, printLabelDto, String.class);
            serial = responseEntity.getBody().replace("\"", "");
            log.info("******************第"+i+"次 get serialNumber printer01：" + serial);
            log.info("printer01:bottomPlateCode match serialNumber,the code value is{},serialNumber is{}",bottomPlateBarcode,serial);
            try {
                //延迟500ms调用
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.info("request to printer server error,error productNo orderNo,",productNo,orderNo);
                e.printStackTrace();
            }
            i++;
        }
        //如果6次调用失败
        if (serial.length() > 20) {
            //推送消息到前台
            log.info("call printer01 Api failure params is,{},{},{},return serial is{}", productNo, orderNo, bottomPlateBarcode,serial);
            infoMap.put("result","printerError");
            infoMap.put("type","3");
            infoMap.put("value",productNo+","+orderNo+","+bottomPlateBarcode);
            infoMap.put("reason","call printer01Api Error,BottomPlateCode is,"+bottomPlateBarcode+",serial is "+serial);
            //调用6次还没有返回正确的serialNumber，推送信息到前台
            template.convertAndSend("/topic/lineLeaderScreen/printerError", infoMap);
            return;
        }
        //如果生成了序列号，首先从数据库中查询该下底盘条码有没有
        ProductCode productCode = productCodeService.getProductCodeByBottomPlateBarCode(bottomPlateBarcode);
        //系统不存在相同的下底盘条码时,创建一条空的记录，保存下底盘条码和序列号，并且此刻的状态为0,同时把扫到的下底盘条码保存到缓存中
        if (productCode == null) {
            productCode = new ProductCode();
            productCode.setCreateDate(Timestamp.from(Instant.now()));
            productCode.setOrderNo(orderNo);
            productCode.setProductNo(productNo);
            productCode.setProductCode(bottomPlateBarcode); //下底盘条码
            productCode.setStatus("0");
            productCode.setSerialNo(serial);
            productCodeService.save(productCode);
            //把下底盘条码和对应的序列号保存到缓存中,并推送信息到前台
            ValueOperations hashOperations=redisTemplate.opsForValue();
            hashOperations.set("bottomPlateBarCode",bottomPlateBarcode);
            hashOperations.set("currentSerialNumber",serial);
            Map<String,String> map =new HashMap<String,String>();
            //把当前的产品的信息和对应的序列号推送到前台
            map.put("bottomPlateCode",bottomPlateBarcode);
            map.put("currentSerialNumber",serial);
            template.convertAndSend("/topic/lineLeaderScreen/productCodeAndSerialInfo",map);
            //给机器人写一个信号,hi哥们你可以继续工作了。
            //推送完信息后给机器人信号,哥们已经扫描完毕。可以继续工作了
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
            log.info("start write node value to plc,scanner Complete,{},{}",bottomPlateBarcode,serial);
            NodeId printer01Node = new NodeId(3, "\"ITread\".\"Scanned_completed\"");
            try {
                //如果发成功了,则给plc信号,hi 你可以抓取了。往节点写1值
                //写2次
                boolean flag1 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
                if(!flag1){
                    log.info("write second write node value to plc,scanner Complate,{}",serial);
                    boolean flag2 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
                }
                log.info("write successful write node value to plc,scanner Complate,{}",serial);
                //写值成功后，调用clear pallet
                //清除托盘所有的数据
                redisTemplate.delete(palletNo);
                //清除托盘绑定的数据
                palletService.clearPalletData(palletNo);

            }catch (Exception e){
                log.error("get opcua failure,write sigual to printer01,{}",serial);
            }
        }else{
            infoMap.put("result","printerError");
            infoMap.put("type","4");
            infoMap.put("reason","bottomPlateCode already exist,value is,"+bottomPlateBarcode);
            template.convertAndSend("/topic/lineLeaderScreen/printerError",infoMap);
        }
    }
}
