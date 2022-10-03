import { Pipe, PipeTransform } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

const TEXT_YES = marker('label.si');
const TEXT_NO = marker('label.no');

@Pipe({
  name: 'booleanToText'
})
export class BooleanToTextPipe implements PipeTransform {

  transform(value: boolean): string {
    if (typeof value !== 'boolean') {
      return '';
    }
    return value ? TEXT_YES : TEXT_NO;
  }

}
