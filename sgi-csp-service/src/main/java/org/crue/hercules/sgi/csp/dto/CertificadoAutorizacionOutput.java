package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.CertificadoAutorizacionDocumentoRef;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacionNombre;

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
public class CertificadoAutorizacionOutput implements Serializable {

  private Long id;
  private Long autorizacionId;
  private Collection<CertificadoAutorizacionDocumentoRef> documentoRef;
  private Collection<CertificadoAutorizacionNombre> nombre;
  private Boolean visible;

}
