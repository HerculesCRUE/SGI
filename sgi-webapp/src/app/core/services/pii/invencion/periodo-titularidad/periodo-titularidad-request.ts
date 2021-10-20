import { DateTime } from 'luxon';

export interface IPeriodoTitularidadRequest {

  /** Referencia a la {@link IInvencion} a la que esta asociada el {@link IPeriodoTitularidad} */
  invencionId: number;
  /** Fecha de Inicio del {@link IPeriodoTitularidad} */
  fechaInicio: string;
  /** Fecha de Finalizacion del {@link IPeriodoTitularidad} */
  fechaFin: string;
  /** Fecha que se asignará al {@link IPeriodoTitularidad} vigente anterior */
  fechaFinPrevious?: string;
}
