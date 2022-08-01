import { TipoEmpresa, EstadoEmpresa } from "@core/models/eer/empresa-explotacion-resultados";

export interface IEmpresaExplotacionResultadosRequest {
  fechaSolicitud: string;
  tipoEmpresa: TipoEmpresa;
  solicitanteRef: string;
  nombreRazonSocial: string;
  entidadRef: string;
  objetoSocial: string;
  conocimientoTecnologia: string;
  numeroProtocolo: string;
  notario: string;
  fechaConstitucion: string;
  fechaAprobacionCG: string;
  fechaIncorporacion: string;
  fechaDesvinculacion: string;
  fechaCese: string;
  observaciones: string;
  estado: EstadoEmpresa;
}
