import { IGrupoEquipoInstrumental } from './grupo-equipo-instrumental';
import { IGrupoLineaInvestigacion } from './grupo-linea-investigacion';

export interface IGrupoLineaEquipoInstrumental {
  id: number;
  grupoLineaInvestigacion: IGrupoLineaInvestigacion;
  grupoEquipoInstrumental: IGrupoEquipoInstrumental;
}
