package org.crue.hercules.sgi.prc.model.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;

@Converter(autoApply = true)
public class CodigoCVNConverter implements AttributeConverter<CodigoCVN, String> {

  @Override
  public String convertToDatabaseColumn(CodigoCVN enumType) {
    if (enumType == null) {
      return null;
    }
    return enumType.getInternValue();
  }

  @Override
  public CodigoCVN convertToEntityAttribute(String internValue) {
    if (internValue == null) {
      return null;
    }

    return Stream.of(CodigoCVN.values())
        .filter(c -> c.getInternValue().equals(internValue))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}