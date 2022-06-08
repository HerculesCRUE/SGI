package org.crue.hercules.sgi.prc.dto.sgp;

import java.io.Serializable;

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
public class DireccionTesisDto implements Serializable {

  private String id;
  private String personaRef;
  private String tituloTrabajo;
  private String fechaDefensa;
  private String alumno;
  private TipoTesisDto tipoProyecto;
  private String calificacionObtenida;
  private String coDirectorTesisRef;
  private Boolean doctoradoEuropeo;
  private String fechaMencionDoctoradoEuropeo;
  private Boolean mencionCalidad;
  private String fechaMencionCalidad;
  private Boolean mencionInternacional;
  private Boolean mencionIndustrial;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoTesisDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }
}
