import { TipoPropiedad } from '@core/enums/tipo-propiedad';

export interface IViaProteccion {
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
