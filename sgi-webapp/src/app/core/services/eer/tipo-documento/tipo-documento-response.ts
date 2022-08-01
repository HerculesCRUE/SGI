export interface ITipoDocumentoResponse {
  id: number;
  nombre: string;
  descripcion: string;
  padre: {
    id: number;
    nombre: string;
    descripcion: string;
    activo: boolean;
  };
  activo: boolean;
}
