import { IComunidadAutonoma } from '../sgo/comunidad-autonoma';
import { IPais } from '../sgo/pais';
import { IProvincia } from '../sgo/provincia';
import { IEmail } from './email';

export interface IDatosContacto {
  paisContacto: IPais;
  comAutonomaContacto: IComunidadAutonoma;
  provinciaContacto: IProvincia;
  ciudadContacto: string;
  codigoPostalContacto: string;
  emails: IEmail[];
  telefonos: string[];
  moviles: string[];
  direccionContacto: string;
}
