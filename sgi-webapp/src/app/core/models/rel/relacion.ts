import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { Module } from '@core/module';
import { CSP_ROUTE_NAMES } from 'src/app/module/csp/csp-route-names';
import { IGrupoWithTitulo } from 'src/app/module/csp/proyecto/proyecto-formulario/proyecto-relaciones/proyecto-relaciones.fragment';
import { PII_ROUTE_NAMES } from 'src/app/module/pii/pii-route-names';
import { IConvocatoria } from '../csp/convocatoria';
import { IProyecto } from '../csp/proyecto';
import { IInvencion } from '../pii/invencion';

export interface IRelacion {
  id: number;
  tipoEntidadOrigen: TipoEntidad;
  tipoEntidadDestino: TipoEntidad;
  entidadOrigen: IConvocatoria | IInvencion | IProyecto | IGrupoWithTitulo;
  entidadDestino: IConvocatoria | IInvencion | IProyecto | IGrupoWithTitulo;
  observaciones: I18nFieldValue[];
}

export enum TipoEntidad {
  CONVOCATORIA = 'CONVOCATORIA',
  GRUPO = 'GRUPO',
  INVENCION = 'INVENCION',
  PROYECTO = 'PROYECTO'
}

export const TIPO_ENTIDAD_MAP: Map<TipoEntidad, string> = new Map([
  [TipoEntidad.CONVOCATORIA, marker('rel.relacion.tipo-entidad.CONVOCATORIA')],
  [TipoEntidad.GRUPO, marker('rel.relacion.tipo-entidad.GRUPO')],
  [TipoEntidad.INVENCION, marker('rel.relacion.tipo-entidad.INVENCION')],
  [TipoEntidad.PROYECTO, marker('rel.relacion.tipo-entidad.PROYECTO')]
]);

export const TIPO_ENTIDAD_HREF_MAP: Map<TipoEntidad, string> = new Map([
  [TipoEntidad.CONVOCATORIA, `/${Module.CSP.path}/${CSP_ROUTE_NAMES.CONVOCATORIA}`],
  [TipoEntidad.GRUPO, `/${Module.CSP.path}/${CSP_ROUTE_NAMES.GRUPO}`],
  [TipoEntidad.INVENCION, `/${Module.PII.path}/${PII_ROUTE_NAMES.INVENCION}`],
  [TipoEntidad.PROYECTO, `/${Module.CSP.path}/${CSP_ROUTE_NAMES.PROYECTO}`],
]);
