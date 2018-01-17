package com.mj.beko.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;
import com.fasterxml.jackson.databind.JavaType;
import com.google.common.base.CaseFormat;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanghb
 */
public class CamelCaseMappingJacksonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private static Map<Class, Map<String, String>> clazz_field_map = new HashMap<>();

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            if (inputMessage instanceof MappingJacksonInputMessage) {
                Class<?> deserializationView = ((MappingJacksonInputMessage) inputMessage).getDeserializationView();
                if (deserializationView != null) {
                    return this.objectMapper.readerWithView(deserializationView).withType(javaType).
                        readValue(translateToCamelCaseKeys(javaType, inputMessage.getBody()));
                }
            }
            return this.objectMapper.readValue(translateToCamelCaseKeys(javaType, inputMessage.getBody()), javaType);
        }
        catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read document: " + ex.getMessage(), ex);
        }
    }

    private void populateClazzFieldMap(JavaType javaType) {
        Class clazz = javaType.getRawClass();
        if (clazz.isAssignableFrom(ArrayList.class)) {
            javaType = javaType.getContentType();
            clazz = javaType.getRawClass();
        }
        if (!clazz_field_map.containsKey(clazz)) {
            Map<String, String> nameFieldMap = new HashMap<>();
            Class actualClazz = clazz;
            Type superClazz = clazz.getGenericSuperclass();
            if (superClazz instanceof ParameterizedType) {
                actualClazz = (Class)(((ParameterizedType) superClazz).getActualTypeArguments()[0]);
            }
            Field[] fields = actualClazz.getDeclaredFields();
            for (Field field : fields) {
                JsonReadingProperty property = field.getAnnotation(JsonReadingProperty.class);
                if (property != null) {
                    nameFieldMap.put(property.value(), field.getName());
                }
            }
            clazz_field_map.put(clazz, nameFieldMap);
        }
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {

        JavaType javaType = getJavaType(type, contextClass);
        populateClazzFieldMap(javaType);
        return readJavaType(javaType, inputMessage);
    }

    private byte[] translateToCamelCaseKeys(JavaType javaType, InputStream messageBody) throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator camelCaseFieldNameGenerator = new JsonGeneratorDelegate(
            this.getObjectMapper().getFactory().createGenerator(writer)) {

            @Override
            public void writeFieldName(String name) throws IOException {
                String conversion = name;
                Class clazz = javaType.getRawClass();
                if (clazz.isAssignableFrom(ArrayList.class)) {
                    clazz = javaType.getContentType().getRawClass();
                }
                Map<String, String> nameFieldMap = clazz_field_map.get(clazz);
                if (nameFieldMap.containsKey(name)) {
                    conversion = nameFieldMap.get(name);
                } else if (name.indexOf('_') > 0) {
                    conversion = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
                }
                delegate.writeFieldName(conversion);
            }
        };
        this.getObjectMapper().writeTree(camelCaseFieldNameGenerator, this.getObjectMapper().readTree(messageBody));
        camelCaseFieldNameGenerator.close();
        return writer.getBuffer().toString().getBytes();
    }
}
