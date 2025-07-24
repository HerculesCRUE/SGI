import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { Dedicacion } from '@core/models/csp/grupo-equipo';

export interface IGrupoEquipoResponse {
  id: number;
  personaRef: string;
  grupoId: number;
  fechaInicio: string;
  fechaFin: string;
  rol: {
    id: number;
    nombre: I18nFieldValueResponse[];
  };
  dedicacion: Dedicacion;
  participacion: number;
}
