import { Pipe, PipeTransform } from '@angular/core';
import { IPrograma } from '@core/models/csp/programa';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';

@Pipe({
  name: 'proyectoEntidadConvocantePlan'
})
export class ProyectoEntidadConvocantePlanPipe implements PipeTransform {

  transform(value: IProyectoEntidadConvocante): string {
    if (value.programa) {
      return this.getTopLevel(value.programa).nombre;
    }
    if (value.programaConvocatoria) {
      return this.getTopLevel(value.programaConvocatoria).nombre;
    }
    return null;
  }

  private getTopLevel(programa: IPrograma): IPrograma {
    if (programa.padre) {
      return this.getTopLevel(programa.padre);
    }
    return programa;
  }
}
