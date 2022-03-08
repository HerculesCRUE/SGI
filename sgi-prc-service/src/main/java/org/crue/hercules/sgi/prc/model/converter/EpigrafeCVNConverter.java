package org.crue.hercules.sgi.prc.model.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
import org.springframework.util.StringUtils;

@Converter(autoApply = true)
public class EpigrafeCVNConverter implements AttributeConverter<EpigrafeCVN, String> {

  @Override
  public String convertToDatabaseColumn(EpigrafeCVN enumType) {
    if (enumType == null) {
      return null;
    }
    return enumType.getInternValue();
  }

  @Override
  public EpigrafeCVN convertToEntityAttribute(String internValue) {
    if (!StringUtils.hasText(internValue)) {
      return null;
    }

    return Stream.of(EpigrafeCVN.values())
        .filter(c -> c.getInternValue().equals(internValue))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}