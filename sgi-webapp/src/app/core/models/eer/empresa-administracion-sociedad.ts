import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IEmpresaExplotacionResultados } from './empresa-explotacion-resultados';


export enum TipoAdministracion {
  ADMINISTRADOR_UNICO = 'ADMINISTRADOR_UNICO',
  ADMINISTRADOR_SOLIDARIO = 'ADMINISTRADOR_SOLIDARIO',
  ADMINISTRADOR_MANCOMUNADO = 'ADMINISTRADOR_MANCOMUNADO',
  CONSEJO_ADMINISTRACION = 'CONSEJO_ADMINISTRACION'
}

export const TIPO_ADMINISTRACION_SOCIEDAD_MAP: Map<TipoAdministracion, string> = new Map([
  [TipoAdministracion.ADMINISTRADOR_UNICO, marker('eer.empresa-administracion-sociedad.tipo-administracion.ADMINISTRADOR_UNICO')],
  [TipoAdministracion.ADMINISTRADOR_SOLIDARIO, marker('eer.empresa-administracion-sociedad.tipo-administracion.ADMINISTRADOR_SOLIDARIO')],
  [TipoAdministracion.ADMINISTRADOR_MANCOMUNADO, marker('eer.empresa-administracion-sociedad.tipo-administracion.ADMINISTRADOR_MANCOMUNADO')],
  [TipoAdministracion.CONSEJO_ADMINISTRACION, marker('eer.empresa-administracion-sociedad.tipo-administracion.CONSEJO_ADMINISTRACION')]
]);

export interface IEmpresaAdministracionSociedad {
  id: number;
  miembroEquipoAdministracion: IPersona;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  tipoAdministracion: TipoAdministracion;
  empresa: IEmpresaExplotacionResultados;
}
