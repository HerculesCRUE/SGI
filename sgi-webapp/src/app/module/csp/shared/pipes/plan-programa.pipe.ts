import { Pipe, PipeTransform } from '@angular/core';
import { IPrograma } from '@core/models/csp/programa';

@Pipe({
  name: 'planPrograma'
})
export class PlanProgramaPipe implements PipeTransform {

  transform(programa: IPrograma): string {
    if (!!!programa) {
      return '';
    }

    return this.getPlan(programa).nombre;
  }

  private getPlan(programa: IPrograma): IPrograma {
    if (programa?.padre) {
      return this.getPlan(programa.padre);
    }
    return programa;
  }

}
