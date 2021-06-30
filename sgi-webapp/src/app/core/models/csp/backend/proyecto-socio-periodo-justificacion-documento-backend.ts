import { ITipoDocumento } from '../tipos-configuracion';

export interface IProyectoSocioPeriodoJustificacionDocumentoBackend {
  id: number;
  proyectoSocioPeriodoJustificacionId: number;
  nombre: string;
  documentoRef: string;
  tipoDocumento: ITipoDocumento;
  comentario: string;
  visible: boolean;
}
