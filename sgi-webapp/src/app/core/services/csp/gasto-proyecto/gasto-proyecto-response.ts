import { Estado } from '@core/models/csp/estado-gasto-proyecto';

export interface IGastoProyectoResponse {
  id: number;
  proyectoId: number;
  gastoRef: string;
  conceptoGasto: {
    id: number;
    nombre: string;
    descripcion: string;
  };
  estado: {
    id: number;
    estado: Estado;
    fechaEstado: string;
    comentario: string;
  };
  fechaCongreso: string;
  importeInscripcion: number;
  observaciones: string;
}
