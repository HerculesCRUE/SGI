import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICampoProduccionCientifica } from '@core/models/prc/campo-produccion-cientifica';
import { IConfiguracionCampo } from '@core/models/prc/configuracion-campo';
import { environment } from '@env';
import {
  FindAllCtor, mixinFindAll,
  RSQLSgiRestFilter, SgiRestBaseService,
  SgiRestFilterOperator, SgiRestFindOptions
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CODIGO_CVN_MAP } from 'src/app/module/prc/shared/cvn/codigos-cvn';
import { EPIGRAFE_CVN_MAP } from 'src/app/module/prc/shared/cvn/epigrafes-cvn';

// tslint:disable-next-line: variable-name
const _ConfiguracionCampoServiceMixinBase:
  FindAllCtor<IConfiguracionCampo, IConfiguracionCampo> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionCampoService extends _ConfiguracionCampoServiceMixinBase {

  private static readonly MAPPING = '/configuraciones-campos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${ConfiguracionCampoService.MAPPING}`,
      http,
    );
  }

  findConfiguracionCampo(campoProduccionCientifica: ICampoProduccionCientifica): Observable<IConfiguracionCampo> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter(
        'epigrafeCVN', SgiRestFilterOperator.EQUALS, EPIGRAFE_CVN_MAP.get(campoProduccionCientifica?.produccionCientifica?.epigrafe)
      )
        .and('codigoCVN', SgiRestFilterOperator.EQUALS, CODIGO_CVN_MAP.get(campoProduccionCientifica?.codigo))
    };
    return this.findAll(options).pipe(
      map(({ items: [firstElement] }) => firstElement)
    );
  }
}
