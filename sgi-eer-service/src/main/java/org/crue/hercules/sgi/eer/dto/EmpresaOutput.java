package org.crue.hercules.sgi.eer.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.eer.model.Empresa.EstadoEmpresa;
import org.crue.hercules.sgi.eer.model.Empresa.TipoEmpresa;

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
public class EmpresaOutput implements Serializable {
  private Long id;
  private Instant fechaSolicitud;
  private TipoEmpresa tipoEmpresa;
  private String solicitanteRef;
  private String nombreRazonSocial;
  private String entidadRef;
  private String objetoSocial;
  private String conocimientoTecnologia;
  private String numeroProtocolo;
  private String notario;
  private Instant fechaConstitucion;
  private Instant fechaAprobacionCG;
  private Instant fechaIncorporacion;
  private Instant fechaDesvinculacion;
  private Instant fechaCese;
  private String observaciones;
  private EstadoEmpresa estado;
  private boolean activo;
}
