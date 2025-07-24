package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoConvocatoriaOutput implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String nombre;
  private Boolean activo;

}
