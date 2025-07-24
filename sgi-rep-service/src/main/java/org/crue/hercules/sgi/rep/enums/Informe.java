package org.crue.hercules.sgi.rep.enums;

import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;

public enum Informe {
  // ETI
  ETI_ACTA("rep-eti-acta-docx", true),
  ETI_BLOQUE_APARTADO("rep-eti-bloque-apartado-docx", true),
  ETI_BLOQUE_APARTADO_ACTA("rep-eti-bloque-apartado-acta-docx", true),
  ETI_BLOQUE_APARTADO_FICHA_EVALUADOR("rep-eti-bloque-apartado-ficha-evaluador-docx", true),
  ETI_EVALUACION_FAVORABLE_MODIFICACION("rep-eti-evaluacion-favorable-memoria-modificacion-docx", true),
  ETI_EVALUACION_FAVORABLE_NUEVA("rep-eti-evaluacion-favorable-memoria-nueva-docx", true),
  ETI_EVALUACION_FAVORABLE_RATIFICACION("rep-eti-evaluacion-favorable-memoria-ratificacion-docx", true),
  ETI_EVALUACION("rep-eti-evaluacion-docx", true),
  ETI_FICHA_EVALUADOR("rep-eti-ficha-evaluador-docx", true),
  ETI_EVALUACION_RETROSPECTIVA("rep-eti-evaluacion-retrospectiva-docx", true),
  // CSP
  CSP_AUTORIZACION_PROYECTO_EXTERNO("rep-csp-certificado-autorizacion-proyecto-externo-docx", true),
  // PRC
  PRC_DETALLE_GRUPO("rep-prc-detalle-grupo-docx", true),
  PRC_DETALLE_PRODUCCION_INVESTIGADOR("rep-prc-detalle-produccion-investigador-docx", true),
  PRC_RESUMEN_PUNTUACION_GRUPOS("rep-prc-resumen-puntuacion-grupos-docx", true);

  private final String resourceName;
  private final boolean localized;

  private Informe(String resourceName, boolean localized) {
    this.resourceName = resourceName;
    this.localized = localized;
  }

  public String getResourceName() {
    if (localized) {
      return this.resourceName + "-" + SgiReportContextHolder.getLanguage().getCode();
    }
    return this.resourceName;
  }
}
