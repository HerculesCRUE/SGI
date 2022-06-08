import { IGrupo } from './grupo';

export interface IGrupoEquipoInstrumental {
  id: number;
  grupo: IGrupo;
  nombre: string;
  numRegistro: string;
  descripcion: string;
}
