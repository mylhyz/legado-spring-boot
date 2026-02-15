package io.legado.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.legado.model.entity.ReadConfig;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * ReadConfig JSON转换器
 */
@Converter(autoApply = true)
public class ReadConfigConverter implements AttributeConverter<ReadConfig, String> {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(ReadConfig attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    @Override
    public ReadConfig convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ReadConfig();
        }
        try {
            return mapper.readValue(dbData, ReadConfig.class);
        } catch (Exception e) {
            return new ReadConfig();
        }
    }
}
