import { IConvocatoriaEntidadFinanciadoraBackend } from '@core/models/csp/backend/convocatoria-entidad-financiadora-backend';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FUENTE_FINANCIACION_RESPONSE_CONVERTER } from '@core/services/csp/fuente-financiacion/fuente-financiacion-response.converter';
import { TIPO_FINANCIACION_RESPONSE_CONVERTER } from '@core/services/csp/tipo-financiacion/tipo-financiacion-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaEntidadFinanciadoraConverter extends
  SgiBaseConverter<IConvocatoriaEntidadFinanciadoraBackend, IConvocatoriaEntidadFinanciadora> {

  toTarget(value: IConvocatoriaEntidadFinanciadoraBackend): IConvocatoriaEntidadFinanciadora {
    if (!value) {
      return value as unknown as IConvocatoriaEntidadFinanciadora;
    }
    return {
      id: value.id,
      empresa: { id: value.entidadRef } as IEmpresa,
      convocatoriaId: value.convocatoriaId,
      fuenteFinanciacion: FUENTE_FINANCIACION_RESPONSE_CONVERTER.toTarget(value.fuenteFinanciacion),
      tipoFinanciacion: TIPO_FINANCIACION_RESPONSE_CONVERTER.toTarget(value.tipoFinanciacion),
      porcentajeFinanciacion: value.porcentajeFinanciacion,
      importeFinanciacion: value.importeFinanciacion
    };
  }

  fromTarget(value: IConvocatoriaEntidadFinanciadora): IConvocatoriaEntidadFinanciadoraBackend {
    if (!value) {
      return value as unknown as IConvocatoriaEntidadFinanciadoraBackend;
    }
    return {
      id: value.id,
      entidadRef: value.empresa?.id,
      convocatoriaId: value.convocatoriaId,
      fuenteFinanciacion: FUENTE_FINANCIACION_RESPONSE_CONVERTER.fromTarget(value.fuenteFinanciacion),
      tipoFinanciacion: TIPO_FINANCIACION_RESPONSE_CONVERTER.fromTarget(value.tipoFinanciacion),
      porcentajeFinanciacion: value.porcentajeFinanciacion,
      importeFinanciacion: value.importeFinanciacion
    };
  }
}

export const CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER = new ConvocatoriaEntidadFinanciadoraConverter();
