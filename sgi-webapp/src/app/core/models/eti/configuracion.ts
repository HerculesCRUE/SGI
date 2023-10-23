export interface IConfiguracion {
  /** Id. */
  id: number;
  /** diasArchivadaInactivo */
  diasArchivadaInactivo: number;
  /** mesesArchivadaPendienteCorrecciones */
  mesesArchivadaPendienteCorrecciones: number;
  /** diasLimiteEvaluador */
  diasLimiteEvaluador: number;
  /** diasAvisoRetrospectiva */
  diasAvisoRetrospectiva: number;
  /** duracionProyectoEvaluacion */
  duracionProyectoEvaluacion: number;
}
