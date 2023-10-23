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
public class EtiComRevisionActaData implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  private Instant fechaEvaluacion;
  private String nombreInvestigacion;
  private String nombreComite;
  private String generoComite;
  private String enlaceAplicacion;
}
