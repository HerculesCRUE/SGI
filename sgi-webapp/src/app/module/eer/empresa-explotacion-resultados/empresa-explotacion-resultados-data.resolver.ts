import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { EmpresaExplotacionResultadosService } from '@core/services/eer/empresa-explotacion-resultados/empresa-explotacion-resultados.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_PARAMS } from './empresa-explotacion-resultados-route-params';
import { IEmpresaExplotacionResultadosData } from './empresa-explotacion-resultados.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY = 'empresaData';

@Injectable()
export class EmpresaExplotacionResultadosDataResolver extends SgiResolverResolver<IEmpresaExplotacionResultadosData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: EmpresaExplotacionResultadosService,
    private authService: SgiAuthService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IEmpresaExplotacionResultadosData> {
    const empresaId = Number(route.paramMap.get(EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_PARAMS.ID));

    return this.service.exists(empresaId).pipe(
      switchMap(value => {
        if (!value) {
          return throwError('NOT_FOUND');
        }
        return of(
          {
            empresa: { id: empresaId } as IEmpresaExplotacionResultados,
            isInvestigador: this.authService.hasAnyAuthority(['EER-EER-INV-V'])
          } as IEmpresaExplotacionResultadosData
        );
      }),
      switchMap(data => this.isReadonly(data, empresaId))
    );
  }

  private isReadonly(data: IEmpresaExplotacionResultadosData, empresaId: number): Observable<IEmpresaExplotacionResultadosData> {
    if (empresaId) {
      data.readonly = !this.authService.hasAnyAuthority(['EER-EER-E']);
      return of(data);
    } else {
      data.readonly = false;
      return of(data);
    }
  }


}
