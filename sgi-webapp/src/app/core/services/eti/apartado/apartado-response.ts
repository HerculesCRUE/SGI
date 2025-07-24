import { IBloqueResponse } from "../bloque/bloque-response";
import { IApartadoDefinicionResponse } from "./apartado-definicion-response";

export interface IApartadoResponse {
  /** Id */
  id: number;
  /** Bloque del apartado */
  bloque: IBloqueResponse;
  /** Apartado padre del formulario */
  padre: IApartadoResponse;
  /** Orden */
  orden: number;
  /** Definición del apartado */
  definicion: IApartadoDefinicionResponse[]
}
