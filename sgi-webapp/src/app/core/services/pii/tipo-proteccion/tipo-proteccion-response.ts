import { TipoPropiedad } from "@core/enums/tipo-propiedad";

export interface ITipoProteccionResponse {
  id: number;
  nombre: string;
  descripcion: string;
  tipoPropiedad: TipoPropiedad;
  padre?: ITipoProteccionResponse;
  activo: boolean;
}
