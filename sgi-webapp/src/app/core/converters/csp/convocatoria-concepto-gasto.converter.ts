import { IConvocatoriaConceptoGastoBackend } from '@core/models/csp/backend/convocatoria-concepto-gasto-backend';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaConceptoGastoConverter extends SgiBaseConverter<IConvocatoriaConceptoGastoBackend, IConvocatoriaConceptoGasto> {

  toTarget(value: IConvocatoriaConceptoGastoBackend): IConvocatoriaConceptoGasto {
    if (!value) {
      return value as unknown as IConvocatoriaConceptoGasto;
    }
    return {
      id: value.id,
      conceptoGasto: value.conceptoGasto,
      convocatoriaId: value.convocatoriaId,
      observaciones: value.observaciones,
      importeMaximo: value.importeMaximo,
      permitido: value.permitido,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
    };
  }

  fromTarget(value: IConvocatoriaConceptoGasto): IConvocatoriaConceptoGastoBackend {
    if (!value) {
      return value as unknown as IConvocatoriaConceptoGastoBackend;
    }
    return {
      id: value.id,
      conceptoGasto: value.conceptoGasto,
      convocatoriaId: value.convocatoriaId,
      observaciones: value.observaciones,
      importeMaximo: value.importeMaximo,
      permitido: value.permitido,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
    };
  }
}

export const CONVOCATORIA_CONCEPTO_GASTO_CONVERTER = new ConvocatoriaConceptoGastoConverter();
