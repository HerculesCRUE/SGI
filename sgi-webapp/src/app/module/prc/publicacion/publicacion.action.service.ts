import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { ActionService } from '@core/services/action-service';
import { ProyectoResumenService } from '@core/services/csp/proyecto-resumen/proyecto-resumen.service';
import { AutorService } from '@core/services/prc/autor/autor.service';
import { CampoProduccionCientificaService } from '@core/services/prc/campo-produccion-cientifica/campo-produccion-cientifica.service';
import { ConfiguracionCampoService } from '@core/services/prc/configuracion-campo/configuracion-campo.service';
import { IEstadoProduccionCientificaRequest } from '@core/services/prc/estado-produccion-cientifica/estado-produccion-cientifica-input';
import { ProduccionCientificaService } from '@core/services/prc/produccion-cientifica/produccion-cientifica.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CvnValorCampoService } from '../shared/cvn/services/cvn-valor-campo.service';
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
    campoProduccionCientificaService: CampoProduccionCientificaService,
    configuracionCampo: ConfiguracionCampoService,
    private produccionCientificaService: ProduccionCientificaService,
    cvnValorCampoService: CvnValorCampoService,
    personaService: PersonaService,
    proyectoResumenService: ProyectoResumenService,
    documentoService: DocumentoService,
    autorService: AutorService,
  ) {
    super();

    this.id = Number(route.snapshot.paramMap.get(PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[PRODUCCION_CIENTIFICA_DATA_KEY];
      this.enableEdit();

      this.datosGenerales = new PublicacionDatosGeneralesFragment(
        this.data?.produccionCientifica,
        campoProduccionCientificaService,
        configuracionCampo,
        cvnValorCampoService,
        produccionCientificaService,
        personaService,
        proyectoResumenService,
        documentoService,
        autorService);
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
