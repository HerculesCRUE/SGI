package org.crue.hercules.sgi.eti.dto.com;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@ToString
public class EmailParam implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Name */
  private String name;
  /** Value */
  private transient Object value;

  public EmailParam(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public EmailParam(String name, Collection<? extends I18nFieldValue> value) {
    this.name = name;
    this.value = value;
  }
}
