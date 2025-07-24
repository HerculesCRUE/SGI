import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { ComiteNombreGenero } from "@core/models/eti/comite";

export interface IComiteResponse {
  /** Id. */
  id: number;
  /** Código */
  codigo: string;
  /** Nombre  */
  nombre: IComiteNombreResponse[];
  /** Formulario Memoria*/
  formularioMemoriaId: number;
  /** Formulario Seguimiento Anual */
  formularioSeguimientoAnualId: number;
  /** Formulario Seguimiento Final */
  formularioSeguimientoFinalId: number;
  /** Formulario Retrospectiva */
  formularioRetrospectivaId: number
  /** Requiere retrospectiva */
  requiereRetrospectiva: boolean;
  /** Prefijo en las memorias */
  prefijoReferencia: string;
  /** Permitir memorias de tipo ratificación */
  permitirRatificacion: boolean;
  /** Habilitar la creación de tareas con nombre libre */
  tareaNombreLibre: boolean;
  /** Habilitar la creación de tareas con experiencia libre */
  tareaExperienciaLibre: boolean;
  /** Habilitar la creación de tareas con detelle de experiencia */
  tareaExperienciaDetalle: boolean;
  /** Habilitar la creación de memorias con título libre */
  memoriaTituloLibre: boolean;
  /** Activo */
  activo: boolean;
}

export interface IComiteNombreResponse extends I18nFieldValueResponse {
  genero: ComiteNombreGenero;
}