export interface IConvocatoriaRequisitoEquipoBackend {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Fecha máxima nivel académico */
  fechaMaximaNivelAcademico: string;
  /** Fecha máxima nivel académico */
  fechaMinimaNivelAcademico: string;
  /** Ratio máxima */
  edadMaxima: number;
  /** Vinculación universidad */
  vinculacionUniversidad: boolean;
  /** Sexo */
  sexoRef: string;
  /** Ratio Sexo */
  ratioSexo: number;
  /** Fecha máxima categoria profesional */
  fechaMaximaCategoriaProfesional: string;
  /** Fecha mínima categoria profesional */
  fechaMinimaCategoriaProfesional: string;
  /** Número mínimo proyectos competitivos */
  numMinimoCompetitivos: number;
  /** Número mínimo proyectos NO competitivos */
  numMinimoNoCompetitivos: number;
  /** Número máximo proyectos competitivos activos */
  numMaximoCompetitivosActivos: number;
  /** Número máximo proyectos NO competitivos activos */
  numMaximoNoCompetitivosActivos: number;
  /** Otros requisitos */
  otrosRequisitos: string;
}
