
import { IEmpresaComposicionSociedad } from '@core/models/eer/empresa-composicion-sociedad';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEmpresaComposicionSociedadResponse } from './empresa-composicion-sociedad-response';


class EmpresaComposicionSociedadResponseConverter
  extends SgiBaseConverter<IEmpresaComposicionSociedadResponse, IEmpresaComposicionSociedad> {
  toTarget(value: IEmpresaComposicionSociedadResponse): IEmpresaComposicionSociedad {
    if (!value) {
      return value as unknown as IEmpresaComposicionSociedad;
    }
    return {
      id: value.id,
      miembroSociedadEmpresa: value.miembroSociedadEmpresaRef ? { id: value.miembroSociedadEmpresaRef } as IEmpresa : undefined,
      miembroSociedadPersona: value.miembroSociedadPersonaRef ? { id: value.miembroSociedadPersonaRef } as IPersona : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      capitalSocial: value.capitalSocial,
      participacion: value.participacion,
      tipoAportacion: value.tipoAportacion,
      empresa: value.empresaId ? { id: value.empresaId } as IEmpresaExplotacionResultados : undefined,
    };
  }

  fromTarget(value: IEmpresaComposicionSociedad): IEmpresaComposicionSociedadResponse {
    if (!value) {
      return value as unknown as IEmpresaComposicionSociedadResponse;
    }
    return {
      id: value.id,
      miembroSociedadEmpresaRef: value.miembroSociedadEmpresa?.id,
      miembroSociedadPersonaRef: value.miembroSociedadPersona?.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      capitalSocial: value.capitalSocial,
      participacion: value.participacion,
      tipoAportacion: value.tipoAportacion,
      empresaId: value.empresa?.id,
    };
  }
}

export const EMPRESA_COMPOSICION_SOCIEDAD_RESPONSE_CONVERTER = new EmpresaComposicionSociedadResponseConverter();
