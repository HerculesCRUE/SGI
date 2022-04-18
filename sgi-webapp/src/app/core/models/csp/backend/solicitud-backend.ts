import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { TipoSolicitudGrupo } from '../solicitud';
import { IEstadoSolicitudBackend } from './estado-solicitud-backend';

export interface ISolicitudBackend {
  /** Id */
  id: number;
  /** Título */
  titulo: string;
  /** Activo */
  activo: boolean;
  /** Codigo externo */
  codigoExterno: string;
  /** Codigo registro interno */
  codigoRegistroInterno: string;
  /** Estado solicitud */
  estado: IEstadoSolicitudBackend;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Convocatoria externa */
  convocatoriaExterna: string;
  /** Creador */
  creadorRef: string;
  /** Solicitante */
  solicitanteRef: string;
  /** Tipo formulario solicitud */
  formularioSolicitud: FormularioSolicitud;
  /** Tipo solicitud grupo */
  tipoSolicitudGrupo: TipoSolicitudGrupo;
  /** Unidad gestion */
  unidadGestionRef: string;
  /** Observaciones */
  observaciones: string;
}
