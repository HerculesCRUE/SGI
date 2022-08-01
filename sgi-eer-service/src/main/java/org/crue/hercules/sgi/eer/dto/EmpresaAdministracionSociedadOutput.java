package org.crue.hercules.sgi.eer.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad.TipoAdministracion;

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
public class EmpresaAdministracionSociedadOutput implements Serializable {

  private Long id;
  private String miembroEquipoAdministracionRef;
  private Instant fechaInicio;
  private Instant fechaFin;
  private TipoAdministracion tipoAdministracion;
}
