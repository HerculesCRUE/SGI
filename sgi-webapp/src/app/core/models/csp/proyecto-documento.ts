import { ITipoDocumento, ITipoFase } from './tipos-configuracion';

export interface IProyectoDocumento {
  id: number;
  proyectoId: number;
  nombre: string;
  documentoRef: string;
  tipoFase: ITipoFase;
  tipoDocumento: ITipoDocumento;
  comentario: string;
  visible: boolean;
}
