import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IAutorizacion } from './autorizacion';

export interface ICertificadoAutorizacion {
  id: number;
  autorizacion: IAutorizacion;
  documentoRef: I18nFieldValue[];
  nombre: I18nFieldValue[];
  visible: boolean;
}
