package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.util.List;

import org.crue.hercules.sgi.eti.model.InformeDocumento;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformeDocumentoOutput implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private Memoria memoria;
  private TipoEvaluacion tipoEvaluacion;
  private Integer version;
  private List<InformeDocumento> informeDocumentos;

}
