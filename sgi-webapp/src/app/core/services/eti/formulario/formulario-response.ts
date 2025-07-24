import { FormularioSeguimientoAnualDocumentacionTitle, FormularioTipo } from "@core/models/eti/formulario";

export interface IFormularioResponse {
  /** Id */
  id: number;
  /** Tipo */
  tipo: FormularioTipo;
  /** Codigo */
  codigo: string;
  /** Titulo del apartado Seguimiento Anual en la documentación de la Memoria */
  seguimientoAnualDocumentacionTitle: FormularioSeguimientoAnualDocumentacionTitle;
  /** Activo */
  activo: boolean;
}
