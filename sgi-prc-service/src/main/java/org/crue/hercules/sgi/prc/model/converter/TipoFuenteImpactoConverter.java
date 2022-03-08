package org.crue.hercules.sgi.prc.model.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;

@Converter(autoApply = true)
public class TipoFuenteImpactoConverter implements AttributeConverter<TipoFuenteImpacto, String> {

  @Override
  public String convertToDatabaseColumn(TipoFuenteImpacto enumType) {
    if (enumType == null) {
      return null;
    }
    return enumType.getInternValue();
  }

  @Override
  public TipoFuenteImpacto convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }

    return Stream.of(TipoFuenteImpacto.values())
        .filter(c -> c.getInternValue().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}