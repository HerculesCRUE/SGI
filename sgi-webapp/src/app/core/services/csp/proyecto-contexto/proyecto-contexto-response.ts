import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IAreaTematicaResponse } from '@core/services/csp/area-tematica/area-tematica-response';
import { PropiedadResultados } from '../../../models/csp/proyecto-contexto';

export interface IProyectoContextoResponse {
  id: number;
  proyectoId: number;
  objetivos: I18nFieldValueResponse[];
  intereses: I18nFieldValueResponse[];
  resultadosPrevistos: I18nFieldValueResponse[];
  propiedadResultados: PropiedadResultados;
  areaTematica: IAreaTematicaResponse;
}
