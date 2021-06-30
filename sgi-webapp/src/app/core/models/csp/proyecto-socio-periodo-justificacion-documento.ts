import { ITipoDocumento } from './tipos-configuracion';

export interface IProyectoSocioPeriodoJustificacionDocumento {
  id: number;
  proyectoSocioPeriodoJustificacionId: number;
  nombre: string;
  documentoRef: string;
  tipoDocumento: ITipoDocumento;
  comentario: string;
  visible: boolean;
}
