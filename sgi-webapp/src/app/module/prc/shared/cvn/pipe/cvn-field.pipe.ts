import { Pipe, PipeTransform } from '@angular/core';
import { CVN_TRANSLATE_PREFIX } from '../literales-cvn';

@Pipe({
  name: 'cvnField'
})
export class CvnFieldPipe implements PipeTransform {

  transform(campoCVN: string | undefined): string {
    return campoCVN ? CVN_TRANSLATE_PREFIX + campoCVN : '';
  }
}
