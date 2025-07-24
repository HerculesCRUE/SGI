import { marker } from "@biesbjerg/ngx-translate-extract-marker";
import { I18nFieldValue } from "@core/i18n/i18n-field";
import { DateTime } from "luxon";
import { IProyectoSge } from "../sge/proyecto-sge";

export interface IRelacionEjecucionEconomica {
  id: number;
  nombre: I18nFieldValue[];
  codigoExterno?: string;
  codigoInterno?: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  proyectoSge: IProyectoSge;
  tipoEntidad: TipoEntidad;
  fechaFinDefinitiva: DateTime;
}

export enum TipoEntidad {
  GRUPO = 'GRUPO',
  PROYECTO = 'PROYECTO'
}

export const TIPO_ENTIDAD_MAP: Map<TipoEntidad, string> = new Map([
  [TipoEntidad.GRUPO, marker('csp.relacion-ejecucion-economica.tipo-entidad.GRUPO')],
  [TipoEntidad.PROYECTO, marker('csp.relacion-ejecucion-economica.tipo-entidad.PROYECTO')]
]);
