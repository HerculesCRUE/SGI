import { ComponentFactoryResolver, Injectable, Injector } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IChecklist } from '@core/models/eti/checklist';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { ActionService, IFragment } from '@core/services/action-service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { ChecklistService } from '@core/services/eti/checklist/checklist.service';
import { EquipoTrabajoService } from '@core/services/eti/equipo-trabajo.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { RespuestaService } from '@core/services/eti/respuesta.service';
import { TareaService } from '@core/services/eti/tarea.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { FormlyConfig, FormlyFormBuilder } from '@ngx-formly/core';
import { SgiAuthService } from '@sgi/framework/auth/';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of, throwError } from 'rxjs';
import { concatMap, filter, map, mergeMap, switchMap, take, takeLast, tap } from 'rxjs/operators';
import { MemoriaFormularioFragment } from '../memoria/memoria-formulario/memoria-formulario/memoria-formulario.fragment';
import { EquipoInvestigadorListadoFragment } from './peticion-evaluacion-formulario/equipo-investigador/equipo-investigador-listado/equipo-investigador-listado.fragment';
import { MemoriasListadoFragment } from './peticion-evaluacion-formulario/memorias-listado/memorias-listado.fragment';
import { PeticionEvaluacionDatosGeneralesFragment } from './peticion-evaluacion-formulario/peticion-evaluacion-datos-generales/peticion-evaluacion-datos-generales.fragment';
import { PeticionEvaluacionTareasFragment } from './peticion-evaluacion-formulario/peticion-evaluacion-tareas/peticion-evaluacion-tareas-listado/peticion-evaluacion-tareas-listado.fragment';

@Injectable()
export class PeticionEvaluacionActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    EQUIPO_INVESTIGADOR: 'equipoInvestigador',
    TAREAS: 'tareas',
    MEMORIAS: 'memorias'
  };

  public readonly: boolean;

  private peticionEvaluacion: IPeticionEvaluacion;
  private datosGenerales: PeticionEvaluacionDatosGeneralesFragment;
  private equipoInvestigadorListado: EquipoInvestigadorListadoFragment;
  private tareas: PeticionEvaluacionTareasFragment;
  private memoriasListado: MemoriasListadoFragment;
  private fragmentos: IFragment[] = [];
  private checklist: IChecklist;

  private formlyFormBuilder: FormlyFormBuilder;


  constructor(
    private readonly logger: NGXLogger,
    fb: FormBuilder,
    protected readonly peticionEvaluacionService: PeticionEvaluacionService,
    private readonly route: ActivatedRoute,
    sgiAuthService: SgiAuthService,
    protected readonly personaService: PersonaService,
    protected readonly equipoTrabajoService: EquipoTrabajoService,
    protected readonly tareaService: TareaService,
    protected readonly memoriaService: MemoriaService,
    protected readonly datosAcademicosService: DatosAcademicosService,
    protected readonly vinculacionService: VinculacionService,
    protected readonly checklistService: ChecklistService,
    protected readonly solicitudService: SolicitudService,
    protected readonly formularioService: FormularioService,
    protected readonly bloqueService: BloqueService,
    protected readonly apartadoService: ApartadoService,
    protected readonly respuestaService: RespuestaService,
    protected readonly evaluacionService: EvaluacionService,
    formlyConfig: FormlyConfig,
    componentFactoryResolver: ComponentFactoryResolver,
    injector: Injector
  ) {
    super();

    this.formlyFormBuilder = new FormlyFormBuilder(formlyConfig, componentFactoryResolver, injector);

    this.peticionEvaluacion = {} as IPeticionEvaluacion;

    if (route.snapshot.data.peticionEvaluacion) {
      this.peticionEvaluacion = route.snapshot.data.peticionEvaluacion;
      this.enableEdit();
      this.readonly = route.snapshot.data.readonly;
    }

    this.route.queryParams.subscribe(params => {
      if (params?.checklist) {
        this.checklist = JSON.parse(params.checklist);
        params.checklist.remove();
      }
    });

    this.datosGenerales =
      new PeticionEvaluacionDatosGeneralesFragment(
        fb, this.peticionEvaluacion?.id, peticionEvaluacionService, sgiAuthService, checklistService, solicitudService, this.checklist, this.readonly);
    this.equipoInvestigadorListado = new EquipoInvestigadorListadoFragment(
      this.peticionEvaluacion?.id, personaService, peticionEvaluacionService, sgiAuthService, datosAcademicosService, vinculacionService);
    this.memoriasListado = new MemoriasListadoFragment(this.peticionEvaluacion?.id, peticionEvaluacionService, memoriaService);
    this.tareas = new PeticionEvaluacionTareasFragment(this.peticionEvaluacion?.id, personaService, tareaService,
      peticionEvaluacionService, this.equipoInvestigadorListado, this.memoriasListado);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.EQUIPO_INVESTIGADOR, this.equipoInvestigadorListado);
    this.tareas.setEquiposTrabajo(this.equipoInvestigadorListado.equiposTrabajo$.value.map((equipoTrabajo) => equipoTrabajo.value));
    this.tareas.setMemorias(this.memoriasListado.memorias$.value.map((memoria) => memoria.value));
    this.equipoInvestigadorListado.setMemorias(this.memoriasListado.memorias$.value.map((memoria) => memoria.value));
    this.addFragment(this.FRAGMENT.TAREAS, this.tareas);
    this.addFragment(this.FRAGMENT.MEMORIAS, this.memoriasListado);

    this.fragmentos.push(this.datosGenerales);
    this.fragmentos.push(this.equipoInvestigadorListado);
    this.fragmentos.push(this.tareas);
    this.fragmentos.push(this.memoriasListado);

    this.datosGenerales.initialize();

    this.equipoInvestigadorListado.equiposTrabajo$.subscribe(list => {
      this.tareas.setEquiposTrabajo(list.map((equipoTrabajo) => equipoTrabajo.value));
    });

    this.memoriasListado.memorias$.subscribe(list => {
      this.tareas.setMemorias(list.map((memoria) => memoria.value));
      this.equipoInvestigadorListado.setMemorias(list.map((memoria) => memoria.value));
    });

    this.subscriptions.push(
      this.datosGenerales.solicitantePeticionEvaluacion$.subscribe((value) => {
        this.memoriasListado.solicitantePeticionEvaluacion = value;
      })
    );

  }

  initializeEquiposInvestigador(): void {
    this.equipoInvestigadorListado.initialize();
  }

  initializeMemorias(): void {
    this.memoriasListado.initialize();
  }

  /**
   * Elimina las tareas asociadas al equipo de trabajo.
   *
   * @param equipoTrabajo el equipo de trabajo.
   */
  public eliminarTareasEquipoTrabajo(equipoTrabajo: StatusWrapper<IEquipoTrabajo>): void {
    this.tareas.deleteTareasEquipoTrabajo(equipoTrabajo);
  }

  saveOrUpdate(): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }
    if (this.isEdit()) {
      return from(this.fragmentos.values()).pipe(
        filter((part) => part.hasChanges()),
        concatMap((part) => part.saveOrUpdate().pipe(
          switchMap(() => {
            return of(void 0);
          }),
          tap(() => part.refreshInitialState(true)))
        ),
        takeLast(1),
        switchMap(() => this.updateMemorias(this.datosGenerales.getKey() as number))
      );
    }
    else {
      const part = this.datosGenerales;
      return part.saveOrUpdate().pipe(
        tap(() => part.refreshInitialState(true)),
        switchMap((k) => {
          if (typeof k === 'string' || typeof k === 'number') {
            this.onKeyChange(k);
          }
          return this.saveOrUpdate();
        }),
        takeLast(1)
      );
    }
  }

  private updateMemorias(peticionEvaluacionId: number): Observable<void> {
    return this.peticionEvaluacionService.findMemorias(peticionEvaluacionId).pipe(
      mergeMap(response => {
        return from(response.items).pipe(
          mergeMap(memoria => {
            const fragment = new MemoriaFormularioFragment(
              this.logger,
              this.readonly,
              memoria.id,
              memoria.comite,
              this.formularioService,
              this.bloqueService,
              this.apartadoService,
              this.respuestaService,
              this.peticionEvaluacionService,
              this.vinculacionService,
              this.datosAcademicosService,
              this.personaService,
              this.memoriaService,
              this.evaluacionService
            );
            fragment.initialize();
            return fragment.initialized$.pipe(filter((value) => value), take(1), map((v) => fragment));
          }),
          filter(value => value.isEditable() && !value.isReadonly()),
          switchMap(fragment => {
            return from(fragment.blocks$.value).pipe(
              // Se excluyen los bloques que no hayan sido persistidos
              filter(block => fragment.blocks$.value.indexOf(block) <= fragment.getLastFilledBlockIndex()),
              concatMap(block => {
                fragment.selectedIndex$.next(fragment.blocks$.value.indexOf(block));
                return block.loaded$.pipe(filter((value) => value), take(1), map(() => block.formlyData));
              }),
              map(formlyData => {
                this.formlyFormBuilder.buildForm(formlyData.formGroup, formlyData.fields, formlyData.model, formlyData.options);
                return fragment;
              }),
              takeLast(1)
            );
          }),
          switchMap((fragment) => fragment.saveOrUpdate()),
        );
      }),
      takeLast(1)
    );
  }
}

