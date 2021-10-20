import { DateTime } from 'luxon';
import { IComunidadAutonoma } from '../sgo/comunidad-autonoma';
import { IPais } from '../sgo/pais';
export interface IDatosPersonales {
  id: string;
  paisNacimiento: IPais;
  fechaNacimiento: DateTime;
  ciudadNacimiento: string;
  comAuntonomaNacimiento: IComunidadAutonoma;
}

