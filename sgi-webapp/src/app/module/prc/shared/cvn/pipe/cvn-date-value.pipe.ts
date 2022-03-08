import { Pipe, PipeTransform } from '@angular/core';
import { IValorCampo } from '@core/models/prc/valor-campo';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { DateTime } from 'luxon';

@Pipe({
  name: 'cvnDateValue'
})
export class CvnDateValuePipe implements PipeTransform {

  transform([firstElement]: IValorCampo[]): DateTime {
    if (!firstElement) {
      return null;
    }
    return LuxonUtils.fromBackend(firstElement.valor);
  }

}
