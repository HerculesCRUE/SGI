import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IProyectoSge } from '../sge/proyecto-sge';
import { Tipo } from './grupo-tipo';
import { ISolicitud } from './solicitud';

export interface IGrupo {
  id: number;
  nombre: I18nFieldValue[];
  fechaInicio: DateTime;
  fechaFin: DateTime;
  proyectoSge: IProyectoSge;
  solicitud: ISolicitud;
  codigo: string;
  tipo: Tipo;
  especialInvestigacion: boolean;
  departamentoOrigenRef: string;
  resumen: I18nFieldValue[];
  activo: boolean;
}
