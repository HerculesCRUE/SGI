package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.BaseEntity;

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
public class ProyectoPeriodoJustificacionIdentificadorJustificacionInput implements Serializable {

  private Instant fechaPresentacionJustificacion;

  @Size(max = BaseEntity.EXTERNAL_REF_LENGTH)
  private String identificadorJustificacion;
}
