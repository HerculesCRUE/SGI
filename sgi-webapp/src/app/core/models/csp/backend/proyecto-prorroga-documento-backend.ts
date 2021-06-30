import { ITipoDocumento } from '../tipos-configuracion';

export interface IProyectoProrrogaDocumentoBackend {
  id: number;
  proyectoProrrogaId: number;
  nombre: string;
  documentoRef: string;
  tipoDocumento: ITipoDocumento;
  visible: boolean;
  comentario: string;
}
