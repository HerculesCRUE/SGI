import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { GRUPO_DATA_KEY } from '../grupo/grupo-data.resolver';
import { IGrupoData } from '../grupo/grupo.action.service';
import { GRUPO_LINEA_INVESTIGACION_ROUTE_PARAMS } from './grupo-linea-investigacion-route-params';
import { IGrupoLineaInvestigacionData } from './grupo-linea-investigacion.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const GRUPO_LINEA_INVESTIGACION_DATA_KEY = 'grupoLineaInvestigacionData';

@Injectable()
export class GrupoLineaInvestigacionDataResolver extends SgiResolverResolver<IGrupoLineaInvestigacionData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: GrupoLineaInvestigacionService,
    private grupoService: GrupoService,
    private authService: SgiAuthService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IGrupoLineaInvestigacionData> {
    const grupoData: IGrupoData = route.parent.data[GRUPO_DATA_KEY];
    const grupoLineaInvestigacionId = Number(route.paramMap.get(GRUPO_LINEA_INVESTIGACION_ROUTE_PARAMS.ID));
    if (grupoLineaInvestigacionId) {
      return this.service.exists(grupoLineaInvestigacionId).pipe(
        switchMap(value => {
          if (!value) {
            return throwError('NOT_FOUND');
          }
          return of(
            {
              id: grupoLineaInvestigacionId,
              isInvestigador: this.authService.hasAnyAuthority(['CSP-GIN-INV-VR'])
            } as IGrupoLineaInvestigacionData
          );
        }),
        switchMap(data => this.isReadonly(data, grupoLineaInvestigacionId)),
        switchMap(data => this.loadGrupoLineaInvestigacionData(grupoData, data)),
        switchMap(data => this.loadGrupoData(grupoData, data)),
      );
    } else {
      const data = {
        isInvestigador: this.authService.hasAnyAuthority(['CSP-GIN-INV-VR'])
      } as IGrupoLineaInvestigacionData;
      return merge(
        this.loadGrupoLineaInvestigacionData(grupoData, data),
        this.loadGrupoData(grupoData, data));
    }
  }

  private loadGrupoLineaInvestigacionData(grupoData: IGrupoData, data: IGrupoLineaInvestigacionData): Observable<IGrupoLineaInvestigacionData> {
    return this.grupoService.findLineasInvestigacion(grupoData.grupo.id).pipe(
      map(gruposLineasInvestigacionResponse => {
        data.grupo = grupoData.grupo;
        data.gruposLineasInvestigacion = gruposLineasInvestigacionResponse.items;
        return data;
      })
    );
  }

  private loadGrupoData(grupoData: IGrupoData, data: IGrupoLineaInvestigacionData): Observable<IGrupoLineaInvestigacionData> {
    return this.grupoService.findById(grupoData.grupo.id).pipe(
      map(grupoResponse => {
        data.grupo = grupoResponse;
        return data;
      })
    );
  }

  private isReadonly(data: IGrupoLineaInvestigacionData, grupoLineaInvestigacionId: number): Observable<IGrupoLineaInvestigacionData> {
    if (grupoLineaInvestigacionId) {
      return this.service.modificable(grupoLineaInvestigacionId)
        .pipe(
          map((value: boolean) => {
            data.readonly = !value;
            return data;
          })
        );
    } else {
      data.readonly = false;
      return of(data);
    }
  }


}
