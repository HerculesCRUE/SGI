import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { I18nFieldValue } from '@core/i18n/i18n-field';

export interface ITipoProteccion {
  id: number;
  nombre: I18nFieldValue[];
  descripcion: I18nFieldValue[];
  tipoPropiedad: TipoPropiedad;
  padre: ITipoProteccion;
  activo: boolean;
}
