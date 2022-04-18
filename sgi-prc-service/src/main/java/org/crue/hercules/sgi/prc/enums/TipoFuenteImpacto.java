package org.crue.hercules.sgi.prc.enums;

import java.util.stream.Stream;

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
  GII_GRIN_SCIE("GII_GRIN_SCIE"),
  /** CORE */
  CORE("CORE"),
  /** Otros */
  OTHERS("OTHERS");

  private String code;

  private TipoFuenteImpacto(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static TipoFuenteImpacto getByCode(String code) {
    return Stream.of(TipoFuenteImpacto.values())
        .filter(codeValue -> codeValue.getCode().equalsIgnoreCase(code))
        .findFirst()
        .orElse(null);
  }
}