package com.mj.beko.web.rest.screenApi;

import com.mj.beko.domain.ProductCode;
import com.mj.beko.domain.dto.PrintLabelDto;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcualistener.third.ProductPutFinishedListener;
import com.mj.beko.opcualistener.third.ReadPrinterLabelListener;
import com.mj.beko.opcualistener.third.UpEpsPutDownFinishedListener;
import com.mj.beko.service.ProductCodeService;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ricardo on 2018/1/5.
 * 打印机处理相关的api
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class PrinterToScreenApi {
    @Inject
    private TaskExecutor taskExecutor;
    @Inject
    private HttpTemplate httpTemplate;
    @Inject
    private SimpMessagingTemplate template;
    @Autowired
    private ProductCodeService productCodeService;
    @Autowired
    private ProductPutFinishedListener productPutFinishedListener;
    @Autowired
    private ReadPrinterLabelListener readPrinterLabelListener;
    @Autowired
    private UpEpsPutDownFinishedListener upEpsPutDownFinishedListener;
    @Autowired
    private OpcUaClientTemplate opcUaClientTemplate;
    private static final String PRINTLABEL = "/GasAutomationApi/api/Product/PrintProductLabel";
    //tablet 再次扫描的按钮
    @RequestMapping("/requestPrinter01ApiAgain")
    public void requestPrinter01ApiAgain(String productNo, String orderNo, String bottomPlateBarcode){
        log.info("Api call request for printer01Api get SerialNum");
       taskExecutor.execute(() -> printerApiWorkAgain(productNo,orderNo,bottomPlateBarcode));
    }
    public void printerApiWorkAgain(String productNo, String orderNo, String bottomPlateBarcode){
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
        while ("".equals(serial) || (i < 4 && serial.length() > 20)) {
            responseEntity = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                    + PRINTLABEL, printLabelDto, String.class);
            serial = responseEntity.getBody().replace("\"", "");
            log.info("******************get serialNumber"+i+" printer01 use tablet screen：" + serial);
            try {
                //延迟500ms调用
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        //如果3次调用失败
        if (serial.length() > 20) {
            Map infoMap =new HashMap<String,String>();
            infoMap.put("result","printerError");
            infoMap.put("type","3");
            infoMap.put("value",productNo+","+orderNo+","+bottomPlateBarcode);
            infoMap.put("reason","call printer01Api Error,BottomPlateCode is,"+bottomPlateBarcode+",serial is "+serial);
            //推送消息到前台
            template.convertAndSend("/topic/lineLeaderScreen/printerError", infoMap);
            log.error("******************get error serialNumber use screen use button api" + serial);
            return;
        }
        ProductCode productCode = productCodeService.getProductCodeByBottomPlateBarCode(bottomPlateBarcode);
        //表明每次为空的时候生成一条记录
        if (productCode == null) {
            productCode = new ProductCode();
            productCode.setCreateDate(Timestamp.from(Instant.now()));
            productCode.setOrderNo(orderNo);
            productCode.setProductNo(productNo);
            productCode.setProductCode(bottomPlateBarcode); //下底盘条码
            productCode.setStatus("0");
            productCode.setSerialNo(serial);
            productCodeService.save(productCode);
            //表示成功，可以告诉plc了。。或可以处理一些事情
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
            NodeId printer01Node = new NodeId(3, "\"ITread\".\"Scanned_completed\"");
            try {
                //如果发成功了,则给plc信号,hi 你可以抓取了。往节点写1值,写2次
                boolean flag1 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
                if(!flag1){
                    boolean flag2 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
                }
            }catch (Exception e){
                log.error("get opcua failure,write sigual to printer01");
            }
        }else{
            Map infoMap =new HashMap<String,String>();
            infoMap.put("result","printerError");
            infoMap.put("type","4");
            infoMap.put("reason","bottomPlateCode already exist,value is,"+bottomPlateBarcode);
            template.convertAndSend("/topic/lineLeaderScreen/printerError",infoMap);
        }
    }
    //通过信号的方式重新扫描eps
    @RequestMapping("/setScanerWorkAgain")
    public Map<String,String> setScanerWorkAgain(){
        Map<String,String> map =new HashMap<String,String>();
        map.put("result","ok");
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
        //机器人放置完成信号
        NodeId finishNode = new NodeId(3, "\"ITread\".\"Place_Finish\"");
        try {
            boolean result=opcUaClientTemplate.writeNodeValue(uaClient, finishNode, 1);
            if(!result){
                boolean result1=opcUaClientTemplate.writeNodeValue(uaClient, finishNode, 1);
            }
        } catch (OpcUaClientException e) {
            log.info("scaner02 机器人放置完成信号写入失败");
            e.printStackTrace();
        }
        return map;
    }
    //bottomPlateBarCode again
    @RequestMapping("/getBottomPlateBarCodeAgain")
    public void getBottomPlateBarCodeAgain(){
        log.info("button click for request getBottomPlateBarcode again");
        taskExecutor.execute(() ->productPutFinishedListener.callBarCodeForBottomPlateBarCode());
    }
    //scanner03 work again
    @RequestMapping("/readPrinterLabelAgain")
    public void setScanner03WorkAgain(){
        log.info("botton click for request scanner03 again");
        taskExecutor.execute(() ->readPrinterLabelListener.readPrinterLabel());
    }
    //eps code get again
    @RequestMapping("/readEpsCodeAgain")
    public void readEpsCodeAgain(){
        taskExecutor.execute(() ->upEpsPutDownFinishedListener.doThing());
    }
}
