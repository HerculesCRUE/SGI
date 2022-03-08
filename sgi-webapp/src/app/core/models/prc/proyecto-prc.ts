import { IProyectoResumen } from '../csp/proyecto-resumen';
import { IProduccionCientifica } from './produccion-cientifica';


export interface IProyectoPrc {
  id: number;
  proyecto: IProyectoResumen;
  produccionCientifica: IProduccionCientifica;
}
