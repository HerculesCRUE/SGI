import { IPersona } from '../sgp/persona';
import { IEmpresaExplotacionResultados } from './empresa-explotacion-resultados';

export interface IEmpresaEquipoEmprendedor {
  id: number;
  miembroEquipo: IPersona;
  empresa: IEmpresaExplotacionResultados;
}
