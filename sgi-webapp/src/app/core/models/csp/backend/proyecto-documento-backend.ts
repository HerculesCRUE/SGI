import { ITipoDocumento, ITipoFase } from '../tipos-configuracion';

export interface IProyectoDocumentoBackend {
  id: number;
  proyectoId: number;
  nombre: string;
  documentoRef: string;
  tipoFase: ITipoFase;
  tipoDocumento: ITipoDocumento;
  comentario: string;
  visible: boolean;
}
