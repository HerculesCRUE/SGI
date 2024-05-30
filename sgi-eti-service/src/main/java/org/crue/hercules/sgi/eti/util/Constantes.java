package org.crue.hercules.sgi.eti.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constantes {

  // Estado retrospectiva
  public static final Long ESTADO_RETROSPECTIVA_PENDIENTE = 1L;
  public static final Long ESTADO_RETROSPECTIVA_COMPLETADA = 2L;
  public static final Long ESTADO_RETROSPECTIVA_EN_SECRETARIA = 3L;
  public static final Long ESTADO_RETROSPECTIVA_EN_EVALUACION = 4L;
  public static final Long ESTADO_RETROSPECTIVA_FIN_EVALUACION = 5L;

  // Tipo convocatoria reunion
  public static final Long TIPO_CONVOCATORIA_REUNION_ORDINARIA = 1L;
  public static final Long TIPO_CONVOCATORIA_REUNION_EXTRAORDINARIA = 2L;
  public static final Long TIPO_CONVOCATORIA_REUNION_SEGUIMIENTO = 3L;

  // Tipo estado memoria
  public static final Long TIPO_ESTADO_MEMORIA_EN_ELABORACION = 1L;
  public static final Long TIPO_ESTADO_MEMORIA_COMPLETADA = 2L;
  public static final Long TIPO_ESTADO_MEMORIA_EN_SECRETARIA = 3L;

  // Tipo estado acta
  public static final Long TIPO_ESTADO_ACTA_EN_ELABORACION = 1L;
  public static final Long TIPO_ESTADO_ACTA_FINALIZADA = 2L;

  // Tipo estado memoria con tipo int para usarlo en un switch
  public static final int ESTADO_MEMORIA_COMPLETADA = 2;
  public static final int ESTADO_MEMORIA_EN_SECRETARIA = 3;
  public static final int ESTADO_MEMORIA_COMPLETADA_SEGUIMIENTO_FINAL = 16;
  public static final int ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_FINAL = 17;
  public static final int ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL = 19;
  public static final int ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_FINAL_ACLARACIONES = 18;
  public static final int ESTADO_MEMORIA_EN_ACLARACION_SEGUIMIENTO_FINAL = 21;
  public static final int ESTADO_MEMORIA_FIN_EVALUACION_SEGUIMIENTO_FINAL = 20;
  public static final int ESTADO_MEMORIA_COMPLETADA_SEGUIMIENTO_ANUAL = 11;
  public static final int ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL = 12;
  public static final int ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL = 13;
  public static final int ESTADO_MEMORIA_SOLICITUD_MODIFICACION = 15;
  public static final int ESTADO_MEMORIA_FIN_EVALUACION_SEGUIMIENTO_ANUAL = 14;
  public static final int ESTADO_MEMORIA_EN_EVALUACION = 5;
  public static final int ESTADO_MEMORIA_NO_PROCEDE_EVALUAR = 8;
  public static final int ESTADO_MEMORIA_EN_SECRETARIA_REVISION_MINIMA = 4;
  public static final int ESTADO_MEMORIA_FIN_EVALUACION = 9;
  public static final int ESTADO_MEMORIA_PENDIENTE_CORRECCIONES = 7;
  public static final int ESTADO_MEMORIA_FAVORABLE_PENDIENTE_MOD_MINIMAS = 6;

  // Formularios
  public static final Long FORMULARIO_M10 = 1L;
  public static final Long FORMULARIO_M20 = 2L;
  public static final Long FORMULARIO_M30 = 3L;
  public static final Long FORMULARIO_ANUAL = 4L;
  public static final Long FORMULARIO_FINAL = 5L;
  public static final Long FORMULARIO_RETROSPECTIVA = 6L;

  // Comites
  public static final Long COMITE_CEI = 1L;
  public static final Long COMITE_CEEA = 2L;
  public static final Long COMITE_CBE = 3L;

}