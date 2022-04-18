package org.crue.hercules.sgi.prc.model.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.springframework.util.StringUtils;

@Converter
public class EpigrafeCVNConverter implements AttributeConverter<EpigrafeCVN, String> {

  @Override
  public String convertToDatabaseColumn(EpigrafeCVN enumType) {
    if (enumType == null) {
      return null;
    }
    return enumType.getCode();
  }

  @Override
  public EpigrafeCVN convertToEntityAttribute(String code) {
    if (!StringUtils.hasText(code)) {
      return null;
    }

    return Stream.of(EpigrafeCVN.values())
        .filter(c -> c.getCode().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}