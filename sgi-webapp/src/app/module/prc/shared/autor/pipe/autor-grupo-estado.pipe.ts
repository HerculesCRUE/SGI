import { Pipe, PipeTransform } from '@angular/core';
import { IAutorGrupo } from '@core/models/prc/autor-grupo';
import { TIPO_ESTADO_PRODUCCION_MAP } from '@core/models/prc/estado-produccion-cientifica';
import { TranslateService } from '@ngx-translate/core';

@Pipe({
  name: 'autorGrupoEstado'
})
export class AutorGrupoEstadoPipe implements PipeTransform {

  constructor(private readonly translateService: TranslateService) { }

  transform(grupos: IAutorGrupo[]): string {
    if (!grupos) {
      return '';
    }
    return grupos.map(grupo =>
      this.translateService.instant(TIPO_ESTADO_PRODUCCION_MAP.get(grupo.estado))
    ).join(', ');
  }
}
