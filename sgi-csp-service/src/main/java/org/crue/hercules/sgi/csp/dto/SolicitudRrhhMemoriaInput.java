package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.SolicitudRrhh;

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
public class SolicitudRrhhMemoriaInput implements Serializable {

  @Size(max = SolicitudRrhh.TITULO_TRABAJO_LENGTH)
  @NotBlank
  private String tituloTrabajo;

  @Size(max = SolicitudRrhh.RESUMEN_LENGTH)
  @NotBlank
  private String resumen;

  @Size(max = SolicitudRrhh.OBSERVACIONES_LENGTH)
  private String observaciones;

}
