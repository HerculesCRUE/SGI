import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoDocumento, ITipoFase } from './tipos-configuracion';

export interface ISolicitudDocumento {
  id: number;
  solicitudId: number;
  comentario: I18nFieldValue[];
  documentoRef: string;
  nombre: I18nFieldValue[];
  tipoDocumento: ITipoDocumento;
  tipoFase: ITipoFase;
}
