import { Pipe, PipeTransform } from '@angular/core';
import { IPersona } from '@core/models/sgp/persona';

export function getPersonaEmailListConcatenated({ emails }: IPersona): string {
  let emailTitle = '';
  if (emails) {
    emailTitle = emails.reduce(
      (accum, emailData) => accum === '' ? accum + emailData.email : accum + ', ' + emailData.email,
      ''
    );
  }
  return emailTitle;
}

@Pipe({
  name: 'personaEmail'
})
export class PersonaEmailPipe implements PipeTransform {

  transform(persona: IPersona): string {
    return getPersonaEmailListConcatenated(persona);
  }
}
