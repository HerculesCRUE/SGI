import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoDocumento } from './tipos-configuracion';

export interface IDocumentoRequeridoSolicitud {
  id: number;
  configuracionSolicitudId: number;
  tipoDocumento: ITipoDocumento;
  observaciones: I18nFieldValue[];
}
