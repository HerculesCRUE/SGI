package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;

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
public class TipoOrigenFuenteFinanciacionInput implements Serializable {
  @NotEmpty
  @Size(max = TipoOrigenFuenteFinanciacion.NOMBRE_LENGTH)
  private String nombre;

}
