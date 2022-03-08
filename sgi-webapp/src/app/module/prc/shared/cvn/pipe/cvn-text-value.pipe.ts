import { Pipe, PipeTransform } from '@angular/core';
import { IValorCampo } from '@core/models/prc/valor-campo';

@Pipe({
  name: 'cvnTextValue'
})
export class CvnTextValuePipe implements PipeTransform {

  transform(valoresCampo: IValorCampo[]): string {
    return valoresCampo.reduce((accum, valorCampo) => {
      if (accum.length > 0) {
        accum += ', ' + valorCampo.valor;
      } else {
        accum += valorCampo.valor;
      }
      return accum;
    }, '');
  }

}
