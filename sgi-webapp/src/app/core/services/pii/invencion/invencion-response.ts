import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoProteccionResponse } from '../tipo-proteccion/tipo-proteccion-response';

export interface IInvencionResponse {
  id: number;
  titulo: I18nFieldValueResponse[];
  fechaComunicacion: string;
  descripcion: I18nFieldValueResponse[];
  comentarios: I18nFieldValueResponse[];
  proyectoRef: string;
  tipoProteccion: ITipoProteccionResponse;
  activo: boolean;
}