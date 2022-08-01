import { TipoAportacion } from "@core/models/eer/empresa-composicion-sociedad";

export interface IEmpresaComposicionSociedadResponse {
  id: number;
  miembroSociedadPersonaRef: string;
  miembroSociedadEmpresaRef: string;
  fechaInicio: string;
  fechaFin: string;
  participacion: number;
  tipoAportacion: TipoAportacion;
  capitalSocial: number;
  empresaId: number;
}
