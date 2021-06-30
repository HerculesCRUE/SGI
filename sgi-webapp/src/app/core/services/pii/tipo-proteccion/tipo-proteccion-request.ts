import { TipoPropiedad } from '@core/models/pii/tipo-proteccion';


export interface ITipoProteccionRequest {

  nombre: string;
  descripcion: string;
  tipoPropiedad: TipoPropiedad;
  padreId?: number;

}


