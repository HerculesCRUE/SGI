import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiError } from '@core/errors/sgi-error';
import { sortGrupoEquipoByPersonaNombre, sortGrupoEquipoByRolProyectoOrden } from '@core/models/csp/grupo-equipo';
import { sortProyectoEquipoByPersonaNombre, sortProyectoEquipoByRolProyectoOrden } from '@core/models/csp/proyecto-equipo';
import { TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { IPersona } from '@core/models/sgp/persona';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RelacionEjecucionEconomicaService } from '@core/services/csp/relacion-ejecucion-economica/relacion-ejecucion-economica.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable, forkJoin, from, throwError } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';
import { IEjecucionEconomicaData } from './ejecucion-economica.action.service';

const MSG_NOT_FOUND = marker('error.load');
const MSG_PROYECTO_SGE_NOT_FOUND = marker('error.sge.proyecto.not-found');

export const EJECUCION_ECONOMICA_DATA_KEY = 'ejecucionEconomicaData';

@Injectable()
export class EjecucionEconomicaDataResolver extends SgiResolverResolver<IEjecucionEconomicaData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private relacionEjecucionEconomicaService: RelacionEjecucionEconomicaService,
    private grupoService: GrupoService,
    private personaService: PersonaService,
    private proyectoService: ProyectoService,
    private proyectoSgeService: ProyectoSgeService,
    private configuracionService: ConfigService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IEjecucionEconomicaData> {

    return forkJoin({
      configuracion: this.configuracionService.getConfiguracion(),
      relaciones: this.relacionEjecucionEconomicaService.findRelacionesProyectoSgeRef(route.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID))
    }).pipe(
      map(({ configuracion, relaciones }) => {
        return {
          configuracion,
          proyectoSge: { id: route.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID) } as IProyectoSge,
          readonly: false,
          relaciones
        } as IEjecucionEconomicaData;
      }),
      switchMap(data =>
        this.proyectoSgeService.findById(data.proyectoSge.id).pipe(
          map(proyectoSge => {
            if (!proyectoSge) {
              throw new SgiError(MSG_PROYECTO_SGE_NOT_FOUND);
            }

            data.proyectoSge = proyectoSge;
            return data;
          }),
          catchError(error => {
            if (error instanceof SgiError) {
              this.setErrorMessage(`${error.title}${!!error.message ? ': ' + error.message : ''}`);
            }
            return throwError(error);
          })
        )
      ),
      switchMap(response =>
        from(response.relaciones).pipe(
          mergeMap(relacion => {
            let responsables$: Observable<IPersona[]>;

            switch (relacion.tipoEntidad) {
              case TipoEntidad.GRUPO:
                responsables$ = this.grupoService.findInvestigadoresPrincipales(relacion.id).pipe(
                  filter(responsables => !!responsables?.length),
                  switchMap(responsables => this.personaService.findAllByIdIn(responsables.map(responsable => responsable.persona.id)).pipe(
                    map(personas => {
                      responsables.forEach(responsable => {
                        responsable.persona = personas.items.find(persona => persona.id === responsable.persona.id);
                      })

                      responsables.sort((a, b) => {
                        return sortGrupoEquipoByRolProyectoOrden(a, b)
                          || sortGrupoEquipoByPersonaNombre(a, b);
                      });

                      return responsables.map(responsable => responsable.persona)
                    }),
                    catchError((error) => {
                      this.logger.error(error);
                      return EMPTY;
                    })
                  ))
                );

                break;
              case TipoEntidad.PROYECTO:
                responsables$ = this.proyectoService.findInvestigadoresPrincipalesActuales(relacion.id).pipe(
                  filter(responsables => !!responsables?.length),
                  switchMap(responsables => this.personaService.findAllByIdIn(responsables.map(responsable => responsable.persona.id)).pipe(
                    map(personas => {
                      responsables.forEach(responsable => {
                        responsable.persona = personas.items.find(persona => persona.id === responsable.persona.id);
                      })

                      responsables.sort((a, b) => {
                        return sortProyectoEquipoByRolProyectoOrden(a, b)
                          || sortProyectoEquipoByPersonaNombre(a, b);
                      });

                      return responsables.map(responsable => responsable.persona)
                    }),
                    catchError((error) => {
                      this.logger.error(error);
                      return EMPTY;
                    })
                  ))
                );

                break;
              default:
                throw Error(`Invalid tipoEntidad "${relacion.tipoEntidad}"`);
            }

            return responsables$.pipe(
              map(responsables => {
                relacion.responsables = responsables;
                return relacion;
              })
            );
          }),
          toArray(),
          map(() => {
            return response;
          })
        )
      )
    );
  }

}
