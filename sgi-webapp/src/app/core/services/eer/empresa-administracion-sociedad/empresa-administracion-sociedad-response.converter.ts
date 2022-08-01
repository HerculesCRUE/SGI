
import { IEmpresaAdministracionSociedad } from '@core/models/eer/empresa-administracion-sociedad';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEmpresaAdministracionSociedadResponse } from './empresa-administracion-sociedad-response';


class EmpresaAdministracionSociedadResponseConverter
  extends SgiBaseConverter<IEmpresaAdministracionSociedadResponse, IEmpresaAdministracionSociedad> {
  toTarget(value: IEmpresaAdministracionSociedadResponse): IEmpresaAdministracionSociedad {
    if (!value) {
      return value as unknown as IEmpresaAdministracionSociedad;
    }
    return {
      id: value.id,
      miembroEquipoAdministracion: value.miembroEquipoAdministracionRef ? { id: value.miembroEquipoAdministracionRef } as IPersona : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      tipoAdministracion: value.tipoAdministracion,
      empresa: value.empresaId ? { id: value.empresaId } as IEmpresaExplotacionResultados : undefined,
    };
  }

  fromTarget(value: IEmpresaAdministracionSociedad): IEmpresaAdministracionSociedadResponse {
    if (!value) {
      return value as unknown as IEmpresaAdministracionSociedadResponse;
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

export const EMPRESA_ADMINISTRACION_SOCIEDAD_RESPONSE_CONVERTER = new EmpresaAdministracionSociedadResponseConverter();
