import { TipoAdministracion } from "@core/models/eer/empresa-administracion-sociedad";

export interface IEmpresaAdministracionSociedadRequest {
  id: number;
  miembroEquipoAdministracionRef: string;
  fechaInicio: string;
  fechaFin: string;
  tipoAdministracion: TipoAdministracion;
  empresaId: number;
}
