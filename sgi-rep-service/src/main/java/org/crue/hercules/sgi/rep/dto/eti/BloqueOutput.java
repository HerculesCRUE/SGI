package org.crue.hercules.sgi.rep.dto.eti;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloqueOutput {
  private Long id;
  private String nombre;
  private Integer orden;

  private List<ApartadoOutput> apartados;
}
