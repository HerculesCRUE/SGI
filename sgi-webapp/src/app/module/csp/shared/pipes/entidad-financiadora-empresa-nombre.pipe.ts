import { Pipe, PipeTransform } from '@angular/core';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';

@Pipe({
  name: 'entidadFinanciadoraEmpresaNombre'
})
export class EntidadFinanciadoraEmpresaNombrePipe implements PipeTransform {

  transform(entidadesFinanciadoras: IEntidadFinanciadora | IEntidadFinanciadora[]): string {
    if (Array.isArray(entidadesFinanciadoras)) {
      return entidadesFinanciadoras.map(entidadFinanciadora => entidadFinanciadora?.empresa?.nombre ?? '').join(', ');
    } else {
      return entidadesFinanciadoras?.empresa?.nombre ?? '';
    }
  }

}
