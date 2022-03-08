import { Pipe, PipeTransform } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IValorCampo } from '@core/models/prc/valor-campo';

const MSG_TRUE = marker('label.si');
const MSG_FALSE = marker('label.no');

@Pipe({
  name: 'cvnBooleanValue'
})
export class CvnBooleanValuePipe implements PipeTransform {

  transform([firstElement]: IValorCampo[]): string {
    if (!firstElement) {
      return '';
    }

    if (firstElement.valor === 'true') {
      return MSG_TRUE;
    } else if (firstElement.valor === 'false') {
      return MSG_FALSE;
    } else {
      return '';
    }
  }

}
