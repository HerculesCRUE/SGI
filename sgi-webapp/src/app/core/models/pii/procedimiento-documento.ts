import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IDocumento } from '../sgdoc/documento';
import { IProcedimiento } from './procedimiento';

export interface IProcedimientoDocumento {

  id: number;
  nombre: I18nFieldValue[];
  documento: IDocumento;
  procedimiento: IProcedimiento;

}
