package org.crue.hercules.sgi.eer.dto;

import java.util.Collection;

import org.crue.hercules.sgi.eer.model.EmpresaDocumentoComentarios;
import org.crue.hercules.sgi.eer.model.EmpresaDocumentoNombre;

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
public class EmpresaDocumentoOutput {
  private Long id;
  private Collection<EmpresaDocumentoNombre> nombre;
  private String documentoRef;
  private Collection<EmpresaDocumentoComentarios> comentarios;
  private Long empresaId;
  private TipoDocumentoOutput tipoDocumento;
}
