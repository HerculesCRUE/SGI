import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IDocumento } from '../sgdoc/documento';
import { IMemoria } from './memoria';
import { ITipoDocumento } from './tipo-documento';

export interface IDocumentacionMemoria {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoria;
  /** TIpo de documento */
  tipoDocumento: ITipoDocumento;
  /** Nombre */
  nombre: I18nFieldValue[];
  /** Ref del documento */
  documento: IDocumento;
}
