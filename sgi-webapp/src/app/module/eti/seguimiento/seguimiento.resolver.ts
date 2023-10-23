import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IEvaluacionWithComentariosEnviados } from '../evaluacion-evaluador/evaluacion-evaluador-listado/evaluacion-evaluador-listado.component';

const MSG_NOT_FOUND = marker('error.load');

@Injectable()
export class SeguimientoResolver extends SgiResolverResolver<IEvaluacionWithComentariosEnviados> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: EvaluacionService,
    private authService: SgiAuthService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IEvaluacionWithComentariosEnviados> {
    const peticion = this.service.findById(Number(route.paramMap.get('id')));
    if (this.authService.hasAuthorityForAnyUO('ETI-EVC-INV-VR,')) {
      return this.service.isSeguimientoEvaluable(Number(route.paramMap.get('id'))).pipe(
        switchMap(response => {
          if (response) {
            return peticion as Observable<IEvaluacionWithComentariosEnviados>;
          } else {
            return throwError('NOT_FOUND');
          }
        }),
        switchMap(response => {
          return this.service.isComentariosEvaluadorEnviados(Number(route.paramMap.get('id'))).pipe(
            map(value => {
              response.enviada = value
              return response;
            })
          );
        })
      );
    } else {
      return peticion.pipe(
        switchMap(response => {
          if (response) {
            return peticion as Observable<IEvaluacionWithComentariosEnviados>;
          } else {
            return throwError('NOT_FOUND');
          }
        }),
        switchMap(response => {
          return this.service.isComentariosEvaluadorEnviados(Number(route.paramMap.get('id'))).pipe(
            map(value => {
              response.enviada = value
              return response;
            })
          );
        })
      );
    }
  }
}
