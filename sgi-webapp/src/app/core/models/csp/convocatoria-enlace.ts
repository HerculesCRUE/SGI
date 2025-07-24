import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoEnlace } from './tipos-configuracion';

export interface IConvocatoriaEnlace {
  /** id */
  id: number;
  /** tipoEnlace */
  tipoEnlace: ITipoEnlace;
  /** activo */
  activo: boolean;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** URL */
  url: string;
  /** descripcion */
  descripcion: I18nFieldValue[];
}
