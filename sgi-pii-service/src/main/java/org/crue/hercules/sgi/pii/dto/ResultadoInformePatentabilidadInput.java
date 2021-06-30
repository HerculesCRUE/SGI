package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad;

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
public class ResultadoInformePatentabilidadInput implements Serializable {
  @NotEmpty
  @Size(max = ResultadoInformePatentabilidad.NOMBRE_LENGTH)
  private String nombre;

  @Size(max = ResultadoInformePatentabilidad.DESCRIPCION_LENGTH)
  private String descripcion;

}
