export enum EpigrafeCVN {
  /** Publicaciones, documentos científicos y técnicos */
  E060_010_010_000 = '060.010.010.000',
  /** Trabajos presentados en congresos nacionales o internacionales */
  E060_010_020_000 = '060.010.020.000',
  /** Obras artísticas dirigidas */
  E050_020_030_000 = '050.020.030.000',
  /** Consejos/comités editoriales */
  E060_030_030_000 = '060.030.030.000',
  /** Invenciones */
  E050_030_010_000 = '050.030.010.000',
  /** Contratos */
  E050_020_020_000 = '050.020.020.000',
  /** Proyecto de investigación */
  E050_020_010_000 = '050.020.010.000',
  /** Organización actividades I+D+i */
  E060_020_030_000 = '060.020.030.000',
  /** Dirección de tesis */
  E030_040_000_000 = '030.040.000.000',
  /** Sexenios =Periodos de actividad investigadora */
  E060_030_070_000 = '060.030.070.000',
}

export const EPIGRAFE_CVN_MAP = new Map(Object.entries(EpigrafeCVN).map(([key, value]) => [value.toString(), key]));
