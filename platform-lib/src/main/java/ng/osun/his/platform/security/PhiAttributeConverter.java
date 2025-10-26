package ng.osun.his.platform.security;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;

/**
 * JPA AttributeConverter for PHI field encryption.
 * Usage: @Convert(converter = PhiAttributeConverter.class) on entity fields
 */
@Converter
@RequiredArgsConstructor
public class PhiAttributeConverter implements AttributeConverter<String, String> {

    @Lazy
    private final PhiEncryptionService encryptionService;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return attribute;
        }
        return encryptionService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return dbData;
        }
        return encryptionService.decrypt(dbData);
    }
}

