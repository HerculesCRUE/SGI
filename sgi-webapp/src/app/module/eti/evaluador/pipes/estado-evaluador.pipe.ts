import { Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';

@Pipe({
  name: 'estadoEvaluador'
})
export class EstadoEvaluadorPipe implements PipeTransform {

  constructor(private readonly translate: TranslateService) {
    //
  }

  transform(fechaBaja: DateTime): string {
    if (!fechaBaja || fechaBaja > DateTime.now()) {
      return this.translate.instant('label.activo');
    } else if (fechaBaja < DateTime.now()) {
      return this.translate.instant('eti.evaluador.estado.inactivo');
    }
    return null;
  }

}
