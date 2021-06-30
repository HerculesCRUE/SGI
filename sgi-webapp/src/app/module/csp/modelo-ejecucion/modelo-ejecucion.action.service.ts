import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { ActionService } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloTipoDocumentoService } from '@core/services/csp/modelo-tipo-documento.service';
import { ModeloTipoEnlaceService } from '@core/services/csp/modelo-tipo-enlace.service';
import { ModeloTipoFaseService } from '@core/services/csp/modelo-tipo-fase.service';
import { ModeloTipoFinalidadService } from '@core/services/csp/modelo-tipo-finalidad.service';
import { ModeloTipoHitoService } from '@core/services/csp/modelo-tipo-hito.service';
import { ModeloUnidadService } from '@core/services/csp/modelo-unidad.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ModeloEjecucionDatosGeneralesFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-datos-generales/modelo-ejecucion-datos-generales.fragment';
import { ModeloEjecucionTipoDocumentoFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-documento/modelo-ejecucion-tipo-documento.fragment';
import { ModeloEjecucionTipoEnlaceFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-enlace/modelo-ejecucion-tipo-enlace.fragment';
import { ModeloEjecucionTipoFaseFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-fase/modelo-ejecucion-tipo-fase.fragment';
import { ModeloEjecucionTipoFinalidadFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-finalidad/modelo-ejecucion-tipo-finalidad.fragment';
import { ModeloEjecucionTipoHitoFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-hito/modelo-ejecucion-tipo-hito.fragment';
import { ModeloEjecucionTipoUnidadGestionFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-unidad-gestion/modelo-ejecucion-tipo-unidad-gestion.fragment';



@Injectable()
export class ModeloEjecucionActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    TIPO_ENLACES: 'tipo-enlaces',
    TIPO_FINALIDADES: 'tipo-finalidades',
    TIPO_FASES: 'tipo-fases',
    TIPO_DOCUMENTOS: 'tipo-documentos',
    TIPO_HITOS: 'tipo-hitos',
    UNIDAD_GESTION: 'unidades',
  };

  private modeloEjecucion: IModeloEjecucion;
  private datosGenerales: ModeloEjecucionDatosGeneralesFragment;
  private tipoEnlaces: ModeloEjecucionTipoEnlaceFragment;
  private tipoFinalidades: ModeloEjecucionTipoFinalidadFragment;
  private tipoFases: ModeloEjecucionTipoFaseFragment;
  private tipoDocumentos: ModeloEjecucionTipoDocumentoFragment;
  private tipoHitos: ModeloEjecucionTipoHitoFragment;
  private tipoUnidadGestion: ModeloEjecucionTipoUnidadGestionFragment;

  constructor(
    readonly logger: NGXLogger,
    route: ActivatedRoute,
    modeloEjecucionService: ModeloEjecucionService,
    modeloTipoEnlaceService: ModeloTipoEnlaceService,
    modeloTipoFinalidadService: ModeloTipoFinalidadService,
    modeloTipoFaseService: ModeloTipoFaseService,
    modeloTipoDocumentoService: ModeloTipoDocumentoService,
    modeloTipoHitoService: ModeloTipoHitoService,
    modeloUnidadService: ModeloUnidadService,
    unidadGestionService: UnidadGestionService) {
    super();
    this.modeloEjecucion = {} as IModeloEjecucion;
    if (route.snapshot.data.modeloEjecucion) {
      this.modeloEjecucion = route.snapshot.data.modeloEjecucion;
      this.enableEdit();
    }
    this.datosGenerales = new ModeloEjecucionDatosGeneralesFragment(logger, this.modeloEjecucion?.id, modeloEjecucionService);
    this.tipoEnlaces = new ModeloEjecucionTipoEnlaceFragment(this.modeloEjecucion?.id,
      modeloEjecucionService, modeloTipoEnlaceService);
    this.tipoFinalidades = new ModeloEjecucionTipoFinalidadFragment(this.modeloEjecucion?.id,
      modeloEjecucionService, modeloTipoFinalidadService);
    this.tipoFases = new ModeloEjecucionTipoFaseFragment(this.modeloEjecucion?.id, modeloEjecucionService,
      modeloTipoFaseService);
    this.tipoDocumentos = new ModeloEjecucionTipoDocumentoFragment(this.modeloEjecucion?.id, modeloEjecucionService,
      modeloTipoDocumentoService);
    this.tipoHitos = new ModeloEjecucionTipoHitoFragment(this.modeloEjecucion?.id, modeloEjecucionService,
      modeloTipoHitoService);
    this.tipoUnidadGestion = new ModeloEjecucionTipoUnidadGestionFragment(this.modeloEjecucion?.id, modeloEjecucionService,
      modeloUnidadService, unidadGestionService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.TIPO_FASES, this.tipoFases);
    this.addFragment(this.FRAGMENT.TIPO_FINALIDADES, this.tipoFinalidades);
    this.addFragment(this.FRAGMENT.TIPO_ENLACES, this.tipoEnlaces);
    this.addFragment(this.FRAGMENT.TIPO_DOCUMENTOS, this.tipoDocumentos);
    this.addFragment(this.FRAGMENT.TIPO_HITOS, this.tipoHitos);
    this.addFragment(this.FRAGMENT.UNIDAD_GESTION, this.tipoUnidadGestion);

    this.subscriptions.push(this.tipoDocumentos.initialized$.subscribe(
      (value) => {
        if (value) {
          this.tipoFases.initialize();
        }
      })
    );
    this.subscriptions.push(this.tipoFases.modeloTipoFase$.subscribe(
      (modelos) => this.tipoDocumentos.modeloTipoFases = modelos.map(modelo => modelo.value)
    ));
  }

  saveOrUpdate(): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }
    if (this.isEdit()) {
      return this.tipoFases.saveOrUpdate().pipe(
        switchMap(() => {
          this.tipoFases.refreshInitialState(true);
          return super.saveOrUpdate();
        })
      );
    } else {
      return this.datosGenerales.saveOrUpdate().pipe(
        switchMap((key) => {
          this.datosGenerales.refreshInitialState(true);
          if (typeof key === 'string' || typeof key === 'number') {
            this.onKeyChange(key);
          }
          return this.tipoFases.saveOrUpdate();
        }),
        switchMap(() => {
          this.tipoFases.refreshInitialState(true);
          return super.saveOrUpdate();
        })
      );
    }
  }
}
