import { Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Pipe({
  name: 'estadoEvaluador',
  pure: false
})
export class EstadoEvaluadorPipe implements PipeTransform {

  private fechaBaja: DateTime;
  private destroy$ = new Subject();

  constructor(private readonly translate: TranslateService) {
    this.translate.onLangChange.pipe(takeUntil(this.destroy$)).subscribe(() => this.transform(this.fechaBaja));
  }

  transform(fechaBaja: DateTime): string {
    this.fechaBaja = fechaBaja;
    if (!fechaBaja || fechaBaja > DateTime.now()) {
      return this.translate.instant('label.activo');
    } else if (fechaBaja < DateTime.now()) {
      return this.translate.instant('eti.evaluador.estado.inactivo');
    }
    return null;
  }

  onDestroy() {
    this.destroy$.next();
  }

}
