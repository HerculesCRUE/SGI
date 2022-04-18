package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.LineaInvestigacion;

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
public class LineaInvestigacionInput implements Serializable {
  @NotEmpty
  @Size(max = LineaInvestigacion.NOMBRE_LENGTH)
  private String nombre;

}
