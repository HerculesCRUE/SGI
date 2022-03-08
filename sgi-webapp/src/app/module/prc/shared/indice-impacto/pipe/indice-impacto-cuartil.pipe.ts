import { Pipe, PipeTransform } from '@angular/core';
import { IIndiceImpacto } from '@core/models/prc/indice-impacto';
import { IndiceImpactoTransformService } from '../indice-impacto-transform.service';

@Pipe({
  name: 'indiceImpactoCuartil'
})
export class IndiceImpactoCuartilPipe implements PipeTransform {

  constructor(private transformService: IndiceImpactoTransformService) { }

  transform(indiceImpacto: IIndiceImpacto): string {
    return this.transformService.transformCuartil(indiceImpacto);
  }
}
