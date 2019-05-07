package eu.marcinszewczyk.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDate;

@Converter
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate localDate) {
        return localDate.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(String s) {
        return null;
    }
}
