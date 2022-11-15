import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConfiguracionService } from '@core/services/csp/configuracion.service';
import { RelacionEjecucionEconomicaService } from '@core/services/csp/relacion-ejecucion-economica/relacion-ejecucion-economica.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, from, Observable, of, throwError } from 'rxjs';
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
    private configuracionService: ConfiguracionService,
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IEjecucionEconomicaData> {

    return this.relacionEjecucionEconomicaService.findRelacionesProyectoSgeRef(route.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID)).pipe(
      map(relaciones => {
        return {
          proyectoSge: { id: route.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID) } as IProyectoSge,
          readonly: false,
          relaciones
        } as IEjecucionEconomicaData;
      }),
      switchMap(data => {
        return this.proyectoSgeService.findById(data.proyectoSge.id).pipe(
          switchMap(proyectoSge => {
            if (!proyectoSge) {
              return throwError(MSG_PROYECTO_SGE_NOT_FOUND);
            }

            data.proyectoSge = proyectoSge;
            return of(data);
          })
        );
      }),
      switchMap(response =>
        from(response.relaciones).pipe(
          mergeMap(relacion => {
            let serviceGetResponsables: GrupoService | ProyectoService;

            switch (relacion.tipoEntidad) {
              case TipoEntidad.GRUPO:
                serviceGetResponsables = this.grupoService;
                break;
              case TipoEntidad.PROYECTO:
                serviceGetResponsables = this.proyectoService;
                break;
              default:
                throw Error(`Invalid tipoEntidad "${relacion.tipoEntidad}"`);
            }

            return serviceGetResponsables.findPersonaRefInvestigadoresPrincipales(relacion.id).pipe(
              filter(personaRefs => !!personaRefs),
              switchMap(personaRefs => this.personaService.findAllByIdIn(personaRefs).pipe(
                catchError((error) => {
                  this.logger.error(error);
                  return EMPTY;
                })
              )),
              map(responsables => {
                relacion.responsables = responsables.items;
                return relacion;
              })
            );
          }),
          toArray(),
          map(() => {
            return response;
          })
        )
      ),
      switchMap(data =>
        this.configuracionService.getConfiguracion().pipe(
          map(configuracion => {
            data.configuracion = configuracion;
            return data;
          })
        )
      )
    );
  }

}
