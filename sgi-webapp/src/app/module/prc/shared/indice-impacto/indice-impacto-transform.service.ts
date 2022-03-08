import { Injectable } from '@angular/core';
import {
  Cuartil, IIndiceImpacto, TipoFuenteImpacto,
  TIPO_FUENTE_IMPACTO_MAP, TIPO_FUENTE_IMPACTO_TRANSLATOR_MAP
} from '@core/models/prc/indice-impacto';
import { TranslateService } from '@ngx-translate/core';

const Q1_CUARTIL_UPPER_LIMIT = 25;
const Q2_CUARTIL_UPPER_LIMIT = 50;
const Q3_CUARTIL_UPPER_LIMIT = 75;

@Injectable({
  providedIn: 'root'
})
export class IndiceImpactoTransformService {

  constructor(private translateService: TranslateService) { }

  transformCuartil(indiceImpacto: IIndiceImpacto): string {
    if (!indiceImpacto) {
      return '';
    }
    if (indiceImpacto.revista25) {
      return Cuartil.Q1;
    }
    if (indiceImpacto.posicionPublicacion && indiceImpacto.numeroRevistas) {
      const caltulatedCuartil = (indiceImpacto.posicionPublicacion * 100) / indiceImpacto.numeroRevistas;

      if (caltulatedCuartil <= Q1_CUARTIL_UPPER_LIMIT) {
        return Cuartil.Q1;
      }
      if (caltulatedCuartil > Q1_CUARTIL_UPPER_LIMIT && caltulatedCuartil <= Q2_CUARTIL_UPPER_LIMIT) {
        return Cuartil.Q2;
      }
      if (caltulatedCuartil > Q2_CUARTIL_UPPER_LIMIT && caltulatedCuartil <= Q3_CUARTIL_UPPER_LIMIT) {
        return Cuartil.Q3;
      }
      if (caltulatedCuartil > Q3_CUARTIL_UPPER_LIMIT) {
        return Cuartil.Q4;
      }
    } else {
      return '';
    }
  }

  transformFuenteImpacto(indiceImpacto: IIndiceImpacto): string {
    if (!indiceImpacto) {
      return '';
    }
    /*
      El back devuelve un String como fuente de impacto que está relacionado con el enum TipoFuenteImpacto
      y es necesario obtener el valor correspondiente del enum para poder hacer la traducción.
    */
    const tipoFuenteImpactoEnumValue = TipoFuenteImpacto[TIPO_FUENTE_IMPACTO_TRANSLATOR_MAP.get(indiceImpacto.tipoFuenteImpacto)];
    // Sino tiene una correspondencia con algún valor del enum no se puede traducir y se devuelve directamente el valor
    if (!tipoFuenteImpactoEnumValue) {
      return indiceImpacto.tipoFuenteImpacto;
    }
    const tipoFuenteImpactoTranslated = this.translateService.instant(TIPO_FUENTE_IMPACTO_MAP.get(tipoFuenteImpactoEnumValue));
    if (tipoFuenteImpactoEnumValue === TipoFuenteImpacto.OTHERS) {

      return typeof indiceImpacto.otraFuenteImpacto === 'string' ?
        tipoFuenteImpactoTranslated + ' - ' + indiceImpacto.otraFuenteImpacto : tipoFuenteImpactoTranslated;
    }
    return tipoFuenteImpactoTranslated;
  }
}
