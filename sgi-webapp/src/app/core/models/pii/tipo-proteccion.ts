import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TipoPropiedad } from '@core/enums/tipo-propiedad';

export interface ITipoProteccion {
  id: number;
  nombre: string;
  descripcion: string;
  tipoPropiedad: TipoPropiedad;
  padre: ITipoProteccion;
  activo: boolean;
}
