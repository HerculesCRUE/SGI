import { ComponentFactoryResolver, Injectable, Injector } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { COMITE, IComite } from '@core/models/eti/comite';
import { IMemoria } from '@core/models/eti/memoria';
import { IRetrospectiva } from '@core/models/eti/retrospectiva';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { Module } from '@core/module';
import { ActionService } from '@core/services/action-service';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { RespuestaService } from '@core/services/eti/respuesta.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { FormlyConfig, FormlyFormBuilder } from '@ngx-formly/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable } from 'rxjs';
import { concatMap, filter, map, switchMap, take, takeLast } from 'rxjs/operators';
import { PETICION_EVALUACION_ROUTE } from '../peticion-evaluacion/peticion-evaluacion-route-names';
import { MemoriaDatosGeneralesFragment } from './memoria-formulario/memoria-datos-generales/memoria-datos-generales.fragment';
import { MemoriaDocumentacionFragment } from './memoria-formulario/memoria-documentacion/memoria-documentacion.fragment';
import { MemoriaEvaluacionesFragment } from './memoria-formulario/memoria-evaluaciones/memoria-evaluaciones.fragment';
import { MemoriaFormularioFragment } from './memoria-formulario/memoria-formulario/memoria-formulario.fragment';
import { MemoriaInformesFragment } from './memoria-formulario/memoria-informes/memoria-informes.fragment';
import { MemoriaRetrospectivaFragment } from './memoria-formulario/memoria-retrospectiva/memoria-retrospectiva.fragment';
import { MemoriaSeguimientoAnualFragment } from './memoria-formulario/memoria-seguimiento-anual/memoria-seguimiento-anual.fragment';
import { MemoriaSeguimientoFinalFragment } from './memoria-formulario/memoria-seguimiento-final/memoria-seguimiento-final.fragment';

const MSG_PETICIONES_EVALUACION = marker('eti.peticion-evaluacion-link');

@Injectable()
export class MemoriaActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    FORMULARIO: 'formulario',
    DOCUMENTACION: 'documentacion',
    EVALUACIONES: 'evaluaciones',
    VERSIONES: 'versiones',
    SEGUIMIENTO_ANUAL: 'seguimientoAnual',
    SEGUIMIENTO_FINAL: 'seguimientoFinal',
    RETROSPECTIVA: 'retrospectiva'
  };

  public readonly memoria: IMemoria;
  public readonly: boolean;

  private datosGenerales: MemoriaDatosGeneralesFragment;
  private formularios: MemoriaFormularioFragment;
  private documentacion: MemoriaDocumentacionFragment;
  private seguimientoAnual: MemoriaSeguimientoAnualFragment;
  private seguimientoFinal: MemoriaSeguimientoFinalFragment;
  private retrospectiva: MemoriaRetrospectivaFragment;
  private evaluaciones: MemoriaEvaluacionesFragment;
  private versiones: MemoriaInformesFragment;

  private formlyFormBuilder: FormlyFormBuilder;

  private isInvestigador: boolean;

  constructor(
    logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    service: MemoriaService,
    private peticionEvaluacionService: PeticionEvaluacionService,
    personaService: PersonaService,
    documentoService: DocumentoService,
    formularioService: FormularioService,
    bloqueService: BloqueService,
    apartadoService: ApartadoService,
    vinculacionService: VinculacionService,
    datosAcademicosService: DatosAcademicosService,
    respuestaService: RespuestaService,
    evaluacionService: EvaluacionService,
    formlyConfig: FormlyConfig,
    componentFactoryResolver: ComponentFactoryResolver,
    injector: Injector
  ) {
    super();
    this.formlyFormBuilder = new FormlyFormBuilder(formlyConfig, componentFactoryResolver, injector);
    this.memoria = {} as IMemoria;
    if (route.snapshot.data.memoria) {
      this.memoria = route.snapshot.data.memoria;
      this.enableEdit();
      this.addPeticionEvaluacionLink(this.memoria.peticionEvaluacion.id);
      this.readonly = route.snapshot.data.readonly;
    }
    else {
      this.loadPeticionEvaluacion(history.state.idPeticionEvaluacion);
    }

    this.isInvestigador = route.snapshot.data.module === Module.INV;

    this.datosGenerales = new MemoriaDatosGeneralesFragment(fb, this.readonly, this.memoria?.id, service, personaService,
      peticionEvaluacionService);
    this.formularios = new MemoriaFormularioFragment(
      logger,
      this.readonly,
      this.memoria?.id,
      this.memoria?.comite,
      formularioService,
      bloqueService,
      apartadoService,
      respuestaService,
      peticionEvaluacionService,
      vinculacionService,
      datosAcademicosService,
      personaService,
      service,
      evaluacionService
    );
    this.documentacion = new MemoriaDocumentacionFragment(this.memoria?.id, this.isInvestigador, service, documentoService);
    this.seguimientoAnual = new MemoriaSeguimientoAnualFragment(
      logger,
      this.readonly,
      this.memoria?.id,
      this.memoria?.comite,
      formularioService,
      bloqueService,
      apartadoService,
      respuestaService,
      peticionEvaluacionService,
      vinculacionService,
      datosAcademicosService,
      personaService,
      service,
      evaluacionService
    );
    this.seguimientoFinal = new MemoriaSeguimientoFinalFragment(
      logger,
      this.readonly,
      this.memoria?.id,
      this.memoria?.comite,
      formularioService,
      bloqueService,
      apartadoService,
      respuestaService,
      peticionEvaluacionService,
      vinculacionService,
      datosAcademicosService,
      personaService,
      service,
      evaluacionService
    );
    this.retrospectiva = new MemoriaRetrospectivaFragment(
      logger,
      this.readonly,
      this.memoria?.id,
      this.memoria?.comite,
      formularioService,
      bloqueService,
      apartadoService,
      respuestaService,
      peticionEvaluacionService,
      vinculacionService,
      datosAcademicosService,
      personaService,
      service,
      evaluacionService
    );
    this.evaluaciones = new MemoriaEvaluacionesFragment(this.memoria?.id, service);
    this.versiones = new MemoriaInformesFragment(this.memoria?.id, service);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    if (this.isEdit()) {
      this.addFragment(this.FRAGMENT.FORMULARIO, this.formularios);
      this.addFragment(this.FRAGMENT.DOCUMENTACION, this.documentacion);
      this.addFragment(this.FRAGMENT.SEGUIMIENTO_ANUAL, this.seguimientoAnual);
      this.addFragment(this.FRAGMENT.SEGUIMIENTO_FINAL, this.seguimientoFinal);
      this.addFragment(this.FRAGMENT.RETROSPECTIVA, this.retrospectiva);
      this.addFragment(this.FRAGMENT.EVALUACIONES, this.evaluaciones);
      this.addFragment(this.FRAGMENT.VERSIONES, this.versiones);

      this.datosGenerales.initialized$.pipe(
        filter(value => value),
        take(1)
      ).subscribe(
        (value) => {
          if (value && this.getComite()?.id === COMITE.CEEA) {
            this.subscriptions.push(this.datosGenerales.getFormGroup().controls.titulo.valueChanges.pipe(
            ).subscribe(
              () => {
                if (!this.formularios.isInitialized()) {
                  this.formularios.initialize();
                  this.formularios.initialized$.pipe(
                    filter((initialized) => initialized),
                    take(1)
                  ).subscribe(
                    (initialized) => {
                      if (initialized && this.formularios.isEditable()) {
                        this.formularios.refreshMemoria(this.datosGenerales.getValue());
                      }
                    }
                  );
                }
                else if (this.formularios.isEditable()) {
                  this.formularios.refreshMemoria(this.datosGenerales.getValue());
                }
              }
            ));
          }
        }
      );
    }

  }

  private addPeticionEvaluacionLink(idPeticionEvaluacion: number): void {
    this.addActionLink({
      title: MSG_PETICIONES_EVALUACION,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      routerLink: ['../..', PETICION_EVALUACION_ROUTE, idPeticionEvaluacion.toString()]
    });
  }

  private loadPeticionEvaluacion(id: number): void {
    if (id) {
      this.peticionEvaluacionService.findById(id).pipe(
        map((peticionEvaluacion) => {
          this.memoria.peticionEvaluacion = peticionEvaluacion;
          this.addPeticionEvaluacionLink(id);
          this.datosGenerales.loadResponsable(id);
        })
      ).subscribe();
    }
  }

  public getComite(): IComite {
    return this.memoria?.comite || this.datosGenerales.getFormGroup()?.controls?.comite?.value;
  }

  public getEstadoMemoria(): TipoEstadoMemoria {
    return this.memoria.estadoActual;
  }

  public getRetrospectiva(): IRetrospectiva {
    return this.memoria.retrospectiva;
  }


  saveOrUpdate(action?: any): Observable<void> {
    const neeeMemoryUpdate = this.getComite()?.id === COMITE.CEEA
      && this.isEdit()
      && this.datosGenerales.hasChanges()
      && !this.formularios.hasChanges()
      && this.formularios.isEditable()
      && !this.readonly;

    let operation$ = super.saveOrUpdate(action);

    if (neeeMemoryUpdate) {
      operation$ = operation$.pipe(
        switchMap(() => {
          // Llegados a este punto, siempre debería estar inicializado, por si acaso lo inicializamos.
          this.formularios.initialize();
          return this.formularios.initialized$.pipe(filter((value) => value), take(1), map((v) => this.formularios));
        }),
        switchMap((fragment) => {
          return from(fragment.blocks$.value).pipe(
            // Se excluyen los bloques que no hayan sido persistidos
            filter(block => fragment.blocks$.value.indexOf(block) <= fragment.getLastFilledBlockIndex()),
            concatMap(block => {
              fragment.selectedIndex$.next(fragment.blocks$.value.indexOf(block));
              return block.loaded$.pipe(filter((value) => value), take(1), map(() => block.formlyData));
            }),
            map(formlyData => {
              // Si el formgroup no ha sido inicializado, lo inicializamos
              if (!Object.keys(formlyData.formGroup.controls).length) {
                this.formlyFormBuilder.buildForm(formlyData.formGroup, formlyData.fields, formlyData.model, formlyData.options);
              }
              return fragment;
            }),
            takeLast(1)
          );
        }),
        switchMap((fragment) => fragment.saveOrUpdate()),
      );
    }
    return operation$;
  }
}
