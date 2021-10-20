import { DateTime } from 'luxon';

export interface IPeriodoTitularidadTitularRequest {

  /** Referencia a la {@link IEmpresa} a la que esta asociada el {@link IPeriodoTitularidadTitular} */
  titularRef: string;
  /** Participacion del {@link IPeriodoTitularidadTitular} en la reparticion de la Titularidad */
  participacion: number;

}
