import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IGrupo } from '@core/models/csp/grupo';
import { Dedicacion, IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { IGrupoPalabraClave } from '@core/models/csp/grupo-palabra-clave';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { IPersona } from '@core/models/sgp/persona';
import { FormFragment } from '@core/services/action-service';
import { GrupoEquipoService } from '@core/services/csp/grupo-equipo/grupo-equipo.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { DateValidator } from '@core/validators/date-validator';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable, of } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, tap } from 'rxjs/operators';
import { GrupoValidator } from '../../validators/grupo-validator';

export class GrupoDatosGeneralesFragment extends FormFragment<IGrupo> {

  private grupo: IGrupo;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly grupoService: GrupoService,
    private readonly grupoEquipoService: GrupoEquipoService,
    private readonly palabraClaveService: PalabraClaveService,
    private readonly rolProyectoService: RolProyectoService,
    private readonly vinculacionService: VinculacionService
  ) {
    super(key, true);
    this.setComplete(true);
    this.grupo = !key ? {} as IGrupo : { id: key } as IGrupo;
  }

  protected initializer(key: string | number): Observable<IGrupo> {
    return this.grupoService.findById(key as number).pipe(
      switchMap(grupo =>
        this.grupoService.findPalabrasClave(grupo.id).pipe(
          map(({ items }) => items.map(grupoPalabraClave => grupoPalabraClave.palabraClave)),
          tap(palabrasClave => this.getFormGroup().controls.palabrasClave.setValue(palabrasClave)),
          map(() => grupo)
        )
      ),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  protected buildFormGroup(): FormGroup {
    return this.isEdit() ? this.buildFormGroupEdit() : this.buildFormGroupCreate();
  }

  buildPatch(grupo: IGrupo): { [key: string]: any } {
    this.grupo = grupo;
    return {
      nombre: grupo.nombre,
      codigo: grupo.codigo,
      proyectoSge: grupo.proyectoSge,
      fechaInicio: grupo.fechaInicio,
      fechaFin: grupo.fechaFin,
      tipo: grupo.tipo,
      especialInvestigacion: grupo.especialInvestigacion
    };
  }

  getValue(): IGrupo {
    const form = this.getFormGroup().controls;
    this.grupo.nombre = form.nombre.value;
    this.grupo.codigo = form.codigo.value;
    this.grupo.proyectoSge = form.proyectoSge?.value;
    this.grupo.fechaInicio = form.fechaInicio.value;
    this.grupo.fechaFin = form.fechaFin.value;
    this.grupo.tipo = form.tipo.value;
    this.grupo.especialInvestigacion = form.especialInvestigacion.value;

    return this.grupo;
  }

  saveOrUpdate(): Observable<number> {
    const grupo = this.getValue();
    const observable$ = this.isEdit() ? this.update(grupo) :
      this.create(grupo);
    return observable$.pipe(
      map(value => {
        this.grupo.id = value.id;
        return this.grupo.id;
      })
    );
  }

  private buildFormGroupCreate(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(null, Validators.required),
      investigadorPrincipal: new FormControl(null, Validators.required),
      departamento: new FormControl({ value: null, disabled: true }),
      codigo: new FormControl(null, {
        validators: Validators.required,
        asyncValidators: GrupoValidator.duplicatedCodigo(this.grupoService, this.grupo.id),
      }),
      proyectoSge: new FormControl(null),
      fechaInicio: new FormControl(null, Validators.required),
      fechaFin: new FormControl(null),
      palabrasClave: new FormControl(null),
      tipo: new FormControl(null),
      especialInvestigacion: new FormControl(false)
    }, {
      validators: [
        DateValidator.isAfter('fechaInicio', 'fechaFin', false)
      ]
    });

    this.loadDepartamentoAndCodigoOnInvestigadorPrincipalChange(formGroup);

    return formGroup;
  }

  private buildFormGroupEdit(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(null, Validators.required),
      codigo: new FormControl(null, {
        validators: Validators.required,
        asyncValidators: GrupoValidator.duplicatedCodigo(this.grupoService, this.grupo.id),
      }),
      proyectoSge: new FormControl(null),
      fechaInicio: new FormControl(null, Validators.required),
      fechaFin: new FormControl(null),
      palabrasClave: new FormControl(null),
      tipo: new FormControl(null),
      especialInvestigacion: new FormControl(false)
    }, {
      validators: [
        DateValidator.isAfter('fechaInicio', 'fechaFin', false)
      ]
    });
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
              )
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

  private update(grupo: IGrupo): Observable<IGrupo> {
    let cascade = this.grupoService.update(grupo.id, grupo).pipe(
      tap(result => this.grupo = result)
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
      dedicacion: Dedicacion.COMPLETA,
      participacion: 100
    }
  }

}
