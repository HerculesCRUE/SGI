import { Pipe, PipeTransform } from '@angular/core';
import { IPersona } from '@core/models/sgp/persona';

@Pipe({
  name: 'personaNombreCompleto'
})
export class PersonaNombreCompletoPipe implements PipeTransform {

  transform(personas: IPersona | IPersona[]): string {
    if (Array.isArray(personas)) {
      return personas.map(persona => this.getNombreCompleto(persona)).join(', ');
    } else {
      return this.getNombreCompleto(personas);
    }
  }

  private getNombreCompleto(persona: IPersona): string {
    let nombreCompleto = '';
    if (persona?.nombre) {
      nombreCompleto = persona.nombre;
    }
    if (persona?.apellidos) {
      if (nombreCompleto === '') {
        nombreCompleto = persona.apellidos;
      } else {
        nombreCompleto += ' ' + persona.apellidos;
      }
    }
    return nombreCompleto;
  }

}
