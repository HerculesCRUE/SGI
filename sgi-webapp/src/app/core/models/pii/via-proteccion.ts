import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { I18nFieldValue } from '@core/i18n/i18n-field';

export interface IViaProteccion {
  id: number;
  nombre: I18nFieldValue[];
  descripcion: I18nFieldValue[];
  tipoPropiedad: TipoPropiedad;
  mesesPrioridad: number;
  paisEspecifico: boolean;
  extensionInternacional: boolean;
  variosPaises: boolean;
  activo: boolean;
}
