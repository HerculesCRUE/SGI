import { Pipe, PipeTransform } from '@angular/core';
import { IAutorGrupo } from '@core/models/prc/autor-grupo';
import { LanguageService } from '@core/services/language.service';
import { TranslateService } from '@ngx-translate/core';

@Pipe({
  name: 'autorGrupoEstadoTooltip'
})
export class AutorGrupoEstadoTooltipPipe implements PipeTransform {

  constructor(
    private readonly translateService: TranslateService,
    private readonly languageService: LanguageService
  ) { }

  transform(grupos: IAutorGrupo[]): string {
    if (!grupos) {
      return '';
    }
    const value = grupos.map(autorGrupo =>
      this.translateService.instant(this.languageService.getFieldValue(autorGrupo.grupo.nombre)) + ' (' + autorGrupo.grupo.codigo + ')'
    );
    return value.join(', ');
  }
}
