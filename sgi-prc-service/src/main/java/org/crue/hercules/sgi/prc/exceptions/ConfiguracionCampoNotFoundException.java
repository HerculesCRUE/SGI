package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;

/**
 * ConfiguracionCampoNotFoundException
 */
public class ConfiguracionCampoNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public ConfiguracionCampoNotFoundException(Long idConfiguracionCampo) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ConfiguracionCampo.class))
        .parameter("id", idConfiguracionCampo).build());
  }
}
