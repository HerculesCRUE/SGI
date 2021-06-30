export interface IDocumentoBackend {
  /** ID */
  documentoRef: string;
  /** Nombre */
  nombre: string;
  /** Versión */
  version: number;
  /** Archivo */
  archivo: string;
  /** Fecha creación */
  fechaCreacion: string;
  /** Tipo */
  tipo: string;

  autorRef: string;
}
