import { ITipoIdentificador } from './tipo-identificador';

export interface IEmpresa {
  id: string;
  nombre: string;
  tipoIdentificador: ITipoIdentificador;
  numeroIdentificacion: string;
  razonSocial: string;
  datosEconomicos: boolean;
  padreId: string;
}
