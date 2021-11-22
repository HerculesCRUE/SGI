import { DateTime } from 'luxon';
import { IProyecto } from '../csp/proyecto';
import { IPais } from '../sgo/pais';
import { IInvencion } from './invencion';
import { ISectorAplicacion } from './sector-aplicacion';

export interface ISectorLicenciado {
  id: number;
  fechaInicioLicencia: DateTime;
  fechaFinLicencia: DateTime;
  invencion: IInvencion;
  sectorAplicacion: ISectorAplicacion;
  contrato: IProyecto;
  pais: IPais;
  exclusividad: boolean;
}
