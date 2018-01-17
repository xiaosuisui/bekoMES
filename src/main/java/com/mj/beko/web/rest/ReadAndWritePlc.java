package com.mj.beko.web.rest;

import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.prosysopc.ua.client.UaClient;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.UnsignedByte;
import org.opcfoundation.ua.builtintypes.UnsignedInteger;
import org.opcfoundation.ua.builtintypes.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by wanghb on 2017/10/26.
 * 用于读写PKC数据
 */
@RestController
@RequestMapping("/api")
public class ReadAndWritePlc {

    private final Logger log = LoggerFactory.getLogger(ReadAndWritePlc.class);

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    //测试读取节点的值
    @RequestMapping("testReadNode/{node}")
    public void testReadNode(@PathVariable String node){
//        NodeId nodeId = new NodeId(2, "BekoOpcua." + node);
//        NodeId nodeId = new NodeId(3, "\"OPCOA\".\"" + node + "\"");
//        NodeId nodeId = new NodeId(3, "\"RFID\".\"" + node + "\"");
        NodeId nodeId = new NodeId(3, "\"information\".\"" + node + "\"");
        Variant variant;
        try {
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
            variant = opcUaClientTemplate.readNodeVariant(uaClient, nodeId);
            UnsignedInteger[] vals = (UnsignedInteger[])(variant.getValue());
            String res1 = vals[0].intValue() + "";
            String data1 = vals[1].floatValue() + "";
            String data2 = vals[2].floatValue() + "";
            String res2 = vals[3].intValue() + "";
            String data12 = vals[4].floatValue() + "";
            String data22 = vals[5].floatValue() + "";
            System.out.println("解析的结果" + res1 + "\t" + data1 + "\t" + data2 + "\t" + res2 + "\t" + data12 + "\t" + data22);
//            UnsignedByte[] value = (UnsignedByte[])(variant.getValue());
//            String palletNo = value[0].intValue() + "";
//            System.out.println("*************" + palletNo + "****************");
//            byte[] bytes = new byte[value.length];
//            for (int i = 0; i < value.length; i++) {
//                bytes[i] = value[i].byteValue();
//            }
//            String str = new String(bytes);
//            System.out.println("**************");

//            long value = variant.longValue();
//            Object value = variant.getValue();
//            UnsignedByte[] value = (UnsignedByte[])variant.getValue();
//            byte[] bytes = new byte[8];
//            for (int i = 0; i < 8; i++) {
//                bytes[i] = value[i].intValue();
//            }
//            ByteBuffer buffer = ByteBuffer.allocate(8);
//            buffer.put(bytes, 0, bytes.length);
//            buffer.flip();
//            System.out.println(nodeId + "的值为：" + value.toString());
//            System.out.println(nodeId + "的值为：" + value[0].intValue());
//            System.out.println(nodeId + "的值为：" + buffer.getLong());
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("77777777777777777777");
        }
    }

    //测试写入节点值
    @RequestMapping("testWriteNode/{index}/{node1}/{node2}/{value}")
    public void testWriteNode(@PathVariable int index, @PathVariable String node1, @PathVariable String node2, @PathVariable int value){
        NodeId nodeId = new NodeId(3, "\"" + node1 + "\".\"" + node2 + "\"");
        try {
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(index);
            boolean flag = opcUaClientTemplate.writeNodeValue(uaClient, nodeId, value);
            if (flag) {
                System.out.println(nodeId + "的值写入成功");
            } else {
                System.out.println(nodeId + "的值写入失败");
            }
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
    }

    //测试写入节点值
    @RequestMapping("writeFlow")
    public void writeFlow(){
        NodeId nodeId = new NodeId(3, "\"information\".\"Flow_Range\"");
        try {
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
            Float[] data = {26f, 36f, 26f, 36f, 25f, 31f, 20f, 30f};
            boolean flag = opcUaClientTemplate.writeNodeValue(uaClient, nodeId, data);
            if (flag) {
                System.out.println(nodeId + "的值写入成功");
            } else {
                System.out.println(nodeId + "的值写入失败");
            }
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
    }
}
