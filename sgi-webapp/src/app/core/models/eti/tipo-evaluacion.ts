export enum TIPO_EVALUACION {
  RETROSPECTIVA = 1,
  MEMORIA = 2,
  SEGUIMIENTO_ANUAL = 3,
  SEGUIMIENTO_FINAL = 4
}
export class TipoEvaluacion {

  /** ID */
  id: number;

  /** Nombre */
  nombre: string;

  /** Activo */
  activo: boolean;

  constructor() {
    this.id = null;
    this.nombre = null;
    this.activo = true;
  }

}
