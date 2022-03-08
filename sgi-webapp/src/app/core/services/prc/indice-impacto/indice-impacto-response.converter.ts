import { IIndiceImpacto } from '@core/models/prc/indice-impacto';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IIndiceImpactoResponse } from './indice-impacto-response';

class IndiceImpactoResponseConverter extends SgiBaseConverter<IIndiceImpactoResponse, IIndiceImpacto>{
  toTarget(value: IIndiceImpactoResponse): IIndiceImpacto {
    if (!value) {
      return value as unknown as IIndiceImpacto;
    }
    return {
      id: value.id,
      anio: value.anio,
      indice: value.indice,
      numeroRevistas: value.numeroRevistas,
      otraFuenteImpacto: value.otraFuenteImpacto,
      posicionPublicacion: value.posicionPublicacion,
      produccionCientifica: value.produccionCientificaId ?
        { id: value.produccionCientificaId } as IProduccionCientifica : null,
      ranking: value.ranking,
      revista25: value.revista25,
      tipoFuenteImpacto: value.tipoFuenteImpacto,
    };
  }
  fromTarget(value: IIndiceImpacto): IIndiceImpactoResponse {
    if (!value) {
      return value as unknown as IIndiceImpactoResponse;
    }
    return {
      id: value.id,
      anio: value.anio,
      indice: value.indice,
      numeroRevistas: value.numeroRevistas,
      otraFuenteImpacto: value.otraFuenteImpacto,
      posicionPublicacion: value.posicionPublicacion,
      produccionCientificaId: value.produccionCientifica?.id,
      ranking: value.ranking,
      revista25: value.revista25,
      tipoFuenteImpacto: value.tipoFuenteImpacto,
    };
  }
}

export const INDICE_IMPACTO_RESPONSE_CONVERTER = new IndiceImpactoResponseConverter();
