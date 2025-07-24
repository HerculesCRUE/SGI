import { TipoPropiedad } from "@core/enums/tipo-propiedad";
import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface ITipoProteccionResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: I18nFieldValueResponse[];
  tipoPropiedad: TipoPropiedad;
  padre?: ITipoProteccionResponse;
  activo: boolean;
}
