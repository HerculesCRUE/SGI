import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoDocumento, ITipoFase } from './tipos-configuracion';

export interface IProyectoDocumento {
  id: number;
  proyectoId: number;
  nombre: I18nFieldValue[];
  documentoRef: string;
  tipoFase: ITipoFase;
  tipoDocumento: ITipoDocumento;
  comentario: I18nFieldValue[];
  visible: boolean;
}
