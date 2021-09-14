import { IPersona } from '../sgp/persona';
import { IInvencion } from './invencion';

export interface IInvencionInventor {

  id: number;
  /** Referencia a la {@link IInvencion} a la que esta asociandose el Inventor({@link IPersona}) */
  invencion: IInvencion;
  /** El Inventor({@link IPersona}) */
  inventor: IPersona;
  /** Porcentaje de participación en la {@link IInvencion} del Inventor({@link IPersona}) */
  participacion: number;
  /** Define si la Universidad deberá realizar el reparto teniendo en cuenta a este Inventor({@link IPersona}) */
  repartoUniversidad: boolean;
  /** Define si la entidad está activa o no */
  activo: boolean;

}
