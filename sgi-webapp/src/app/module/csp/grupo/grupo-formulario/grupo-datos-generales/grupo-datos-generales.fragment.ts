import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { IGrupoEspecialInvestigacion } from '@core/models/csp/grupo-especial-investigacion';
import { IGrupoPalabraClave } from '@core/models/csp/grupo-palabra-clave';
import { IGrupoTipo } from '@core/models/csp/grupo-tipo';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { TipoEntidadSGI } from '@core/models/sge/relacion-eliminada';
import { IPersona } from '@core/models/sgp/persona';
import { FormFragment } from '@core/services/action-service';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { GrupoEquipoService } from '@core/services/csp/grupo-equipo/grupo-equipo.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateValidator } from '@core/validators/date-validator';
import { I18nValidators } from '@core/validators/i18n-validator';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, forkJoin, from, Observable, of } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, tap } from 'rxjs/operators';
import { GrupoValidator } from '../../validators/grupo-validator';
import { IGrupoEquipoListado } from '../grupo-equipo-investigacion/grupo-equipo-investigacion.fragment';

export class GrupoDatosGeneralesFragment extends FormFragment<IGrupo> {

  private grupo: IGrupo;
  readonly tipos$ = new BehaviorSubject<IGrupoTipo[]>([]);
  readonly especialesInvestigacion$ = new BehaviorSubject<IGrupoEspecialInvestigacion[]>([]);
  equipoInvestigacion$ = new BehaviorSubject<IGrupoEquipoListado[]>([]);
  proyectosSge$ = new BehaviorSubject<StatusWrapper<IProyectoSge>[]>([]);

  private proyectosSgeEliminados: IProyectoSge[] = [];
  private _disableAddIdentificadorSge$ = new BehaviorSubject<boolean>(false);
  private _isEliminarRelacionProyectoSgeEnabled = false;
  private _isModificacionProyectoSgeEnabled = false;

  get showProyectoSge(): boolean {
    return this.isEjecucionEconomicaGruposEnabled ?? false;
  }

  get disableAddIdentificadorSge$(): Observable<boolean> {
    return this._disableAddIdentificadorSge$;
  }

  get isEliminarRelacionProyectoSgeEnabled(): boolean {
    return !this.readonly && this._isEliminarRelacionProyectoSgeEnabled;
  }

  get isModificacionProyectoSgeEnabled(): boolean {
    return this._isModificacionProyectoSgeEnabled;
  }

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly configService: ConfigService,
    private readonly grupoService: GrupoService,
    private readonly grupoEquipoService: GrupoEquipoService,
    private readonly palabraClaveService: PalabraClaveService,
    private readonly proyectoSgeService: ProyectoSgeService,
    private readonly rolProyectoService: RolProyectoService,
    private readonly vinculacionService: VinculacionService,
    private readonly isEjecucionEconomicaGruposEnabled: boolean,
    public readonly readonly: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.grupo = !key ? {} as IGrupo : { id: key } as IGrupo;
    this.initAddIdentificadorSgeDisableSubscription();
  }

  private initAddIdentificadorSgeDisableSubscription(): void {
    this.subscriptions.push(
      this.proyectosSge$.subscribe(proyectosSge => {
        this._disableAddIdentificadorSge$.next((proyectosSge?.length ?? 0) > 0);
      })
    )
  }

  protected initializer(key: string | number): Observable<IGrupo> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('fechaInicio', SgiRestSortDirection.DESC)
    };

    return this.grupoService.findById(key as number).pipe(
      switchMap(grupo =>
        forkJoin({
          especialesInvestigacion: this.grupoService.findEspecialesInvestigacion(grupo.id, findOptions),
          grupoTipos: this.grupoService.findTipos(grupo.id, findOptions),
          isEliminarRelacionProyectoSgeEnabled: this.configService.isSgeEliminarRelacionProyectoEnabled(),
          isModificacionProyectoSgeEnabled: this.configService.isModificacionProyectoSgeEnabled(),
          palabrasClave: this.grupoService.findPalabrasClave(grupo.id),
          solicitud: !!grupo.solicitud ? this.grupoService.findSolicitud(grupo.id) : of(null),
        }).pipe(
          tap(({
            especialesInvestigacion,
            grupoTipos,
            isEliminarRelacionProyectoSgeEnabled,
            isModificacionProyectoSgeEnabled,
            palabrasClave,
          }) => {
            this._isEliminarRelacionProyectoSgeEnabled = isEliminarRelacionProyectoSgeEnabled;
            this._isModificacionProyectoSgeEnabled = isModificacionProyectoSgeEnabled;
            this.getFormGroup().controls.palabrasClave.setValue(palabrasClave.items.map(grupoPalabraClave => grupoPalabraClave.palabraClave));
            this.especialesInvestigacion$.next(especialesInvestigacion.items);
            this.tipos$.next(grupoTipos.items);
          }),
          map(({ solicitud }) => {
            grupo.solicitud = solicitud;
            return grupo;
          }),
          catchError((error) => {
            this.logger.error(error);
            return EMPTY;
          })
        )
      )
    );
  }

  protected buildFormGroup(): FormGroup {
    return this.isEdit() ? this.buildFormGroupEdit() : this.buildFormGroupCreate();
  }

  buildPatch(grupo: IGrupo): { [key: string]: any } {
    this.grupo = grupo;
    let formValues: { [key: string]: any } = {
      nombre: grupo.nombre,
      codigo: grupo.codigo,
      fechaInicio: grupo.fechaInicio,
      fechaFin: grupo.fechaFin,
      tipo: grupo.tipo,
      especialInvestigacion: grupo.especialInvestigacion ?? false,
      resumen: grupo.resumen ?? []
    };

    if (grupo.solicitud) {
      formValues = {
        ...formValues,
        solicitud: grupo.solicitud.codigoRegistroInterno
      };
    }

    if (grupo.proyectoSge) {
      this.proyectosSge$.next([new StatusWrapper<IProyectoSge>(grupo.proyectoSge)]);
    }

    return formValues;
  }

  getValue(): IGrupo {
    const form = this.getFormGroup().controls;
    this.grupo.nombre = form.nombre.value;
    this.grupo.codigo = form.codigo.value;
    this.grupo.proyectoSge = this.proyectosSge$?.value[0]?.value;
    this.grupo.fechaInicio = form.fechaInicio.value;
    this.grupo.fechaFin = form.fechaFin.value;
    this.grupo.tipo = form.tipo.value;
    this.grupo.especialInvestigacion = form.especialInvestigacion.value;
    this.grupo.resumen = form.resumen.value;

    return this.grupo;
  }

  saveOrUpdate(): Observable<number> {
    const grupo = this.getValue();
    const observable$ = this.isEdit() ? this.update(grupo) :
      this.create(grupo);
    return observable$.pipe(
      map(value => {
        this.grupo.id = value.id;
        if (this.isEdit()) {
          this.actualizarTablas(value.id);
        }
        return this.grupo.id;
      })
    );
  }

  public addProyectoSge(proyectoSge: IProyectoSge): void {
    const wrapped = new StatusWrapper<IProyectoSge>(proyectoSge);
    wrapped.setCreated();
    const current = this.proyectosSge$.value;
    current.push(wrapped);
    this.proyectosSge$.next(current);
    this.setChanges(true);
  }

  public deleteRelacionProyectoSge(proyectoSgeWrapper: StatusWrapper<IProyectoSge>): void {
    const current = this.proyectosSge$.value;
    const index = current.findIndex(
      (value) => value === proyectoSgeWrapper
    );
    if (index >= 0) {
      if (!proyectoSgeWrapper.created) {
        this.proyectosSgeEliminados.push(current[index]?.value);
      }
      current.splice(index, 1);
      this.proyectosSge$.next(current);
      this.setChanges(true);
    }
  }

  private buildFormGroupCreate(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl([], [I18nValidators.required, I18nValidators.maxLength(250)]),
      investigadorPrincipal: new FormControl(null, Validators.required),
      departamento: new FormControl({ value: null, disabled: true }),
      codigo: new FormControl(null, {
        validators: Validators.required,
        asyncValidators: GrupoValidator.duplicatedCodigo(this.grupoService, this.grupo.id),
      }),
      fechaInicio: new FormControl(null, Validators.required),
      fechaFin: new FormControl(null),
      palabrasClave: new FormControl(null),
      tipo: new FormControl(null),
      especialInvestigacion: new FormControl(false, Validators.required),
      resumen: new FormControl([], I18nValidators.maxLength(4000))
    }, {
      validators: [
        DateValidator.isAfter('fechaInicio', 'fechaFin', false)
      ]
    });

    this.loadDepartamentoAndCodigoOnInvestigadorPrincipalChange(formGroup);

    return formGroup;
  }

  private buildFormGroupEdit(): FormGroup {
    const form = new FormGroup({
      nombre: new FormControl([], [I18nValidators.required, I18nValidators.maxLength(250)]),
      codigo: new FormControl(null, {
        validators: Validators.required,
        asyncValidators: GrupoValidator.duplicatedCodigo(this.grupoService, this.grupo.id),
      }),
      proyectoSge: new FormControl(null),
      fechaInicio: new FormControl(null, Validators.required),
      fechaFin: new FormControl(null),
      palabrasClave: new FormControl(null),
      tipo: new FormControl(null),
      especialInvestigacion: new FormControl({ value: null, disabled: true }),
      solicitud: new FormControl(null),
      resumen: new FormControl([], I18nValidators.maxLength(4000))
    }, {
      validators: [
        DateValidator.isAfter('fechaInicio', 'fechaFin', false)
      ]
    });

    if (this.readonly) {
      form.disable();
    }

    this.subscriptions.push(
      this.equipoInvestigacion$.subscribe(equipoInvestigacion => {
        const fechaInicioControl = form.get('fechaInicio');
        const fechaFinControl = form.get('fechaFin');
        fechaInicioControl.setValidators([
          Validators.required,
          GrupoValidator.fechaInicioConflictsEquipoInvestigacion(equipoInvestigacion)
        ]
        );
        fechaFinControl.setValidators(
          GrupoValidator.fechaFinConflictsEquipoInvestigacion(equipoInvestigacion)
        );
        fechaInicioControl.updateValueAndValidity();
        fechaFinControl.updateValueAndValidity();
      }
      )
    );

    return form;
  }

  private loadDepartamentoAndCodigoOnInvestigadorPrincipalChange(formGroup: FormGroup): void {
    this.subscriptions.push(
      formGroup.controls.investigadorPrincipal.valueChanges.pipe(
        filter(value => !!value?.id),
        switchMap(value =>
          this.vinculacionService.findByPersonaId(value.id).pipe(
            switchMap(vinculacion => {
              if (!vinculacion || !vinculacion?.departamento) {
                formGroup.controls.codigo.setValue(null, { emitEvent: false });
                return of(null);
              }

              return this.grupoService.getNextCodigo(vinculacion.departamento.id).pipe(
                tap(codigo => formGroup.controls.codigo.setValue(codigo, { emitEvent: false })),
                map(() => vinculacion.departamento)
              );
            })
          )
        )
      ).subscribe(departamento => {
        formGroup.controls.departamento.setValue(departamento?.nombre, { emitEvent: false });
        this.grupo.departamentoOrigenRef = departamento?.id;
      })
    );
  }

  private create(grupo: IGrupo): Observable<IGrupo> {
    let cascade = this.grupoService.create(grupo).pipe(
      tap(result => this.grupo.id = result.id),
      switchMap(grupoCreated => this.saveInvestigadorPrincipal(grupoCreated))
    );

    if (this.getFormGroup().controls.palabrasClave.dirty) {
      cascade = cascade.pipe(
        mergeMap(createdGrupo => this.saveOrUpdatePalabrasClave(createdGrupo))
      );
    }

    return cascade;
  }

  private notificarRelacionesEliminadas(proyectosSgeEliminados: IProyectoSge[]): Observable<void> {
    if (this.proyectosSgeEliminados.length === 0) {
      return of(null);
    }

    return from(proyectosSgeEliminados).pipe(
      switchMap(proyectoSge =>
        this.proyectoSgeService.notificarRelacionesEliminadas(
          proyectoSge.id,
          [
            {
              entidadSGIId: this.grupo.id.toString(),
              tipoEntidadSGI: TipoEntidadSGI.GRUPO
            }
          ]
        )
      )
    );
  }

  private update(grupo: IGrupo): Observable<IGrupo> {
    let cascade = of(null);
    if (this.proyectosSgeEliminados?.length) {
      cascade = this.notificarRelacionesEliminadas(this.proyectosSgeEliminados);
    }

    cascade = cascade = cascade.pipe(
      switchMap(() => this.grupoService.update(grupo.id, grupo)),
      tap(grupo => this.grupo = grupo)
    );

    if (this.getFormGroup().controls.palabrasClave.dirty) {
      cascade = cascade.pipe(
        mergeMap(updatedGrupo => this.saveOrUpdatePalabrasClave(updatedGrupo))
      );
    }

    return cascade;
  }

  private saveOrUpdatePalabrasClave(grupo: IGrupo): Observable<IGrupo> {
    const palabrasClave = this.getFormGroup().controls.palabrasClave.value ?? [];
    const proyectoPalabrasClave: IGrupoPalabraClave[] = palabrasClave.map(palabraClave => (
      {
        grupo,
        palabraClave
      } as IGrupoPalabraClave)
    );
    return this.palabraClaveService.update(palabrasClave).pipe(
      mergeMap(() => this.grupoService.updatePalabrasClave(grupo.id, proyectoPalabrasClave)),
      map(() => grupo)
    );
  }

  private saveInvestigadorPrincipal(grupo: IGrupo): Observable<IGrupo> {
    const investigadorPrincipal = this.getFormGroup().controls.investigadorPrincipal.value;

    return this.rolProyectoService.findPrincipal().pipe(
      switchMap(rol => this.grupoEquipoService.create(this.fillInvestigadorPrincipal(grupo, investigadorPrincipal, rol))),
      map(() => grupo)
    );
  }

  private fillInvestigadorPrincipal(grupo: IGrupo, persona: IPersona, rol: IRolProyecto): IGrupoEquipo {
    return {
      id: undefined,
      grupo: { id: grupo.id } as IGrupo,
      persona,
      fechaInicio: grupo.fechaInicio,
      fechaFin: null,
      rol,
      dedicacion: null,
      participacion: null
    };
  }

  private actualizarTablas(id: number) {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('fechaInicio', SgiRestSortDirection.DESC)
    };
    this.subscriptions.push(this.grupoService.findEspecialesInvestigacion(id, findOptions).subscribe(especialesInvestigacionResponse => {
      this.especialesInvestigacion$.next(especialesInvestigacionResponse.items);
    }));

    this.subscriptions.push(this.grupoService.findTipos(id, findOptions).subscribe(tiposResponse => {
      this.tipos$.next(tiposResponse.items);
    }));
  }

}
