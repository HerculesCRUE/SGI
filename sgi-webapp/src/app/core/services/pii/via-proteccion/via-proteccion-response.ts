import { TipoPropiedad } from "@core/enums/tipo-propiedad";
import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IViaProteccionResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: I18nFieldValueResponse[];
  tipoPropiedad: TipoPropiedad;
  mesesPrioridad: number;
  paisEspecifico: boolean;
  extensionInternacional: boolean;
  variosPaises: boolean;
  activo: boolean;
}
