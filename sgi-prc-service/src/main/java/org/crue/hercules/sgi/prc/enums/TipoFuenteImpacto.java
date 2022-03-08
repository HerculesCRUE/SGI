package org.crue.hercules.sgi.prc.enums;

import java.util.stream.Stream;

import org.crue.hercules.sgi.prc.exceptions.TipoFuenteImpactoNotFoundException;

/** TipoFuenteImpacto */
public enum TipoFuenteImpacto {
  /** WOS (JCR) */
  WOS_JCR("000"),
  /** SCOPUS (SJR) */
  SCOPUS_SJR("010"),
  /** INRECS */
  INRECS("020"),
  /** BCI */
  BCI("BCI"),
  /** ICEE */
  ICEE("ICEE"),
  /** DIALNET */
  DIALNET("DIALNET"),
  /** CitEC */
  CITEC("CITEC"),
  /** SCIMAGO */
  SCIMAGO("SCIMAGO"),
  /** ERIH */
  ERIH("ERIH"),
  /** MIAR */
  MIAR("MIAR"),
  /** FECYT */
  FECYT("FECYT"),
  /** GII-GRIN-SCIE */
  GII_GRIN_SCIE("GII-GRIN-SCIE"),
  /** CORE */
  CORE("CORE"),
  /** Otros */
  OTHERS("OTHERS");

  private String internValue;

  private TipoFuenteImpacto(String internValue) {
    this.internValue = internValue;
  }

  public String getInternValue() {
    return internValue;
  }

  public static TipoFuenteImpacto getByInternValue(String internValue) {
    try {
      return Stream.of(TipoFuenteImpacto.values())
          .filter(internValueValue -> internValueValue.getInternValue().equalsIgnoreCase(internValue))
          .findFirst()
          .orElseThrow(() -> new TipoFuenteImpactoNotFoundException(internValue));

    } catch (Exception e) {
      throw new TipoFuenteImpactoNotFoundException(internValue);
    }
  }
}