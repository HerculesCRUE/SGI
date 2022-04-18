import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { ActionService } from '@core/services/action-service';
import { IEstadoProduccionCientificaRequest } from '@core/services/prc/estado-produccion-cientifica/estado-produccion-cientifica-input';
import { ProduccionCientificaService } from '@core/services/prc/produccion-cientifica/produccion-cientifica.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CvnValorCampoService } from '../shared/cvn/services/cvn-valor-campo.service';
import { ProduccionCientificaInitializerService } from '../shared/produccion-cientifica-initializer.service';
import { PRODUCCION_CIENTIFICA_ROUTE_PARAMS } from '../shared/produccion-cientifica-route-params';
import { IProduccionCientificaData, PRODUCCION_CIENTIFICA_DATA_KEY } from '../shared/produccion-cientifica.resolver';
import { PublicacionDatosGeneralesFragment } from './publicacion-formulario/publicacion-datos-generales/publicacion-datos-generales.fragment';

@Injectable()
export class PublicacionActionService extends ActionService {

  public readonly id: number;
  private data: IProduccionCientificaData;
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
  };
  private datosGenerales: PublicacionDatosGeneralesFragment;

  get canEdit(): boolean {
    return this.data?.canEdit ?? true;
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
      this.enableEdit();

      this.datosGenerales = new PublicacionDatosGeneralesFragment(
        this.data?.produccionCientifica,
        initializerService,
        cvnValorCampoService,
        personaService);
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
