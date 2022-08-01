package org.crue.hercules.sgi.prc.enums;

import java.util.stream.Stream;

/** EpigrafeCVN */
public enum EpigrafeCVN {
  /** Publicaciones, documentos científicos y técnicos */
  E060_010_010_000("060.010.010.000", "Publicaciones, documentos científicos y técnicos"),
  /** Trabajos presentados en congresos nacionales o internacionales */
  E060_010_020_000("060.010.020.000", "Trabajos presentados en congresos nacionales o internacionales"),
  /** Obras artísticas dirigidas */
  E050_020_030_000("050.020.030.000", "Obras artísticas dirigidas"),
  /** Consejos/comités editoriales */
  E060_030_030_000("060.030.030.000", "Consejos/comités editoriales "),
  /** Invenciones */
  E050_030_010_000("050.030.010.000", "Invenciones"),
  /** Contratos */
  E050_020_020_000("050.020.020.000", "Contratos"),
  /** Proyecto de investigación */
  E050_020_010_000("050.020.010.000", "Proyecto de investigación"),
  /** Organización actividades I+D+i */
  E060_020_030_000("060.020.030.000", "Organización actividades I+D+i"),
  /** Dirección de tesis */
  E030_040_000_000("030.040.000.000", "Dirección de tesis"),
  /** Sexenios (Periodos de actividad investigadora) */
  E060_030_070_000("060.030.070.000", "Sexenios (Periodos de actividad investigadora)");

  private String code;
  private String description;

  private EpigrafeCVN(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public static EpigrafeCVN getByCode(String code) {
    return Stream.of(EpigrafeCVN.values())
        .filter(epigrafeValue -> epigrafeValue.getCode().equalsIgnoreCase(code))
        .findFirst()
        .orElse(null);
  }
}