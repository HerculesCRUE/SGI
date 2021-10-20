import { IEmpresa } from '../sgemp/empresa';
import { IPeriodoTitularidad } from './periodo-titularidad';

export interface IPeriodoTitularidadTitular {

  id: number;
  /** Referencia al {@link IPeriodoTitularidad} a la que esta asociada el {@link IPeriodoTitularidadTitular} */
  periodoTitularidad: IPeriodoTitularidad;
  /** {@link IEmpresa} titular en el {@link IPeriodoTitularidad} */
  titular: IEmpresa;
  /** Porcentaje de participación en el {@link IPeriodoTitularidad} del Titular ({@link IPeriodoTitularidadTitular}) */
  participacion: number;

}
