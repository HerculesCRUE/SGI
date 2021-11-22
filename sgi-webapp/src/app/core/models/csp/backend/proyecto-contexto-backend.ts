import { IAreaTematica } from '../area-tematica';
import { PropiedadResultados } from '../proyecto-contexto';

export interface IProyectoContextoBackend {
  id: number;
  proyectoId: number;
  objetivos: string;
  intereses: string;
  resultadosPrevistos: string;
  propiedadResultados: PropiedadResultados;
  areaTematica: IAreaTematica;
}
