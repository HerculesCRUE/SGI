import { TipoPropiedad } from "@core/enums/tipo-propiedad";

export interface IViaProteccionResponse {
  id: number;
  nombre: string;
  descripcion: string;
  tipoPropiedad: TipoPropiedad;
  mesesPrioridad: number;
  paisEspecifico: boolean;
  extensionInternacional: boolean;
  variosPaises: boolean;
  activo: boolean;
}
