import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoDocumento } from './tipos-configuracion';

export interface IProyectoSocioPeriodoJustificacionDocumento {
  id: number;
  proyectoSocioPeriodoJustificacionId: number;
  nombre: I18nFieldValue[];
  documentoRef: string;
  tipoDocumento: ITipoDocumento;
  comentario: I18nFieldValue[];
  visible: boolean;
}
