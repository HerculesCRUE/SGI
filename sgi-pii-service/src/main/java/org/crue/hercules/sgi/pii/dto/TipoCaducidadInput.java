package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.TipoCaducidad;

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
public class TipoCaducidadInput implements Serializable {

  @NotEmpty
  @Size(max = TipoCaducidad.DESCRIPCION_MAX_LENGTH)
  private String descripcion;

}
