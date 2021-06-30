import { ITipoDocumento } from './tipos-configuracion';

export interface IProyectoPeriodoSeguimientoDocumento {
  id: number;
  proyectoPeriodoSeguimientoId: number;
  nombre: string;
  documentoRef: string;
  tipoDocumento: ITipoDocumento;
  visible: boolean;
  comentario: string;
}
