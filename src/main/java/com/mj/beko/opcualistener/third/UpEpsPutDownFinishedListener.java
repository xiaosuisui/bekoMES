package com.mj.beko.opcualistener.third;

import com.mj.beko.codeScanner.GetBarcode;
import com.mj.beko.domain.ProductCode;
import com.mj.beko.domain.dto.PrintLabelDto;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.repository.ProductCodeRepository;
import com.mj.beko.service.ProductCodeService;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wanghb
 * 上泡沫放置完成，触发扫码枪
 */
@Slf4j
@Component
public class UpEpsPutDownFinishedListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpEpsPutDownFinishedListener.class);

    @Inject
    private TaskExecutor taskExecutor;

    @Inject
    private GetBarcode getBarcode;

    @Inject
    private ProductCodeService productCodeService;

    @Inject
    private ProductCodeRepository productCodeRepository;

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private HttpTemplate httpTemplate;
    @Inject
    private SimpMessagingTemplate template;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String PRINTLABEL = "/GasAutomationApi/api/Product/PrintProductLabel";

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        taskExecutor.execute(() -> doThing());
    }
  //上泡沫放置完成后的调用的方法,上泡沫放置完成的方法
    @Async("taskExecutor")
    public void doThing(){
        Map<String,String> infoMap =new HashMap<String,String>();
        String productId = "";
        //调用扫描枪,获取eps Barcode
        String barcode = getBarcode.getBarcode1();
        if (barcode == null) {
            log.info("cant get eps code");
            infoMap.put("result","epsError");
            infoMap.put("type","5");
            infoMap.put("reason","cant get eps code,value is"+barcode);
            //LOGGER.error("******************bar Code cant get epsCode first********************");
            //如果扫不到条码则返回,并推送消息到前台
            template.convertAndSend("/topic/lineLeaderScreen/matchError",infoMap);
            return;
        } else {
            //从eps中截取的productId
            productId = barcode.substring(0, 10);
        }
        log.info("up eps put finished, get eps code ,value is {}",barcode);
        //从productCode里查是否已经扫描过eps(查询该eps条码是否存在)barCode指EPS条码(查询系统中有没有改条形码的记录)
        ProductCode epsProductCodeEntity=productCodeService.getProductCodeByEpsCodeAndStatus(barcode);
         //表明此刻是扫到了，因为某种原因没有让他走,如果能查到eps的记录,表明第一次是扫到了，但是因为原因没走。
        //如果能查询到该eps条码的记录,则判断productId是否匹配。
       if(epsProductCodeEntity!=null){
           log.info("get epsProductCodeEntity,is{}",epsProductCodeEntity.toString());
            //首先判断没有走是不是因为不匹配
           //获取productCode中的productNo
            String epsProductNo=epsProductCodeEntity.getProductNo();
            //如果eps匹配当前生产的订单,则就让它go 如果eps扫描结果不为空
            if(epsProductNo!=null && productId.equals(epsProductNo)){
                log.info("here because of some error first dont down, let up ->down");
                //让阻挡下降放行
                UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
                //增加日志记录
                log.info("first get epsCode,work again, let zudang down,{},{},eps is{}",productId,epsProductNo,barcode);
                NodeId finishNode = new NodeId(3, "\"ITread\".\"Place_Read_Finish\"");
                try {
                    boolean result=opcUaClientTemplate.writeNodeValue(uaClient, finishNode, 1);
                    log.info("let zudang go go go go go,not normal");
                    if(!result){
                        boolean result1=opcUaClientTemplate.writeNodeValue(uaClient, finishNode, 1);
                    }
                } catch (OpcUaClientException e) {
                    LOGGER.info("scaner02 push button 。。productNo",epsProductNo);
                    e.printStackTrace();
                }
                //如果条码不匹配,则推出不匹配的信息。不放行，需要人工参与了
                //如果扫描截取的条码跟系统中的条码不一致,则推送报警消息
            }else if(epsProductNo==null||productId==null||"".equals(productId)||!productId.equals(epsProductNo)){
                LOGGER.error("*****push button,do not match! eps productID=" + productId + ",productNo=" +epsProductNo);
                Map<String,String> map =new HashMap<String,String>();
                map.put("result","epsError");
                map.put("type","8");
                map.put("reason","eps cant match productNo,eps id is"+productId+"productNo is"+epsProductNo);
                template.convertAndSend("/topic/lineLeaderScreen/matchError",map);
                return;
            }
        }else if(epsProductCodeEntity==null) {
           //通过bottomPlateBarCode get ProductCode
           //get cache matchBottomPlateBarCode
           String matchBottomPlateCode =redisTemplate.opsForValue().get("matchBottomPlateCode").toString();
           //get cache serial Number
           String matchSerialNumber=redisTemplate.opsForValue().get("matchSerialNumber").toString();
           //user cache matchBottomPlatebarCode to search db
           ProductCode productCode=productCodeService.getProductCodeByBottomPlateBarCode(matchBottomPlateCode);
           log.info("match bottomPlate barCode,get matchBottomPlate,value is",matchBottomPlateCode);
           //如果此刻查不到之前的状态为0的记录
           if (productCode == null) {
               //此处报一个错误日志出来,(now data error please check)
               Map<String,String> map =new HashMap<String,String>();
               map.put("result","epsError");
               map.put("type","9");
               map.put("reason","MES system cant search bottomPlateCode in productCode,cacheBottomValue is"+matchBottomPlateCode);
               template.convertAndSend("/topic/lineLeaderScreen/matchError",map);
               //此处该不该让阻挡下降，以后做逻辑处理
               return;
           }else if(productCode!=null){
               //获取对应的序列号 db中可以查到对应的bottomPlateBarCode
/*               String dbSerialNum=productCode.getSerialNo();
               //如果读到的序列号跟数据库中的序列号不一致
               if(!matchSerialNumber.contains(dbSerialNum)){
                   infoMap.put("result","MatchError");
                   infoMap.put("type","6");
                   infoMap.put("reason","serial cant match,db value is"+dbSerialNum+"cache serial value is"+matchSerialNumber);
                   template.convertAndSend("/topic/lineLeaderScreen/matchError",infoMap);
                   return;
               }*/
               //productNo cant match productId
               if(productId==null||"".equals(productId)|| !productId.equals(productCode.getProductNo())){
                    infoMap.put("result","MatchError");
                    infoMap.put("type","8");
                    infoMap.put("reason","product cant match eps, value is"+productId+productCode.getProductNo());
                   template.convertAndSend("/topic/lineLeaderScreen/matchError",infoMap);
                   return;
               }
           }
           log.info("match serialNumber.dbvalue is{},cacheValue is{}",productCode.getSerialNo(),matchSerialNumber);
           log.info("match epsCode is,{},match bottomPlate code is{}",barcode,matchBottomPlateCode);
           log.info("*****Product match OK! eps productID=" + productId + ",productNo=" + productCode.getProductNo());
           productCode.setStatus("1");
           productCode.setEpsCode(barcode);
           productCode = productCodeRepository.saveAndFlush(productCode);

           String productNo = productCode.getProductNo();
           String orderNo = productCode.getOrderNo();
           String serial = productCode.getSerialNo();
           log.info("match send serial ,value is {}",serial);
           //调用第二台打印机
           PrintLabelDto printLabelDto = new PrintLabelDto();
           printLabelDto.setProductNo(productNo);
           printLabelDto.setLine("110");
           printLabelDto.setTagType(3);
           printLabelDto.setQuantity(1);
           printLabelDto.setPrinter(1);
           printLabelDto.setOrder("12345678");
           printLabelDto.setSerial(serial);
           ResponseEntity<String> responseEntity;
           int i = 0;
           String returnSerial = "";
           while ("".equals(returnSerial) || (i < 5 && returnSerial.length() > 20)) {
               responseEntity = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                       + PRINTLABEL, printLabelDto, String.class);
               returnSerial = responseEntity.getBody().replace("\"", "");
               LOGGER.info("******************get document serialNumber：" + returnSerial);
               i++;
           }
           if (returnSerial.length() > 20) {
               //推送消息到前台
               LOGGER.error("******************get documentSerialNumber：" + returnSerial);
               template.convertAndSend("/topic/lineLeaderScreen/printer02ApiError",returnSerial);
               LOGGER.error("******************get api failure document Failure*********************");
               return;
           }
           productCode.setStatus("2");
           productCode.setDocumentEpsCode(barcode);
           productCodeRepository.saveAndFlush(productCode);
           //让阻挡下降放行
           UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
           log.info("Normal write to let zudang down,eps is,{}",barcode);
           NodeId finishNode = new NodeId(3, "\"ITread\".\"Place_Read_Finish\"");
           try {
               boolean result = opcUaClientTemplate.writeNodeValue(uaClient, finishNode, 1);
               log.info("zudang down normal finished......");
               if (!result) {
                   boolean result1 = opcUaClientTemplate.writeNodeValue(uaClient, finishNode, 1);
               }
           } catch (OpcUaClientException e) {
               LOGGER.info("scaner02 给阻挡写值失败。。。。。。productNo", productCode.getProductCode());
               e.printStackTrace();
           }
       }
    }
}