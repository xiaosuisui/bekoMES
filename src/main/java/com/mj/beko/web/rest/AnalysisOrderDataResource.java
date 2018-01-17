package com.mj.beko.web.rest;

import com.alibaba.fastjson.JSON;
import com.ar3.services.strong.boptomesintegration._2014_12.AssemblyBOPDataService.Part;
import com.ar3.services.strong.boptomesintegration._2014_12.AssemblyBOPDataService.Operations;
import com.ar3.services.strong.boptomesintegration._2014_12.AssemblyBOPDataService.Stations;
import com.ar3.services.strong.boptomesintegration._2014_12.AssemblyBOPDataService.PlantBOPStructure;
import com.ar3.services.strong.boptomesintegration._2014_12.AssemblyBOPDataService.AssemblyBOPDataResponse;
import com.mj.beko.domain.*;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.repository.*;
import com.mj.beko.schedule.ScheduleGetProductPlanAndOperation;
import com.mj.beko.service.OperationService;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.WorkstationService;
import com.prosysopc.ua.client.UaClient;
import com.tc.integration.MESIntegration;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wanghb
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AnalysisOrderDataResource {

    private final Logger log = LoggerFactory.getLogger(AnalysisOrderDataResource.class);

    @Inject
    private OrderService orderService;

    @Inject
    private OperationService operationService;

    @Inject
    private OrderStationRepository orderStationRepository;

    @Inject
    private StationDatasetsRepository stationDatasetsRepository;

    @Inject
    private OperationDatasetsRepository operationDatasetsRepository;

    @Inject
    private PartsDatasetsRepository partsDatasetsRepository;

    @Inject
    private ConsumedPartsRepository consumedPartsRepository;

    @Inject
    private WorkstationService workstationService;

    @Inject
    private HttpTemplate httpTemplate;

    @Inject
    private ScheduleGetProductPlanAndOperation scheduleGetProductPlanAndOperation;

    public static final String GET_ORDER_BY_ID = "/api/orders/{0}";

    @RequestMapping("getOrderAndAnalysisData")
    public void getOrderAndAnalysisData() throws Exception {
        log.info("===== 开始解析订单数据 =====");
        Client orderClient = null;
        Client gongYiClient = null;
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
            orderClient = factory.createClient("http://192.168.88.142:8080/soap/workOrder?wsdl");
            Object[] objects = orderClient.invoke("getWorkOrder",1111);
            String result = JSON.toJSONString(objects[0]);
            JSONArray orderArray = new JSONArray(result);
            for (int q = 0; q < orderArray.length(); q++) {
                JSONObject orderObj = orderArray.getJSONObject(q);

                //根据工单号确定工艺是否存在，存在则不获取工艺信息，不存在则获取工艺信息
                OrderStation hasOrNo = orderStationRepository.findOrderStationByProductNo(orderObj.getString("productNo"));
                if (hasOrNo != null) continue;

                //保存订单表
                Order order = new Order();
                order.setOrderNo(orderObj.getString("orderNo"));
                order.setProductNo(orderObj.getString("productNo"));
                order.setQuantity(orderObj.getInt("quantity"));
                order.setStatus(orderObj.getString("status"));
                order.setOnlineNumber(0);
                order.setRepairNumber(0);
                order.setCompletionNumber(0);
                order = orderService.save(order);

                Thread.currentThread().setContextClassLoader(cl);
                gongYiClient = factory.createClient(" http://192.168.88.142:8080/soap/readData?wsdl");
                Object[] res = gongYiClient.invoke("getData","123");
                String jsonString1 = (String) res[0];
                System.out.println(jsonString1);
                //获取到整个数据
                JSONObject jsonObject = new JSONObject(jsonString1);
                jsonObject = jsonObject
                    .getJSONObject("data")
                    .getJSONObject("assemblyBOPDataResponse")
                    .getJSONObject("productAndPlantBOP")
                    .getJSONObject("plantBOP");
                //获取所有工位JSON数组
                JSONArray stationArray = jsonObject.getJSONArray("stations");
                for (int i = 0; i < stationArray.length(); i++) {
                    //获得单个工位
                    JSONObject oneStation = stationArray.getJSONObject(i);

                    //将数据解析到工位实体上
                    Workstation workstation = new Workstation();
                    workstation.setId(oneStation.getLong("stationId"));
                    workstation.setStationName(oneStation.getString("stationName"));
//                    workstation.setStationNo(oneStation.getString("seqNo"));
//                    workstation.setAllocationPercent(oneStation.getString("stationAllocationPercent"));

                    //保存OrderStation
//                  OrderStation orderStation = orderStationService.saveOrderStation(orderId, workstation.getId());
                    OrderStation orderStation = new OrderStation();
                    orderStation.setProductNo(order.getProductNo());
                    orderStation.setWorkstation(workstation);
                    orderStation = orderStationRepository.save(orderStation);

                    //获取station datasets
                    JSONArray stationDatasetsArray = oneStation.getJSONArray("datasets");
                    List<StationDatasets> stationDatasets = new ArrayList<>();
                    for (int j = 0; j < stationDatasetsArray.length(); j++) {
                        JSONObject datasetObj = stationDatasetsArray.getJSONObject(j);
                        StationDatasets stationDataset;
                        JSONArray values = datasetObj.getJSONArray("value");
                        for (int m = 0; m < values.length(); m++) {
                            stationDataset = new StationDatasets();
                            stationDataset.setOrderStation(orderStation);
                            stationDataset.setStationKey(datasetObj.getString("key"));
                            stationDataset.setStationValue(values.getString(m));
                            stationDatasets.add(stationDataset);
                        }
                    }
                    //保存StationDatasets
                    stationDatasetsRepository.save(stationDatasets);

                    //获取当前工位的所有工序JSON数组
                    JSONArray operationArray = oneStation.getJSONArray("operations");
                    for (int j = 0; j < operationArray.length(); j++) {
                        //获得单个工序
                        JSONObject oneOperation = operationArray.getJSONObject(j);
                        //将数据解析到工序实体上
                        Operation operation = new Operation();
                        operation.setOperationName(oneOperation.getString("operationName"));
                        operation.setOperationDesc(oneOperation.getString("operationDesc"));
                        operation.setSeqNo(oneOperation.getString("seqNo"));
                        operation.setOperationAllocation(oneOperation.getString("operationAllocation"));
                        operation.setOperationDuration(oneOperation.getString("operationDuration"));
                        operation.setOperationStdManSec(oneOperation.getString("operationStdManSec"));
//                        operation.setOperationDateReleased(oneOperation.getString("operationDateReleased"));
                        operation.setOrderStation(orderStation);
                        operation = operationService.save(operation);

                        //获取Operation datasets
                        JSONArray operationDatasetsArray = oneOperation.getJSONArray("datasets");
                        List<OperationDatasets> operations = new ArrayList<>();
                        for (int k = 0; k < operationDatasetsArray.length(); k++) {
                            JSONObject datasetsObj = operationDatasetsArray.getJSONObject(k);
                            OperationDatasets operationDatasets;
                            JSONArray values = datasetsObj.getJSONArray("value");
                            for (int m = 0; m < values.length(); m++) {
                                operationDatasets = new OperationDatasets();
                                operationDatasets.setOperation(operation);
                                operationDatasets.setOperationKey(datasetsObj.getString("key"));
                                operationDatasets.setOperationValue(values.getString(m));
                                operations.add(operationDatasets);
                            }
                        }
                        //保存oprationDatasets
                        operationDatasetsRepository.save(operations);

                        //获取当前工位当前工序的组件
                        JSONArray consumedPartsArray = oneOperation.getJSONArray("consumedParts");
                        for (int k = 0; k < consumedPartsArray.length(); k++) {
                            //获取单个组件
                            JSONObject oneParts = consumedPartsArray.getJSONObject(k);
                            //将数据解析到ConsumedParts上
                            ConsumedParts consumedParts = new ConsumedParts();
                            consumedParts.setOperation(operation);
                            consumedParts.setPartName(oneParts.getString("partName"));
                            consumedParts.setPartDateReleased(oneParts.getString("partDateReleased"));
                            consumedParts = consumedPartsRepository.save(consumedParts);

                            //获得ConsumedParts datasets
                            JSONArray consumedPartsArrays = oneParts.getJSONArray("datasets");
                            List<PartsDatasets> partsDatasets = new ArrayList<>();
                            for (int p = 0; p < consumedPartsArrays.length(); p++) {
                                JSONObject datasetsObj = consumedPartsArrays.getJSONObject(p);
                                PartsDatasets datasets;
                                JSONArray values = datasetsObj.getJSONArray("value");
                                for (int m = 0; m < values.length(); m++) {
                                    datasets = new PartsDatasets();
                                    datasets.setConsumedParts(consumedParts);
                                    datasets.setPartKey(datasetsObj.getString("key"));
                                    datasets.setPartValue(values.getString(m));
                                    partsDatasets.add(datasets);
                                }
                            }
                            partsDatasetsRepository.save(partsDatasets);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (orderClient != null) orderClient.destroy();
            if (gongYiClient != null) gongYiClient.destroy();
        }
    }

    @RequestMapping("testHttpClient/{id}")
    public void testHttpClient(@PathVariable Long id){
        ResponseEntity<Order> result = httpTemplate.getForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                + MessageFormat.format(GET_ORDER_BY_ID, id), Order.class);
        Order order = result.getBody();
        System.out.println("************" + order + "***********");
        ResponseEntity<String> stringResult = httpTemplate.getForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                + MessageFormat.format(GET_ORDER_BY_ID, id), String.class);
        String res = stringResult.getBody();
        System.out.println("************" + res + "***********");
        ParameterizedTypeReference<ArrayList<Order>> typeRef = new ParameterizedTypeReference<ArrayList<Order>>(){};
        ResponseEntity<ArrayList<Order>> result1 = httpTemplate.exchange(httpTemplate.getBekoApiHttpSchemeHierarchical()
                + MessageFormat.format(GET_ORDER_BY_ID, id), HttpMethod.GET, null, typeRef);

    }

    @RequestMapping("testBeko")
    public void testBeko(String bopId, String productNo){
//        scheduleGetProductPlanAndOperation.getProductPlan();
        MESIntegration mesIntegration = new MESIntegration();
        AssemblyBOPDataResponse retObj = mesIntegration.getBOPData(bopId, new String[]{productNo}, null);
        String bopErrorMsg = retObj.bopErrorMsg;
        if ("".equals(bopErrorMsg)) {
            Map<String, Object> productAndPlantBOPMap = retObj.productAndPlantBOPMap;
            for (Map.Entry<String, Object> bopEntry : productAndPlantBOPMap.entrySet()) {
//                String productNo = entry.getKey();  //产品类型
                PlantBOPStructure plantBOP = (PlantBOPStructure) bopEntry.getValue();
                String proErrMsgifAny = plantBOP.bopErrorMsg;
                if ("".equals(proErrMsgifAny)) {
                    //解析工位数据
                    Stations[] stations = plantBOP.stationsVec;
                    for (Stations stns : stations) {
                        Map<String, String> stnPropertiesMap = stns.stnPropertiesMap;
                        //此处进行工位匹配操作
                        /////////////////////////////////////////
                        Workstation workstation = workstationService.getWorkstationByStationId(stnPropertiesMap.get("station_id"));
                        if (workstation == null || workstation.getStationId() == null || "".equals(workstation.getStationId())) continue;
                        /////////////////////////////////////////
                        //保存工位与工艺中间表信息
                        OrderStation orderStation = new OrderStation();
                        orderStation.setProductNo(productNo);
                        orderStation.setWorkstation(workstation);
                        orderStation = orderStationRepository.save(orderStation);
                        //保存工位Datasets
                        Map<String, String> stnDatasets = stns.stnDatasets;
                        List<StationDatasets> stationDatasetsList = new ArrayList<>();
                        StationDatasets stationDataset;
                        for (Map.Entry<String, String> stnsEntry : stnDatasets.entrySet()) {
                            stationDataset = new StationDatasets();
                            stationDataset.setOrderStation(orderStation);
                            stationDataset.setStationKey(stnsEntry.getKey());
                            stationDataset.setStationValue(stnsEntry.getValue());
                            stationDatasetsList.add(stationDataset);
                        }
                        stationDatasetsRepository.save(stationDatasetsList);
                        //解析工序数据
                        Operations[] operations = stns.operationVec;
                        for (Operations opers : operations) {
                            Map<String, String> oprPropertiesMap = opers.oprPropertiesMap;
                            //保存工序实体
                            Operation operation = new Operation();
                            operation.setOperationId(oprPropertiesMap.get("operation_id"));
                            operation.setOperationName(oprPropertiesMap.get("operation_name"));
                            operation.setOperationDesc(oprPropertiesMap.get("operation_desc"));
                            operation.setSeqNo(oprPropertiesMap.get("Seq_no"));
                            operation.setOperationAllocation(oprPropertiesMap.get("operationAllocation"));
                            operation.setOperationDuration(oprPropertiesMap.get("operation_Duration"));
                            operation.setOperationStdManSec(oprPropertiesMap.get("operation_Std_Man_Sec"));
                            operation.setOperationProcessCategory(oprPropertiesMap.get("operation_Process_Category"));
                            operation.setOrderStation(orderStation);
                            operation = operationService.save(operation);
                            //保存工序Datasets
                            Map<String, String> oprDatasets = opers.oprDatasets;
                            List<OperationDatasets> operationDatasetsList = new ArrayList<>();
                            OperationDatasets operationDatasets;
                            for (Map.Entry<String, String> oprsEntry : oprDatasets.entrySet()) {
                                operationDatasets = new OperationDatasets();
                                operationDatasets.setOperation(operation);
                                operationDatasets.setOperationKey(oprsEntry.getKey());
                                operationDatasets.setOperationValue(oprsEntry.getValue());
                                operationDatasetsList.add(operationDatasets);
                            }
                            operationDatasetsRepository.save(operationDatasetsList);
                            //解析核心零件数据
                            Part[] parts = opers.consumedParts;
                            for (Part part : parts) {
                                //保存核心零件信息
                                Map<String, String> partProperties = part.partProperties;
                                ConsumedParts consumedParts = new ConsumedParts();
                                consumedParts.setOperation(operation);
                                consumedParts.setPartId(partProperties.get("part_id"));
                                consumedParts.setPartName(partProperties.get("part_name"));
                                consumedParts.setPartDesc(partProperties.get("part_desc"));
                                consumedParts.setPartDateReleased(partProperties.get("part_date_released"));
                                consumedParts = consumedPartsRepository.save(consumedParts);
                                //保存核心零件Datasets
                                Map<String, String> partDatasets = part.partDatasets;
                                List<PartsDatasets> partsDatasetsList = new ArrayList<>();
                                PartsDatasets datasets;
                                for (Map.Entry<String, String> partsEntry : partDatasets.entrySet()) {
                                    datasets = new PartsDatasets();
                                    datasets.setConsumedParts(consumedParts);
                                    datasets.setPartKey(partsEntry.getKey());
                                    datasets.setPartValue(partsEntry.getValue());
                                    partsDatasetsList.add(datasets);
                                }
                                partsDatasetsRepository.save(partsDatasetsList);
                            }
                        }
                    }
                }
            }
        }
    }
}
