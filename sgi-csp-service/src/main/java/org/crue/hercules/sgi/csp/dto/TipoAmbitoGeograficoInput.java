package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;

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
public class TipoAmbitoGeograficoInput implements Serializable {
  @NotEmpty
  @Size(max = TipoAmbitoGeografico.NOMBRE_LENGTH)
  private String nombre;

}
