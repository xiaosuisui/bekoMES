package com.mj.beko.schedule;

import com.ar3.services.strong.boptomesintegration._2014_12.AssemblyBOPDataService;
import com.mj.beko.domain.*;
import com.mj.beko.domain.dto.OrderDto;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.repository.*;
import com.mj.beko.service.OperationService;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.WorkstationService;
import com.tc.integration.MESIntegration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wanghb
 */
@Component
public class ScheduleGetProductPlanAndOperation {

    @Inject
    private HttpTemplate httpTemplate;

    @Inject
    private OrderService orderService;

    @Inject
    private OrderStationRepository orderStationRepository;

    @Inject
    private WorkstationService workstationService;

    @Inject
    private StationDatasetsRepository stationDatasetsRepository;

    @Inject
    private OperationService operationService;

    @Inject
    private OperationDatasetsRepository operationDatasetsRepository;

    @Inject
    private ConsumedPartsRepository consumedPartsRepository;

    @Inject
    private PartsDatasetsRepository partsDatasetsRepository;

    private static final String GET_PRODUCTPLAN = "/GasAutomationApi/api/ProductionPlan/GetProductionPlans";

    /**
     * 根据SapLineId、StartDate、EndDate调用beko接口获取生产计划
     */
    public void getProductPlan(){
        ParameterizedTypeReference<ArrayList<Order>> typeRef = new ParameterizedTypeReference<ArrayList<Order>>(){};
        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//        headers.setContentType(type);
//        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        OrderDto orderDto = new OrderDto();
        orderDto.setSapLineId("110");
        orderDto.setStartDate(LocalDate.now().toString() + " 00:00");
        orderDto.setEndDate(LocalDate.now().toString() + " 23:59");
//        ResponseEntity<String> responseEntity = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
//                + GET_PRODUCTPLAN, orderDto, String.class);
//        String res = responseEntity.getBody();
        HttpEntity<OrderDto> httpEntity = new HttpEntity<>(orderDto, headers);
        ResponseEntity<ArrayList<Order>> responseEntity = httpTemplate.exchange(httpTemplate.getBekoApiHttpSchemeHierarchical()
                + GET_PRODUCTPLAN, HttpMethod.POST, httpEntity, typeRef);

//        ArrayList<Order> orders = responseEntity.getBody();
//        RequestEntity<OrderDao> requestEntity = null;
//        try {
//            requestEntity = new RequestEntity<OrderDao>(orderDao, HttpMethod.POST, new URI(httpTemplate.getBekoApiHttpSchemeHierarchical()
//                    + GET_PRODUCTPLAN));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        ResponseEntity<ArrayList<Order>> responseEntity = httpTemplate.exchange(requestEntity, typeRef);
        List<Order> orders = responseEntity.getBody();
        for (Order order : orders) {
            //将拉取的订单计划保存到数据库中
            order.setOperationDateTime(new Timestamp(order.getOperationDateTime().getTime() - 2*60*60*1000));
            order.setStatus("0");
            order.setOnlineNumber(0);
            order.setRepairNumber(0);
            order.setBrokenNumber(0);
            order.setRepair02Broken(0);
            order.setCompletionNumber(0);
            order.setStatus("4");
            orderService.save(order);
        }
    }

    /**
     * 根据订单获取工艺信息
     */
    public void getOperations(){
        //查询订单表中状态为"4"的记录
        List<Order> orders = orderService.findOrdersByStatus("4");
        if (orders != null && orders.size() > 0){
            for (Order order : orders){
                String productNo = order.getProductNo();
                //根据工单号确定工艺是否存在，存在则不获取工艺信息，不存在则获取工艺信息
                OrderStation hasOrNo = orderStationRepository.findOrderStationByProductNo(productNo);
                if (hasOrNo == null) {
                    //调用Beko的http API获取工艺信息
                    MESIntegration mesIntegration = new MESIntegration();
                    AssemblyBOPDataService.AssemblyBOPDataResponse retObj = mesIntegration.getBOPData("PCI-MHM6_Mntj_Bndi_EBOP", new String[]{productNo}, null);
                    String bopErrorMsg = retObj.bopErrorMsg;
                    if ("".equals(bopErrorMsg)) {
                        Map<String, Object> productAndPlantBOPMap = retObj.productAndPlantBOPMap;
                        for (Map.Entry<String, Object> bopEntry : productAndPlantBOPMap.entrySet()) {
//                          String productNo = entry.getKey();  //产品类型
                            AssemblyBOPDataService.PlantBOPStructure plantBOP = (AssemblyBOPDataService.PlantBOPStructure) bopEntry.getValue();
                            String proErrMsgifAny = plantBOP.bopErrorMsg;
                            if ("".equals(proErrMsgifAny)) {
                                //解析工位数据
                                AssemblyBOPDataService.Stations[] stations = plantBOP.stationsVec;
                                for (AssemblyBOPDataService.Stations stns : stations) {
                                    Map<String, String> stnPropertiesMap = stns.stnPropertiesMap;
                                    //此处进行工位匹配操作
                                    Workstation workstation = workstationService.getWorkstationByStationId(stnPropertiesMap.get("station_id"));
                                    if (workstation == null || workstation.getStationId() == null || "".equals(workstation.getStationId())) continue;
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
                                    AssemblyBOPDataService.Operations[] operations = stns.operationVec;
                                    for (AssemblyBOPDataService.Operations opers : operations) {
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
                                        AssemblyBOPDataService.Part[] parts = opers.consumedParts;
                                        for (AssemblyBOPDataService.Part part : parts) {
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
                //当工艺拉取成功时根据订单号将订单状态改为0
                orderService.updateOrderStatusByOrderNoWhenOperationOk(order.getOrderNo());
            }
        }
    }
}
