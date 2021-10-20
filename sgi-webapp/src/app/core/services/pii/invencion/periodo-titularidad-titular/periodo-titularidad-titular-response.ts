import { IInvencion } from '@core/models/pii/invencion';

export interface IPeriodoTitularidadTitularResponse {

  id: number;
  /** Referencia al {@link IPeriodoTitularidad} a la que esta asociada el {@link IPeriodoTitularidadTitular} */
  periodoTitularidadId: number;
  /** Referencia a la {@link IEmpresa} a la que esta asociada el {@link IPeriodoTitularidadTitular} */
  titularRef: string;
  /** Participacion del {@link IPeriodoTitularidadTitular} en la reparticion de la Titularidad */
  participacion: number;

}
