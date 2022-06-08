package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.crue.hercules.sgi.prc.model.Modulador.TipoModulador;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ModuladorOutput implements Serializable {
  private Long id;
  private String areaRef;
  private TipoModulador tipo;
  private BigDecimal valor1;
  private BigDecimal valor2;
  private BigDecimal valor3;
  private BigDecimal valor4;
  private BigDecimal valor5;
  private Long convocatoriaBaremacionId;
}
