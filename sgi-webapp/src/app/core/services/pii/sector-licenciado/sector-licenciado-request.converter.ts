import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencion } from '@core/models/pii/invencion';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { ISectorLicenciado } from '@core/models/pii/sector-licenciado';
import { IPais } from '@core/models/sgo/pais';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISectorLicenciadoRequest } from './sector-licenciado-request';

class SectorLicenciadoRequestConverter extends SgiBaseConverter<ISectorLicenciadoRequest, ISectorLicenciado> {

  toTarget(value: ISectorLicenciadoRequest): ISectorLicenciado {
    if (!value) {
      return value as unknown as ISectorLicenciado;
    }

    return {
      id: undefined,
      fechaInicioLicencia: LuxonUtils.fromBackend(value.fechaInicioLicencia),
      fechaFinLicencia: LuxonUtils.fromBackend(value.fechaFinLicencia),
      invencion: { id: value.invencionId } as IInvencion,
      sectorAplicacion: { id: value.sectorAplicacionId } as ISectorAplicacion,
      contrato: { id: parseInt(value.contratoRef, 10) } as IProyecto,
      pais: { id: value.paisRef } as IPais,
      exclusividad: value.exclusividad
    };
  }

  fromTarget(value: ISectorLicenciado): ISectorLicenciadoRequest {
    if (!value) {
      return value as unknown as ISectorLicenciadoRequest;
    }

    return {
      fechaInicioLicencia: LuxonUtils.toBackend(value.fechaInicioLicencia),
      fechaFinLicencia: LuxonUtils.toBackend(value.fechaFinLicencia),
      invencionId: value.invencion?.id,
      sectorAplicacionId: value.sectorAplicacion?.id,
      contratoRef: value.contrato?.id.toString(),
      paisRef: value.pais?.id,
      exclusividad: value.exclusividad
    };
  }
}

export const SECTOR_LICENCIADO_REQUEST_CONVERTER = new SectorLicenciadoRequestConverter();
