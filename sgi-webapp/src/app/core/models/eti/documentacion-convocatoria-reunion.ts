import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IDocumento } from '../sgdoc/documento';
import { IConvocatoriaReunion } from './convocatoria-reunion';

export interface IDocumentacionConvocatoriaReunion {
  /** Id */
  id: number;
  /** ConvocatoriaReunion */
  convocatoriaReunion: IConvocatoriaReunion;
  /** nombre */
  nombre: I18nFieldValue[];
  /** Ref del documento */
  documento: IDocumento;
}
