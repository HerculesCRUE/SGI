package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.framework.i18n.Language;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformeOutput implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private Memoria memoria;
  private TipoEvaluacion tipoEvaluacion;
  private Integer version;
  private String documentoRef;
  private String lang;

  public InformeOutput(Long id, Memoria memoria, TipoEvaluacion tipoEvaluacion, Integer version, String documentoRef,
      Language language) {
    this.id = id;
    this.memoria = memoria;
    this.tipoEvaluacion = tipoEvaluacion;
    this.version = version;
    this.documentoRef = documentoRef;
    this.lang = language.getCode();
  }

}
