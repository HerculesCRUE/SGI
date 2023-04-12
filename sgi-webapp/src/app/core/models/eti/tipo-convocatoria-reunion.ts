export enum TIPO_CONVOCATORIA_REUNION {
  EXTRAORDINARIA = 1,
  ORDINARIA = 2,
  SEGUIMIENTO = 3
}

export class TipoConvocatoriaReunion {

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
