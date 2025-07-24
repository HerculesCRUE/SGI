import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';

export interface ITipoProteccionRequest {
  id: number;
  nombre: I18nFieldValueRequest[];
  descripcion: I18nFieldValueRequest[];
  tipoPropiedad: TipoPropiedad;
  padreId?: number;

}
