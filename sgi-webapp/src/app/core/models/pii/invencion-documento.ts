import { DateTime } from 'luxon';
import { IDocumento } from '../sgdoc/documento';
import { I18nFieldValue } from '@core/i18n/i18n-field';

export interface IInvencionDocumento {
  id: number;
  fechaAnadido: DateTime;
  nombre: I18nFieldValue[];
  documento: IDocumento;
  invencionId: number;
}