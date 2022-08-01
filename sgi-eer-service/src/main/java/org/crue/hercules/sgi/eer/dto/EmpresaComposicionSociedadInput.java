package org.crue.hercules.sgi.eer.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad.TipoAportacion;

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
public class EmpresaComposicionSociedadInput implements Serializable {
  private Long id;

  @Size(max = EmpresaComposicionSociedad.REF_LENGTH)
  private String miembroSociedadPersonaRef;

  @Size(max = EmpresaComposicionSociedad.REF_LENGTH)
  private String miembroSociedadEmpresaRef;

  @NotNull
  private Instant fechaInicio;

  private Instant fechaFin;

  @Min(EmpresaComposicionSociedad.PARTICIPACION_MIN)
  @Max(EmpresaComposicionSociedad.PARTICIPACION_MAX)
  @NotNull
  private BigDecimal participacion;

  @Enumerated(EnumType.STRING)
  @NotNull
  private TipoAportacion tipoAportacion;

  private BigDecimal capitalSocial;

  @NotNull
  private Long empresaId;

}
