import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { ActionService } from '@core/services/action-service';
import { IEstadoProduccionCientificaRequest } from '@core/services/prc/estado-produccion-cientifica/estado-produccion-cientifica-input';
import { ProduccionCientificaService } from '@core/services/prc/produccion-cientifica/produccion-cientifica.service';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CvnValorCampoService } from '../shared/cvn/services/cvn-valor-campo.service';
import { ProduccionCientificaInitializerService } from '../shared/produccion-cientifica-initializer.service';
import { PRODUCCION_CIENTIFICA_ROUTE_PARAMS } from '../shared/produccion-cientifica-route-params';
import { IProduccionCientificaData, PRODUCCION_CIENTIFICA_DATA_KEY } from '../shared/produccion-cientifica.resolver';
import { ActividadIdiDatosGeneralesFragment } from './actividad-idi-formulario/actividad-idi-datos-generales/actividad-idi-datos-generales.fragment';

@Injectable()
export class ActividadIdiActionService extends ActionService {

  public readonly id: number;
  private data: IProduccionCientificaData;
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
  };
  private datosGenerales: ActividadIdiDatosGeneralesFragment;

  get canEdit(): boolean {
    return this.data?.canEdit ?? true;
  }

  constructor(
    route: ActivatedRoute,
    private produccionCientificaService: ProduccionCientificaService,
    initializerService: ProduccionCientificaInitializerService,
    cvnValorCampoService: CvnValorCampoService,
  ) {
    super();

    this.id = Number(route.snapshot.paramMap.get(PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[PRODUCCION_CIENTIFICA_DATA_KEY];
      this.enableEdit();

      this.datosGenerales = new ActividadIdiDatosGeneralesFragment(
        this.data?.produccionCientifica,
        initializerService,
        cvnValorCampoService);
      this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    }
  }

  isProduccionCientificaEditable$(): Observable<boolean> {
    return this.datosGenerales.isProduccionCientificaEditable$();
  }

  validar(): Observable<IProduccionCientifica> {
    return this.produccionCientificaService.validar(this.data.produccionCientifica?.id)
      .pipe(
        tap(produccionCientifica => this.datosGenerales.emitProduccionCientifica(produccionCientifica))
      );
  }

  rechazar(estadoProduccionCientifica: IEstadoProduccionCientificaRequest): Observable<IProduccionCientifica> {
    return this.produccionCientificaService.rechazar(this.data.produccionCientifica?.id, estadoProduccionCientifica)
      .pipe(
        tap(produccionCientifica => this.datosGenerales.emitProduccionCientifica(produccionCientifica))
      );
  }
}
