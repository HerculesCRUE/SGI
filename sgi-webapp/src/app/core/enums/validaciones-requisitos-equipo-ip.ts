import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum ValidacionRequisitosEquipoIp {
  NO_NIVEL_ACADEMICO = 'NO_NIVEL_ACADEMICO',
  FECHA_OBTENCION_NIVEL_ACADEMICO_MAX = 'FECHA_OBTENCION_NIVEL_ACADEMICO_MAX',
  FECHA_OBTENCION_NIVEL_ACADEMICO_MIN = 'FECHA_OBTENCION_NIVEL_ACADEMICO_MIN',
  FECHA_OBTENCION_NIVEL_ACADEMICO_DESCONOCIDA = 'FECHA_OBTENCION_NIVEL_ACADEMICO_DESCONOCIDA',
  VINCULACION_UNIVERSIDAD = 'VINCULACION_UNIVERSIDAD',
  NO_CATEGORIA_PROFESIONAL = 'NO_CATEGORIA_PROFESIONAL',
  FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MAX = 'FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MAX',
  FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MIN = 'FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MIN',
  FECHA_OBTENCION_CATEGORIA_PROFESIONAL_DESCONOCIDA = 'FECHA_OBTENCION_CATEGORIA_PROFESIONAL_DESCONOCIDA',
  EDAD_DESCONOCIDA = 'EDAD_DESCONOCIDA',
  EDAD_MAX = 'EDAD_MAX',
  SEXO = 'SEXO'
}

export const VALIDACION_REQUISITOS_EQUIPO_IP_MAP: Map<ValidacionRequisitosEquipoIp, string> = new Map([
  [ValidacionRequisitosEquipoIp.NO_NIVEL_ACADEMICO, marker('csp.validacion-requisitos-equipo-ip.NO_NIVEL_ACADEMICO')],
  [ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_MAX, marker('csp.validacion-requisitos-equipo-ip.FECHA_OBTENCION_NIVEL_ACADEMICO_MAX')],
  [ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_MIN, marker('csp.validacion-requisitos-equipo-ip.FECHA_OBTENCION_NIVEL_ACADEMICO_MIN')],
  [ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_DESCONOCIDA, marker('csp.validacion-requisitos-equipo-ip.FECHA_OBTENCION_NIVEL_ACADEMICO_DESCONOCIDA')],
  [ValidacionRequisitosEquipoIp.VINCULACION_UNIVERSIDAD, marker('csp.validacion-requisitos-equipo-ip.VINCULACION_UNIVERSIDAD')],
  [ValidacionRequisitosEquipoIp.NO_CATEGORIA_PROFESIONAL, marker('csp.validacion-requisitos-equipo-ip.NO_CATEGORIA_PROFESIONAL')],
  [ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MAX, marker('csp.validacion-requisitos-equipo-ip.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MAX')],
  [ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MIN, marker('csp.validacion-requisitos-equipo-ip.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MIN')],
  [ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_DESCONOCIDA, marker('csp.validacion-requisitos-equipo-ip.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_DESCONOCIDA')],
  [ValidacionRequisitosEquipoIp.EDAD_DESCONOCIDA, marker('csp.validacion-requisitos-equipo-ip.EDAD_DESCONOCIDA')],
  [ValidacionRequisitosEquipoIp.EDAD_MAX, marker('csp.validacion-requisitos-equipo-ip.EDAD_MAX')],
  [ValidacionRequisitosEquipoIp.SEXO, marker('csp.validacion-requisitos-equipo-ip.SEXO')]
]);
