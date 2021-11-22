package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.SectorLicenciado;

public class SectorLicenciadoNotFoundException extends PiiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public SectorLicenciadoNotFoundException(Long sectorLicenciadoId) {
    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(SectorLicenciado.class))
        .parameter("id", sectorLicenciadoId).build());
  }
}
