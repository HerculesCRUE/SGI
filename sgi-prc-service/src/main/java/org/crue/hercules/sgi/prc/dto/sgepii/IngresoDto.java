package org.crue.hercules.sgi.prc.dto.sgepii;

import java.io.Serializable;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class IngresoDto implements Serializable {
  public enum Tipo {
    Gasto,
    Ingreso
  }

  private String id;
  private Tipo tipo;
  private HashMap<String, Object> columnas;
}