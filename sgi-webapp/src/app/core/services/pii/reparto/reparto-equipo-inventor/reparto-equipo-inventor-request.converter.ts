import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { IReparto } from '@core/models/pii/reparto';
import { IRepartoEquipoInventor } from '@core/models/pii/reparto-equipo-inventor';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRepartoEquipoInventorRequest } from './reparto-equipo-inventor-request';

class RepartoEquipoInventorRequestConverter extends SgiBaseConverter<IRepartoEquipoInventorRequest, IRepartoEquipoInventor> {

  toTarget(value: IRepartoEquipoInventorRequest): IRepartoEquipoInventor {
    if (!value) {
      return value as unknown as IRepartoEquipoInventor;
    }

    return {
      id: undefined,
      reparto: { id: value.repartoId } as IReparto,
      invencionInventor: { id: value.invencionInventorId } as IInvencionInventor,
      proyecto: value.proyectoRef !== null ? { id: +value.proyectoRef } as IProyecto : null,
      importeNomina: value.importeNomina,
      importeProyecto: value.importeProyecto,
      importeOtros: value.importeOtros
    };
  }

  fromTarget(value: IRepartoEquipoInventor): IRepartoEquipoInventorRequest {
    if (!value) {
      return value as unknown as IRepartoEquipoInventorRequest;
    }

    return {
      repartoId: value.reparto?.id,
      invencionInventorId: value.invencionInventor?.id,
      proyectoRef: value.proyecto?.id?.toString(),
      importeNomina: value.importeNomina,
      importeProyecto: value.importeProyecto,
      importeOtros: value.importeOtros
    };
  }
}

export const REPARTO_EQUIPO_INVENTOR_REQUEST_CONVERTER = new RepartoEquipoInventorRequestConverter();
