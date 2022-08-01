package org.crue.hercules.sgi.eer.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.eer.model.Empresa;
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
public class EmpresaInput implements Serializable {

  @NotNull
  private Instant fechaSolicitud;

  @NotNull
  private TipoEmpresa tipoEmpresa;

  @Size(max = Empresa.REFERENCIAS_LENGTH)
  private String solicitanteRef;

  @Size(max = Empresa.NOMBRE_RAZON_SOCIAL_LENGTH)
  private String nombreRazonSocial;

  @Size(max = Empresa.REFERENCIAS_LENGTH)
  private String entidadRef;

  @Size(max = Empresa.OBJETO_SOCIAL_LENGTH)
  @NotBlank
  private String objetoSocial;

  @Size(max = Empresa.CONOCIMIENTO_TECNOLOGIA_LENGTH)
  @NotBlank
  private String conocimientoTecnologia;

  @Size(max = Empresa.NUMERO_PROTOCOLO_LENGTH)
  private String numeroProtocolo;

  @Size(max = Empresa.NOTARIO_LENGTH)
  private String notario;

  private Instant fechaConstitucion;

  private Instant fechaAprobacionCG;

  private Instant fechaIncorporacion;

  private Instant fechaDesvinculacion;

  private Instant fechaCese;

  @Size(max = Empresa.OBSERVACIONES_LENGTH)
  private String observaciones;

  @NotNull
  private EstadoEmpresa estado;

}
