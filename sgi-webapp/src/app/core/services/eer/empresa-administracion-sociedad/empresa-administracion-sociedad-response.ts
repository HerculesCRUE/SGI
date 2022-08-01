import { TipoAdministracion } from "@core/models/eer/empresa-administracion-sociedad";

export interface IEmpresaAdministracionSociedadResponse {
  id: number;
  miembroEquipoAdministracionRef: string;
  fechaInicio: string;
  fechaFin: string;
  tipoAdministracion: TipoAdministracion;
  empresaId: number;
}
