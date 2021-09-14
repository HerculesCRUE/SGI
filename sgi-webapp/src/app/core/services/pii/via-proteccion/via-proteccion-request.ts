import { TipoPropiedad } from "@core/enums/tipo-propiedad";

export interface IViaProteccionRequest {

  nombre: string;
  descripcion: string;
  tipoPropiedad: TipoPropiedad;
  mesesPrioridad: number;
  paisEspecifico: boolean;
  extensionInternacional: boolean;
  variosPaises: boolean;
}
