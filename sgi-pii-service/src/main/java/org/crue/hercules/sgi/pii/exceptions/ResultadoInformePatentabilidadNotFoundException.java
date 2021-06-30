package org.crue.hercules.sgi.pii.exceptions;

public class ResultadoInformePatentabilidadNotFoundException extends PiiNotFoundException {

  private static final long serialVersionUID = 1L;

  public ResultadoInformePatentabilidadNotFoundException(Long resultadoInformePatentabilidadId) {
    super("ResultadoInformePatentabilidad " + resultadoInformePatentabilidadId + " does not exist.");

  }
}
