import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { IReparto } from '@core/models/pii/reparto';
import { IRepartoEquipoInventor } from '@core/models/pii/reparto-equipo-inventor';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRepartoEquipoInventorResponse } from './reparto-equipo-inventor-response';

class RepartoEquipoInventorResponseConverter extends SgiBaseConverter<IRepartoEquipoInventorResponse, IRepartoEquipoInventor>{
  toTarget(value: IRepartoEquipoInventorResponse): IRepartoEquipoInventor {
    if (!value) {
      return value as unknown as IRepartoEquipoInventor;
    }
    return {
      id: value.id,
      reparto: { id: value.repartoId } as IReparto,
      invencionInventor: { id: value.invencionInventorId } as IInvencionInventor,
      proyecto: value.proyectoRef !== null ? { id: +value.proyectoRef } as IProyecto : null,
      importeNomina: value.importeNomina,
      importeProyecto: value.importeProyecto,
      importeOtros: value.importeOtros
    };
  }

  fromTarget(value: IRepartoEquipoInventor): IRepartoEquipoInventorResponse {
    if (!value) {
      return value as unknown as IRepartoEquipoInventorResponse;
    }
    return {
      id: value.id,
      repartoId: value.reparto?.id,
      invencionInventorId: value.invencionInventor?.id,
      proyectoRef: value.proyecto?.id?.toString(),
      importeNomina: value.importeNomina,
      importeProyecto: value.importeProyecto,
      importeOtros: value.importeOtros
    };
  }
}

export const REPARTO_EQUIPO_INVENTOR_RESPONSE_CONVERTER = new RepartoEquipoInventorResponseConverter();
