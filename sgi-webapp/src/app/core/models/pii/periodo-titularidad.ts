import { DateTime } from 'luxon';
import { IInvencion } from './invencion';

export interface IPeriodoTitularidad {

  id: number;
  /** Referencia a la {@link IInvencion} a la que esta asociada el {@link IPeriodoTitularidad} */
  invencion: IInvencion;
  /** Fecha de Inicio del {@link IPeriodoTitularidad} */
  fechaInicio: DateTime;
  /** Fecha de Finalizacion del {@link IPeriodoTitularidad} */
  fechaFin: DateTime;

  fechaFinPrevious?: DateTime;

}
