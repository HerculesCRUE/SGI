package org.crue.hercules.sgi.eer.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

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
public class EmpresaComposicionSociedadOutput implements Serializable {

  private Long id;
  private String miembroSociedadPersonaRef;
  private String miembroSociedadEmpresaRef;
  private Instant fechaInicio;
  private Instant fechaFin;
  private BigDecimal participacion;
  private TipoAportacion tipoAportacion;
  private BigDecimal capitalSocial;
}
