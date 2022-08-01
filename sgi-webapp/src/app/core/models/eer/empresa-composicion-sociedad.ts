import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IEmpresa } from '../sgemp/empresa';
import { IPersona } from '../sgp/persona';
import { IEmpresaExplotacionResultados } from './empresa-explotacion-resultados';


export enum TipoAportacion {
  DINERARIA = 'DINERARIA',
  NO_DINERARIA = 'NO_DINERARIA'
}

export const TIPO_APORTACION_COMPOSICION_SOCIEDAD_MAP: Map<TipoAportacion, string> = new Map([
  [TipoAportacion.DINERARIA, marker('eer.empresa-composicion-sociedad.tipo-aportacion.DINERARIA')],
  [TipoAportacion.NO_DINERARIA, marker('eer.empresa-composicion-sociedad.tipo-aportacion.NO_DINERARIA')]
]);

export interface IEmpresaComposicionSociedad {
  id: number;
  miembroSociedadPersona: IPersona;
  miembroSociedadEmpresa: IEmpresa;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  participacion: number;
  tipoAportacion: TipoAportacion;
  capitalSocial: number;
  empresa: IEmpresaExplotacionResultados;
}
