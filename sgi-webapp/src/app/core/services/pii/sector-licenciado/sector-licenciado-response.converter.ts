import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencion } from '@core/models/pii/invencion';
import { ISectorLicenciado } from '@core/models/pii/sector-licenciado';
import { IPais } from '@core/models/sgo/pais';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISectorLicenciadoResponse } from './sector-licenciado-response';

class SectorLicenciadoResponseConverter extends SgiBaseConverter<ISectorLicenciadoResponse, ISectorLicenciado>{
  toTarget(value: ISectorLicenciadoResponse): ISectorLicenciado {
    if (!value) {
      return value as unknown as ISectorLicenciado;
    }
    return {
      id: value.id,
      fechaInicioLicencia: LuxonUtils.fromBackend(value.fechaInicioLicencia),
      fechaFinLicencia: LuxonUtils.fromBackend(value.fechaFinLicencia),
      invencion: { id: value.invencionId } as IInvencion,
      sectorAplicacion: value.sectorAplicacion,
      contrato: { id: parseInt(value.contratoRef, 10) } as IProyecto,
      pais: { id: value.paisRef } as IPais,
      exclusividad: value.exclusividad
    };
  }
  fromTarget(value: ISectorLicenciado): ISectorLicenciadoResponse {
    if (!value) {
      return value as unknown as ISectorLicenciadoResponse;
    }
    return {
      id: value.id,
      fechaInicioLicencia: LuxonUtils.toBackend(value.fechaInicioLicencia),
      fechaFinLicencia: LuxonUtils.toBackend(value.fechaFinLicencia),
      invencionId: value.invencion?.id,
      sectorAplicacion: value.sectorAplicacion,
      contratoRef: value.contrato?.id?.toString(),
      paisRef: value.pais?.id,
      exclusividad: value.exclusividad
    };
  }
}

export const SECTOR_LICENCIADO_RESPONSE_CONVERTER = new SectorLicenciadoResponseConverter();
