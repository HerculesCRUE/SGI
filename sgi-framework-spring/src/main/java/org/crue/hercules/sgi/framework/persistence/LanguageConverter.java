package org.crue.hercules.sgi.framework.persistence;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.framework.i18n.Language;

@Converter
public class LanguageConverter implements AttributeConverter<Language, String> {

  @Override
  public String convertToDatabaseColumn(Language enumType) {
    if (enumType == null) {
      return null;
    }
    return enumType.getCode();
  }

  @Override
  public Language convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }

    return Stream.of(Language.values())
        .filter(c -> c.getCode().equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}