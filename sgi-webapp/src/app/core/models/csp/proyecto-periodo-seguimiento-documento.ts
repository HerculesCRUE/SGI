import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoDocumento } from './tipos-configuracion';

export interface IProyectoPeriodoSeguimientoDocumento {
  id: number;
  proyectoPeriodoSeguimientoId: number;
  nombre: I18nFieldValue[];
  documentoRef: string;
  tipoDocumento: ITipoDocumento;
  visible: boolean;
  comentario: I18nFieldValue[];
}
