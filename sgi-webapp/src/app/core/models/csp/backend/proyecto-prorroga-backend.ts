import { Tipo } from '../proyecto-prorroga';

export interface IProyectoProrrogaBackend {
  id: number;
  proyectoId: number;
  numProrroga: number;
  fechaConcesion: string;
  tipo: Tipo;
  fechaFin: string;
  importe: number;
  observaciones: string;
}
