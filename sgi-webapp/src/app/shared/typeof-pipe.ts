import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'typeof'
})
export class TypeOfPipe implements PipeTransform {

  transform(value: any): any {
    if (value instanceof Array) {
      return 'array';
    }

    return typeof value;
  }

}