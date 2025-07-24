import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoAmbitoGeografico, ITipoOrigenFuenteFinanciacion } from './tipos-configuracion';

export interface IFuenteFinanciacion {
  id: number;
  nombre: I18nFieldValue[];
  descripcion: I18nFieldValue[];
  fondoEstructural: boolean;
  tipoAmbitoGeografico: ITipoAmbitoGeografico;
  tipoOrigenFuenteFinanciacion: ITipoOrigenFuenteFinanciacion;
  activo: boolean;
}
