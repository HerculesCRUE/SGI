package org.crue.hercules.sgi.framework.i18n;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * I18n Field Value DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class I18nFieldValueDto implements I18nFieldValue, Serializable {
  private Language lang;
  private String value;
}
