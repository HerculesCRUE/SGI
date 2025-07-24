import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoDocumento, ITipoFase } from './tipos-configuracion';

export interface IConvocatoriaDocumento {
  id: number;
  convocatoriaId: number;
  nombre: I18nFieldValue[];
  documentoRef: string;
  tipoFase: ITipoFase;
  tipoDocumento: ITipoDocumento;
  publico: boolean;
  observaciones: I18nFieldValue[];
}
