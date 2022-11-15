package org.crue.hercules.sgi.rep.dto.eti;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class ActaComentariosMemoriaReportOutput extends BloquesReportOutput {
  private String numReferenciaMemoria;
  private Integer numComentarios;
}
