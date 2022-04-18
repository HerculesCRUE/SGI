package org.crue.hercules.sgi.prc.model.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.prc.enums.CodigoCVN;

@Converter
public class CodigoCVNConverter implements AttributeConverter<CodigoCVN, String> {

  @Override
  public String convertToDatabaseColumn(CodigoCVN enumType) {
    if (enumType == null) {
      return null;
    }
    return enumType.getCode();
  }

  @Override
  public CodigoCVN convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }

    return Stream.of(CodigoCVN.values())
        .filter(c -> c.getCode().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}