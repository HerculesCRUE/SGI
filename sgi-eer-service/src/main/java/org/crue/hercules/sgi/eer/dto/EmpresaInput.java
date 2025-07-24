package org.crue.hercules.sgi.eer.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.Empresa.EstadoEmpresa;
import org.crue.hercules.sgi.eer.model.Empresa.TipoEmpresa;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

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

  private List<I18nFieldValueDto> nombreRazonSocial;

  @Size(max = Empresa.REFERENCIAS_LENGTH)
  private String entidadRef;

  @NotEmpty
  private List<I18nFieldValueDto> objetoSocial;

  @NotEmpty
  private List<I18nFieldValueDto> conocimientoTecnologia;

  @Size(max = Empresa.NUMERO_PROTOCOLO_LENGTH)
  private String numeroProtocolo;

  private List<I18nFieldValueDto> notario;

  private Instant fechaConstitucion;

  private Instant fechaAprobacionCG;

  private Instant fechaIncorporacion;

  private Instant fechaDesvinculacion;

  private Instant fechaCese;

  private List<I18nFieldValueDto> observaciones;

  @NotNull
  private EstadoEmpresa estado;

}
