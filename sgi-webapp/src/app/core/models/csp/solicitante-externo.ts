import { DateTime } from 'luxon';
import { IComunidadAutonoma } from '../sgo/comunidad-autonoma';
import { IPais } from '../sgo/pais';
import { IProvincia } from '../sgo/provincia';
import { ISexo } from '../sgp/sexo';
import { ITipoDocumento } from '../sgp/tipo-documento';

export interface ISolicitanteExterno {
  id: number;
  solicitudId: number;
  nombre: string;
  apellidos: string;
  tipoDocumento: ITipoDocumento;
  numeroDocumento: string;
  sexo: ISexo;
  fechaNacimiento: DateTime;
  paisNacimiento: IPais;
  telefono: string;
  email: string;
  direccion: string;
  paisContacto: IPais;
  comunidad: IComunidadAutonoma;
  provincia: IProvincia;
  ciudad: string;
  codigoPostal: string;
}
