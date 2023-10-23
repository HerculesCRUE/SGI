package org.crue.hercules.sgi.eti.dto.com;

import java.io.Serializable;
import java.time.Instant;

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
public class EtiComAsignacionEvaluacionData implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  private String referenciaMemoria;
  private String nombreInvestigacion;
  private String generoComite;
  private Instant fechaConvocatoriaReunion;
  private String tituloSolicitudEvaluacion;
  private String nombreApellidosEvaluador1;
  private String nombreApellidosEvaluador2;
  private Instant fechaEvaluacionAnterior;
  private String enlaceAplicacion;

}
