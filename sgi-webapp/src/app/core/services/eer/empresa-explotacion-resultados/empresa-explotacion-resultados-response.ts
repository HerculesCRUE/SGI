import { EstadoEmpresa, TipoEmpresa } from "@core/models/eer/empresa-explotacion-resultados";

export interface IEmpresaExplotacionResultadosResponse {
  id: number;
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
  activo: boolean;
}
