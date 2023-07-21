import { IComunidadAutonoma } from '@core/models/sgo/comunidad-autonoma';
import { IPais } from '@core/models/sgo/pais';
export interface IDatosPersonalesResponse {
  id: string;
  paisNacimiento: IPais;
  fechaNacimiento: string;
  ciudadNacimiento: string;
  comAutonomaNacimiento: IComunidadAutonoma;
}
