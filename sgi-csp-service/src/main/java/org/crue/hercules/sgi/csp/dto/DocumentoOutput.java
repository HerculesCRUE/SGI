package org.crue.hercules.sgi.csp.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoOutput {

  private String documentoRef;
  private String nombre;
  private Integer version;
  private byte[] archivo;
  private LocalDateTime fechaCreacion;
  private String tipo;
  private String autorRef;

}
