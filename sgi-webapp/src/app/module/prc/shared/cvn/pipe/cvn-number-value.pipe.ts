import { DecimalPipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { IValorCampo } from '@core/models/prc/valor-campo';

@Pipe({
  name: 'cvnNumberValue'
})
export class CvnNumberValuePipe implements PipeTransform {

  constructor(private decimalPipe: DecimalPipe) { }

  transform(valoresCampo: IValorCampo[], digitsInfo = '1.0-3'): string {
    return valoresCampo.reduce((accum, valorCampo) => {
      if (accum.length > 0) {
        accum += '; ' + this.decimalPipe.transform(valorCampo.valor, digitsInfo);
      } else {
        accum += this.decimalPipe.transform(valorCampo.valor, digitsInfo);
      }
      return accum;
    }, '');
  }

}
