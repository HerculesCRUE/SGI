import { Pipe, PipeTransform } from '@angular/core';
import { IAutor } from '@core/models/prc/autor';

@Pipe({
  name: 'autorNombre'
})
export class AutorNombrePipe implements PipeTransform {

  transform(autor: IAutor): string {
    return transformAutorNombre(autor);
  }
}

export function transformAutorNombre(autor: IAutor): string {
  if (!autor) {
    return '';
  }
  return autor.persona ? autor.persona.nombre : autor.nombre;
}
