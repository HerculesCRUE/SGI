import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IDocumento } from '../sgdoc/documento';
import { IEmpresaExplotacionResultados } from './empresa-explotacion-resultados';
import { ITipoDocumento } from './tipo-documento';

export interface IEmpresaDocumento {
  id: number;
  nombre: I18nFieldValue[];
  documento: IDocumento;
  comentarios: I18nFieldValue[];
  empresa: IEmpresaExplotacionResultados;
  tipoDocumento: ITipoDocumento;
  subtipoDocumento: ITipoDocumento;
}
