import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { ActionService } from '@core/services/action-service';
import { IEstadoProduccionCientificaRequest } from '@core/services/prc/estado-produccion-cientifica/estado-produccion-cientifica-input';
import { ProduccionCientificaService } from '@core/services/prc/produccion-cientifica/produccion-cientifica.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { Observable } from 'rxjs';
import { concatMap, map, tap } from 'rxjs/operators';
import { CvnValorCampoService } from '../shared/cvn/services/cvn-valor-campo.service';
import { ProduccionCientificaInitializerService } from '../shared/produccion-cientifica-initializer.service';
import { PRODUCCION_CIENTIFICA_DATA_KEY, PRODUCCION_CIENTIFICA_ROUTE_PARAMS } from '../shared/produccion-cientifica-route-params';
import { IProduccionCientificaData } from '../shared/produccion-cientifica.resolver';
import { PublicacionDatosGeneralesFragment } from './publicacion-formulario/publicacion-datos-generales/publicacion-datos-generales.fragment';

@Injectable()
export class PublicacionActionService extends ActionService {

  public readonly id: number;
  private data: IProduccionCientificaData;
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
  };
  private datosGenerales: PublicacionDatosGeneralesFragment;
  // tslint:disable-next-line: variable-name
  private _canEdit: boolean;

  get canEdit(): boolean {
    return this._canEdit;
  }

  constructor(
    route: ActivatedRoute,
    private produccionCientificaService: ProduccionCientificaService,
    initializerService: ProduccionCientificaInitializerService,
    cvnValorCampoService: CvnValorCampoService,
    personaService: PersonaService,
  ) {
    super();

    this.id = Number(route.snapshot.paramMap.get(PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[PRODUCCION_CIENTIFICA_DATA_KEY];
      this._canEdit = this.data.canEdit;
      this.enableEdit();

      this.datosGenerales = new PublicacionDatosGeneralesFragment(
        this.data?.produccionCientifica,
        initializerService,
        cvnValorCampoService,
        personaService);
      this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    }
  }

  isProduccionCientificaDisabled$(): Observable<boolean> {
    return this.datosGenerales.isProduccionCientificaDisabled$();
  }


  validar(): Observable<IProduccionCientifica> {
    return this.produccionCientificaService.validar(this.data.produccionCientifica?.id)
      .pipe(
        tap(produccionCientifica => this.datosGenerales.refreshDatosGenerales(produccionCientifica))
      );
  }

  rechazar(estadoProduccionCientifica: IEstadoProduccionCientificaRequest): Observable<IProduccionCientifica> {
    return this.produccionCientificaService.rechazar(this.data.produccionCientifica?.id, estadoProduccionCientifica)
      .pipe(
        tap(produccionCientifica => this.datosGenerales.refreshDatosGenerales(produccionCientifica))
      );
  }

  validarInvestigador(): Observable<IProduccionCientifica> {
    return this.produccionCientificaService.validar(this.data.produccionCientifica?.id)
      .pipe(
        tap(produccionCientifica => this.datosGenerales.refreshDatosGenerales(produccionCientifica)),
        concatMap(produccionCientifica => this.updateCanEdit(produccionCientifica))
      );
  }

  rechazarInvestigador(estadoProduccionCientifica: IEstadoProduccionCientificaRequest): Observable<IProduccionCientifica> {
    return this.produccionCientificaService.rechazar(this.data.produccionCientifica?.id, estadoProduccionCientifica)
      .pipe(
        tap(produccionCientifica => this.datosGenerales.refreshDatosGenerales(produccionCientifica)),
        concatMap(produccionCientifica => this.updateCanEdit(produccionCientifica))
      );
  }

  private updateCanEdit(produccionCientifica: IProduccionCientifica): Observable<IProduccionCientifica> {
    return this.produccionCientificaService.isEditableByInvestigador(produccionCientifica.id).pipe(
      tap(canEdit => this._canEdit = canEdit),
      map(() => produccionCientifica)
    );
  }
}
