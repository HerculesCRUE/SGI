import { Pipe, PipeTransform } from '@angular/core';
import { IAutor } from '@core/models/prc/autor';

@Pipe({
  name: 'autorApellidos'
})
export class AutorApellidosPipe implements PipeTransform {

  transform(autor: IAutor): string {
    return transformAutorApellidos(autor);
  }
}

export function transformAutorApellidos(autor: IAutor): string {
  if (!autor) {
    return '';
  }
  return autor.persona ? autor.persona.apellidos : autor.apellidos;
}
