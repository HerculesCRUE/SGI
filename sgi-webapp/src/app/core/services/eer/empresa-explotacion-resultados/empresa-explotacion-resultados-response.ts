import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { EstadoEmpresa, TipoEmpresa } from "@core/models/eer/empresa-explotacion-resultados";

export interface IEmpresaExplotacionResultadosResponse {
  id: number;
  fechaSolicitud: string;
  tipoEmpresa: TipoEmpresa;
  solicitanteRef: string;
  nombreRazonSocial: I18nFieldValueResponse[];
  entidadRef: string;
  objetoSocial: I18nFieldValueResponse[];
  conocimientoTecnologia: I18nFieldValueResponse[];
  numeroProtocolo: string;
  notario: I18nFieldValueResponse[];
  fechaConstitucion: string;
  fechaAprobacionCG: string;
  fechaIncorporacion: string;
  fechaDesvinculacion: string;
  fechaCese: string;
  observaciones: I18nFieldValueResponse[];
  estado: EstadoEmpresa;
  activo: boolean;
}
