import { Injectable } from '@angular/core';
import { ICampoProduccionCientifica } from '@core/models/prc/campo-produccion-cientifica';
import { IValorCampo } from '@core/models/prc/valor-campo';
import { CampoProduccionCientificaService } from '@core/services/prc/campo-produccion-cientifica/campo-produccion-cientifica.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CvnValorCampoService {

  constructor(private campoProduccionCientificaService: CampoProduccionCientificaService) { }

  findCvnValorCampo = (campoProduccionCientifica: ICampoProduccionCientifica): Observable<IValorCampo[]> => {
    const options: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('orden', SgiRestSortDirection.ASC)
    };
    return this.campoProduccionCientificaService.findValores(campoProduccionCientifica?.id, options).pipe(
      map(response => response.items),
      map(items => items.map(item => {
        item.campoProduccionCientifica = campoProduccionCientifica;
        return item;
      }))
    );
  }
}
