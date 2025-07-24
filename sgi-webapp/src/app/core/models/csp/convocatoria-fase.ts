import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IConvocatoriaFaseAviso } from '@core/services/csp/convocatoria-fase/convocatoria-fase-aviso';
import { DateTime } from 'luxon';
import { ITipoFase } from './tipos-configuracion';

export interface IConvocatoriaFase {
  id: number;
  convocatoriaId: number;
  tipoFase: ITipoFase;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: I18nFieldValue[];
  aviso1: IConvocatoriaFaseAviso;
  aviso2: IConvocatoriaFaseAviso;
}
