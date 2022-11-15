package org.crue.hercules.sgi.rep.dto.eti;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class ActaComentariosReportOutput {
  private List<ActaComentariosMemoriaReportOutput> comentariosMemoria;
}
