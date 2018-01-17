package com.mj.beko.opcua;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanghb
 */
public class OpcUaUtil {

    private static final Logger log = LoggerFactory.getLogger(OpcUaUtil.class);

    private static Map<String, Object> LAST_NODE_VALUE_MAP = new ConcurrentHashMap<>();

    public static boolean isNewNodeValueValid(String plcIndex, NodeId id, DataValue oldDataValue, DataValue newDataValue) {
        String nodeId = plcIndex + "." + id.toString();
        if (null == oldDataValue) {
            log.info("The subscription for {} is initialized.", nodeId);
            LAST_NODE_VALUE_MAP.put(nodeId, newDataValue.getValue().getValue());
            return false;
        }
        Variant oldVariant = oldDataValue.getValue();
        Variant newVariant = newDataValue.getValue();
        if (newVariant.isArray()){
            Object[] oldArray = (Object[]) oldVariant.getValue();
            Object[] newArray = (Object[]) newVariant.getValue();
            boolean flag = false;
            for (int i = 0; i < newArray.length; i++) {
                if (oldArray[i].equals(newArray[i])
                        || (LAST_NODE_VALUE_MAP.get(nodeId) != null
                        && ((Object[])LAST_NODE_VALUE_MAP.get(nodeId))[i].equals(newArray[i]))) {
                    continue;
                }
                flag = true;
                break;
            }
            if (!flag){
                log.debug("--->>" + nodeId + " from " + oldVariant + " to " + newVariant + "<<---");
                log.error("data change error: listener YOU DU!!!");
                return false;
            }
        } else {
            if (newVariant.getValue().equals(oldVariant.getValue())
                    || (LAST_NODE_VALUE_MAP.get(nodeId) != null
                    && LAST_NODE_VALUE_MAP.get(nodeId).equals(newVariant.getValue()))) {

                log.debug("--->>" + nodeId + " from " + oldVariant + " to " + newVariant + "<<---");
                log.error("data change error: listener YOU DU!!!");
                return false;
            }
        }
        log.info("--->>" + nodeId + " from " + oldVariant + " to " + newVariant + "<<--- go.");
        LAST_NODE_VALUE_MAP.put(nodeId, newVariant.getValue());
        return true;
    }
}
