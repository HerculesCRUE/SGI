package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidadDescripcion;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidadNombre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoInformePatentabilidadOutput implements Serializable {
  private Long id;
  private Collection<ResultadoInformePatentabilidadNombre> nombre;
  private Collection<ResultadoInformePatentabilidadDescripcion> descripcion;
  private Boolean activo;

}
