package com.mj.beko.web.websoket;

import com.mj.beko.domain.*;
import com.mj.beko.service.OperationService;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class MyTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private OrderService orderService;

    private SimpMessagingTemplate template;

    @Autowired
    public MyTest(SimpMessagingTemplate template) {
        this.template = template;
    }


//    @Scheduled(fixedRate = 10000)
    public void mytesdfsdf() {
        List<Operation> operationList = operationService.getOperationByProductNoAndWorkstationId("00001", 1L);
        Map<String, Order> currentOrderAndNextOrder = orderService.getCurrentOrderAndNextOrder();
        //获得视频URL
        List<OperationDatasets>operationDatasets = operationService.getOperationDatasetsByProductNoAndWorkstationId("00001",1L);
        //获得图片的URL
        List<PartsDatasetsVM> picturePartDatasets = operationService.getPicturePartDatasetsByProductNoAndWorkstationId("00001", 1L);

        //模拟向一体机发消息
        List list = new ArrayList();
        list.add("station01");
        list.add("station02");
        list.add("station03");
        list.add("station04");
        list.add("station05");
        list.add("station06");
        list.add("station07");
        list.add("station08");
        list.add("station09");
        list.add("station10");
        list.add("station11");
        list.add("station12");
//        Greeting greeting = new Greeting("我是消息");
//        template.convertAndSend("/topic/greetingsBack02", greeting);
        for (int i = 0;i<list.size();i++) {
        Greeting greeting = new Greeting("我是发给: "+list.get(i)+"的消息");
        template.convertAndSend("/topic/"+list.get(i)+"/operation", operationList);
        template.convertAndSend("/topic/"+list.get(i)+"/currentOrder", currentOrderAndNextOrder.get("currentOrder"));
        template.convertAndSend("/topic/"+list.get(i)+"/nextOrder", currentOrderAndNextOrder.get("nextOrder")==null ? new Order(): currentOrderAndNextOrder.get("nextOrder"));
        template.convertAndSend("/topic/"+list.get(i)+"/videoURL", operationDatasets);
        template.convertAndSend("/topic/"+list.get(i)+"/pictureURL", picturePartDatasets);

        }

    }

    /**
     * 测试扫码枪
     * 测试前，需要将权限放行
     */
    @RequestMapping("/api/barcodeScanner")
    public void barcodeScannerTest(String barcode) {
        System.out.println(barcode);
        template.convertAndSend("/topic/greetingsBack02", barcode);
    }

    /**
     * 测试宕机时操作人员发信息通知线长（根据角色）
     */
    @RequestMapping("/api/callLineLeader")
    public void callLineLeaderTest(String message) {
        System.out.println(message);
        //查询可以接受信息的角色
        List<Role> list = roleService.findAll();
        if (list.size() > 0) {
            for (Role role : list
                ) {
                String destination = role.getName();
                template.convertAndSend("/topic/"+destination, message);
                System.out.println("发送信息给："+destination);
            }
        }
    }
}
