import { TipoPropiedad } from '@core/models/pii/tipo-proteccion';


export interface ITipoProteccionResponse {
  id: number;
  nombre: string;
  descripcion: string;
  tipoPropiedad: TipoPropiedad;
  padre?: ITipoProteccionResponse;
  activo: boolean;
}
