import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IRequerimientoJustificacion } from './requerimiento-justificacion';

export interface IIncidenciaDocumentacionRequerimiento {
  id: number;
  requerimientoJustificacion: IRequerimientoJustificacion;
  nombreDocumento: I18nFieldValue[];
  incidencia: I18nFieldValue[];
  alegacion: I18nFieldValue[];
}
