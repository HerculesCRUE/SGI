import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoria } from '../csp/convocatoria';
import { IProyecto } from '../csp/proyecto';
import { IInvencion } from '../pii/invencion';

export interface IRelacion {
  id: number;
  tipoEntidadOrigen: TipoEntidad;
  tipoEntidadDestino: TipoEntidad;
  entidadOrigen: IConvocatoria | IInvencion | IProyecto;
  entidadDestino: IConvocatoria | IInvencion | IProyecto;
  observaciones: string;
}

export enum TipoEntidad {
  PROYECTO = 'PROYECTO',
  CONVOCATORIA = 'CONVOCATORIA',
  INVENCION = 'INVENCION'
}

export const TIPO_ENTIDAD_MAP: Map<TipoEntidad, string> = new Map([
  [TipoEntidad.PROYECTO, marker('rel.relacion.tipo-entidad.PROYECTO')],
  [TipoEntidad.CONVOCATORIA, marker('rel.relacion.tipo-entidad.CONVOCATORIA')],
  [TipoEntidad.INVENCION, marker('rel.relacion.tipo-entidad.INVENCION')]
]);

export const TIPO_ENTIDAD_HREF_MAP: Map<TipoEntidad, '/csp/convocatoria' | '/pii/invenciones' | '/csp/proyectos'> = new Map([
  [TipoEntidad.CONVOCATORIA, '/csp/convocatoria'],
  [TipoEntidad.INVENCION, '/pii/invenciones'],
  [TipoEntidad.PROYECTO, '/csp/proyectos'],
]);
