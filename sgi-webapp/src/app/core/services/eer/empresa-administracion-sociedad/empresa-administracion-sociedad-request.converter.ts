

import { IEmpresaAdministracionSociedad } from '@core/models/eer/empresa-administracion-sociedad';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEmpresaAdministracionSociedadRequest } from './empresa-administracion-sociedad-request';

class EmpresaAdministracionSociedadRequestConverter
  extends SgiBaseConverter<IEmpresaAdministracionSociedadRequest, IEmpresaAdministracionSociedad> {
  toTarget(value: IEmpresaAdministracionSociedadRequest): IEmpresaAdministracionSociedad {
    if (!value) {
      return value as unknown as IEmpresaAdministracionSociedad;
    }
    return {
      id: undefined,
      miembroEquipoAdministracion: value.miembroEquipoAdministracionRef ? { id: value.miembroEquipoAdministracionRef } as IPersona : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      tipoAdministracion: value.tipoAdministracion,
      empresa: value.empresaId ? { id: value.empresaId } as IEmpresaExplotacionResultados : undefined,
    };
  }

  fromTarget(value: IEmpresaAdministracionSociedad): IEmpresaAdministracionSociedadRequest {
    if (!value) {
      return value as unknown as IEmpresaAdministracionSociedadRequest;
    }
    return {
      id: value.id,
      miembroEquipoAdministracionRef: value.miembroEquipoAdministracion?.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      tipoAdministracion: value.tipoAdministracion,
      empresaId: value.empresa?.id,
    };
  }
}

export const EMPRESA_ADMINISTRACION_SOCIEDAD_REQUEST_CONVERTER = new EmpresaAdministracionSociedadRequestConverter();
