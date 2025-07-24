package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.csp.model.LineaInvestigacionNombre;

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
  private List<LineaInvestigacionNombre> nombre;

}
