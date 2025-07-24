export interface IRelacionEliminada {
  entidadSGIId: string;
  tipoEntidadSGI: TipoEntidadSGI;
}

export enum TipoEntidadSGI {
  GRUPO = 'GRUPO',
  PROYECTO = 'PROYECTO',
}