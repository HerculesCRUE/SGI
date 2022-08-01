export interface IEmpresaDocumentoResponse {
  id: number;
  nombre: string;
  documentoRef: string;
  comentarios: string;
  empresaId: number;
  tipoDocumento: {
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
  };
}
