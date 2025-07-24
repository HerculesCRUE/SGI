package org.crue.hercules.sgi.csp.dto.com;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Enlace implements Serializable {

  /** Serial version */
  private static final long serialVersionUID = 1L;
  private Collection<? extends I18nFieldValue> descripcion;
  private String url;
  private Collection<? extends I18nFieldValue> tipoEnlace;
}
