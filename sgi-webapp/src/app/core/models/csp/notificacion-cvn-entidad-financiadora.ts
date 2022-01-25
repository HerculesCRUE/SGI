import { IEmpresa } from '../sgemp/empresa';
import { INotificacionProyectoExternoCVN } from './notificacion-proyecto-externo-cvn';

export interface INotificacionCVNEntidadFinanciadora {
  id: number;
  datosEntidadFinanciadora: string;
  entidadFinanciadora: IEmpresa;
  notificacionProyecto: INotificacionProyectoExternoCVN;
}
