package org.crue.hercules.sgi.prc.model;

import java.io.Serializable;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable;

/**
 * Base Entity.
 */
public class BaseEntity extends Auditable implements Serializable {

  public static final int ID_REF_LENGTH = 250;
  public static final int EPIGRAFE_LENGTH = 50;
  public static final int URL_LENGTH = 1000;
  public static final int DOCUMENTO_REF_LENGTH = 100;
  public static final int FIRMA_LENGTH = 100;
  public static final int PERSONA_REF_LENGTH = 50;
  public static final int NOMBRE_LENGTH = 50;
  public static final int APELLIDOS_LENGTH = 100;
  public static final int ORCID_ID_LENGTH = 100;
  public static final int CAMPO_CVN_LENGTH = 50;
  public static final int COMENTARIO_LENGTH = 2000;
  public static final int GRUPO_REF_LENGTH = 100;
  public static final int TIPO_FUENTE_IMPACTO_LENGTH = 50;
  public static final int OTRA_FUENTE_IMPACTO_LENGTH = 1000;
  public static final int VALOR_LENGTH = 250;
  public static final int PREFIJO_ENUMERADO_LENGTH = 250;
  public static final int TIPO_FORMATO_LENGTH = 50;
  public static final int TIPO_ESTADO_LENGTH = 50;
  public static final int TIPO_RANKING_LENGTH = 50;
  public static final int TIPO_MODULADOR_LENGTH = 50;
  public static final int NOMBRE_EDITORIAL_LENGTH = 100;
  public static final int AREA_REF_LENGTH = 50;
  public static final int DEPARTAMENTO_REF_LENGTH = 50;
  public static final int TIPO_RANGO_LENGTH = 50;
  public static final int TIPO_TEMPORALIDAD_LENGTH = 50;
  public static final int NOMBRE_CONVOCATORIA_LENGTH = 100;
  public static final int PARTIDA_PRESUPUESTARIA_LENGTH = 50;
  public static final int TIPO_CUANTIA_LENGTH = 50;
  public static final int TIPO_BAREMO_LENGTH = 80;
  public static final int TIPO_FUENTE_LENGTH = 50;
  public static final int TIPO_PUNTOS_LENGTH = 50;
  public static final int VALOR_MAPEO_TIPOS = 100;
  public static final int NOMBRE_CONF_BAREMOS_LENGTH = 100;
  public static final int CUARTIL_LENGTH = 2;

  /** Serial version. */
  private static final long serialVersionUID = 1L;

  /**
   * Interfaz para marcar validaciones en los create.
   */
  public interface Create {
  }

  /**
   * Interfaz para marcar validaciones en los update.
   */
  public interface Update {
  }

}
