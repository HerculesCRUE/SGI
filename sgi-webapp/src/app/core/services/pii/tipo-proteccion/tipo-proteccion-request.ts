import { TipoPropiedad } from '@core/enums/tipo-propiedad';

export interface ITipoProteccionRequest {

  nombre: string;
  descripcion: string;
  tipoPropiedad: TipoPropiedad;
  padreId?: number;

}
