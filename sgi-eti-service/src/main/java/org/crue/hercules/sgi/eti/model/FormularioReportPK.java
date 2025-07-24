package org.crue.hercules.sgi.eti.model;

import java.io.Serializable;

import org.crue.hercules.sgi.framework.i18n.Language;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FormularioReportPK implements Serializable {
  private Long formularioId;
  private Language lang;
}
