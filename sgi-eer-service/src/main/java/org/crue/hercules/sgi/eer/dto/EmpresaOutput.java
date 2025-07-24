package org.crue.hercules.sgi.eer.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.eer.model.Empresa.EstadoEmpresa;
import org.crue.hercules.sgi.eer.model.Empresa.TipoEmpresa;
import org.crue.hercules.sgi.eer.model.EmpresaConocimientoTecnologia;
import org.crue.hercules.sgi.eer.model.EmpresaNombreRazonSocial;
import org.crue.hercules.sgi.eer.model.EmpresaNotario;
import org.crue.hercules.sgi.eer.model.EmpresaObjetoSocial;
import org.crue.hercules.sgi.eer.model.EmpresaObservaciones;

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
  private Collection<EmpresaNombreRazonSocial> nombreRazonSocial;
  private String entidadRef;
  private Collection<EmpresaObjetoSocial> objetoSocial;
  private Collection<EmpresaConocimientoTecnologia> conocimientoTecnologia;
  private String numeroProtocolo;
  private Collection<EmpresaNotario> notario;
  private Instant fechaConstitucion;
  private Instant fechaAprobacionCG;
  private Instant fechaIncorporacion;
  private Instant fechaDesvinculacion;
  private Instant fechaCese;
  private Collection<EmpresaObservaciones> observaciones;
  private EstadoEmpresa estado;
  private boolean activo;
}
