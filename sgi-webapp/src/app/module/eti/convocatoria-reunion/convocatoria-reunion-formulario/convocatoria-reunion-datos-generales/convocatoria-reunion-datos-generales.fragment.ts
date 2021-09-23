import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { IAsistente } from '@core/models/eti/asistente';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IConvocatoriaReunionDatosGenerales } from '@core/models/eti/convocatoria-reunion-datos-generales';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IPersona } from '@core/models/sgp/persona';
import { FormFragment } from '@core/services/action-service';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { DateValidator } from '@core/validators/date-validator';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';

export class ConvocatoriaReunionDatosGeneralesFragment extends FormFragment<IConvocatoriaReunionDatosGenerales> {
  private convocatoriaReunion: IConvocatoriaReunionDatosGenerales;
  evaluadoresComite: IEvaluador[] = [];
  asistentes: IAsistente[] = [];

  constructor(
    private readonly logger: NGXLogger,
    private fb: FormBuilder,
    key: number,
    private convocatoriaReunionService: ConvocatoriaReunionService,
    private asistenteService: AsistenteService,
    private personaService: PersonaService,
    private evaluadorService: EvaluadorService,
    private readonly: boolean
  ) {
    super(key);
    this.convocatoriaReunion = {} as IConvocatoriaReunionDatosGenerales;
    this.convocatoriaReunion.activo = true;
  }

  protected buildFormGroup(): FormGroup {
    const fb = this.fb.group({
      comite: ['', new NullIdValidador().isValid()],
      fechaEvaluacion: [null, Validators.required],
      fechaLimite: [null, Validators.required],
      tipoConvocatoriaReunion: ['', new NullIdValidador().isValid()],
      horaInicio: [null, Validators.required],
      lugar: ['', Validators.required],
      ordenDia: ['', Validators.required],
      convocantes: ['', Validators.required],
    },
      {
        validators: [
          DateValidator.isAfter('fechaLimite', 'fechaEvaluacion')]
      });

    // En control del código solo aparece al editar
    if (this.isEdit()) {
      fb.addControl('codigo', new FormControl({ value: '', disabled: true }));
    }

    if (this.readonly) {
      fb.disable();
    }

    return fb;
  }

  protected initializer(key: number): Observable<IConvocatoriaReunionDatosGenerales> {
    return this.convocatoriaReunionService.findByIdWithDatosGenerales(key).pipe(
      switchMap((value) => {
        this.convocatoriaReunion = value;
        return this.loadConvocantes();
      }),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  /**
   * Carga los convocantes de la convocatoria
   */
  loadConvocantes(): Observable<IConvocatoriaReunionDatosGenerales> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, this.convocatoriaReunion.comite.id.toString())
    };
    return this.evaluadorService.findAll(options).pipe(
      switchMap((listadoConvocantes) => {
        const personaIds = new Set<string>(listadoConvocantes.items.map((convocante) => convocante.persona.id));
        return this.personaService.findAllByIdIn([...personaIds]).pipe(
          map((personas) => this.loadDatosPersona(personas, listadoConvocantes.items))
        );
      }),
      switchMap((evaluadores) => {
        this.evaluadoresComite = evaluadores;
        return this.loadAsistentes();
      }),
      switchMap(() => {
        return of(this.convocatoriaReunion);
      })
    );
  }

  /**
   * Carga los datos personales de los evaluadores
   *
   * @param listado Listado de personas
   * @param evaluadores Evaluadores
   */
  private loadDatosPersona(listado: SgiRestListResult<IPersona>, evaluadores: IEvaluador[]): IEvaluador[] {
    const personas = listado.items;
    evaluadores.forEach((convocante) => {
      const datosPersonaConvocante = personas.find((persona) => convocante.persona.id === persona.id);
      convocante.persona = datosPersonaConvocante;
    });
    return evaluadores;
  }

  /**
   * Carga los asistentes que asistieron a la convocatoria dentro del formGroup
   */
  private loadAsistentes(): Observable<SgiRestListResult<IAsistente>> {
    return this.convocatoriaReunionService.findAsistentes(this.convocatoriaReunion.id).pipe(
      switchMap((asistentes) => {
        this.asistentes = asistentes.items;
        const asistentesFormGroup = [];
        const ids = asistentes.items.filter(asistente => asistente.asistencia).map(
          (convocante) => convocante.evaluador.id);
        this.evaluadoresComite.forEach((evaluador) => {
          if (ids.includes(evaluador.id)) {
            asistentesFormGroup.push(evaluador);
          }
        });
        this.getFormGroup().get('convocantes').setValue(asistentesFormGroup);
        return of(asistentes);
      })
    );
  }

  private getDate(hours: number, minutes: number): Date {
    const date = new Date();
    date.setHours(hours);
    date.setMinutes(minutes);
    date.setSeconds(0);
    date.setMilliseconds(0);
    return date;
  }

  buildPatch(value: IConvocatoriaReunionDatosGenerales): { [key: string]: any } {
    const result = {
      codigo: value.codigo,
      comite: value.comite,
      fechaEvaluacion: value.fechaEvaluacion,
      fechaLimite: value.fechaLimite?.minus({ hours: 23, minutes: 59, seconds: 59 }),
      tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
      horaInicio: this.getDate(value.horaInicio, value.minutoInicio),
      lugar: value.lugar,
      ordenDia: value.ordenDia,
    };

    if (value.idActa) {
      // Si tiene Acta solo se podrá modificar lugar y orden del día
      this.getFormGroup().controls.comite.disable({ onlySelf: true });
      this.getFormGroup().controls.fechaEvaluacion.disable({ onlySelf: true });
      this.getFormGroup().controls.fechaLimite.disable({ onlySelf: true });
      this.getFormGroup().controls.tipoConvocatoriaReunion.disable({ onlySelf: true });
      this.getFormGroup().controls.horaInicio.disable({ onlySelf: true });
      this.getFormGroup().controls.convocantes.disable({ onlySelf: true });
    } else {
      // Para que en la carga inicial no se permita editar si hay evaluaciones asignadas.
      if (value.numEvaluaciones > 0) {
        this.getFormGroup().controls.comite.disable({ onlySelf: true });
        this.getFormGroup().controls.fechaLimite.disable({ onlySelf: true });
        this.getFormGroup().controls.tipoConvocatoriaReunion.disable({ onlySelf: true });
      } else if (value.fechaEnvio) {
        // Si ya se ha enviado no se podrá modificar el comité
        this.getFormGroup().controls.comite.disable({ onlySelf: true });
      }
    }

    if (value.id) {
      this.getFormGroup().controls.comite.disable();
    }

    return result;
  }

  /**
   * True / False si la convocatoria tiene Acta
   */
  hasActa(): boolean {
    return (this.convocatoriaReunion && this.convocatoriaReunion.idActa) ? true : false;
  }

  getValue(): IConvocatoriaReunionDatosGenerales {
    const form = this.getFormGroup();
    this.convocatoriaReunion.comite = (this.getFormGroup().controls.comite.disabled) ?
      this.getFormGroup().controls.comite.value : form.controls.comite.value;
    this.convocatoriaReunion.fechaEvaluacion = form.controls.fechaEvaluacion.value;
    this.convocatoriaReunion.fechaLimite = (this.getFormGroup().controls.fechaLimite.disabled) ?
      this.getFormGroup().controls.fechaLimite.value.plus({ hours: 23, minutes: 59, seconds: 59 }) :
      form.controls.fechaLimite.value.plus({ hours: 23, minutes: 59, seconds: 59 });
    this.convocatoriaReunion.tipoConvocatoriaReunion = (this.getFormGroup().controls.tipoConvocatoriaReunion.disabled) ?
      this.getFormGroup().controls.tipoConvocatoriaReunion.value : form.controls.tipoConvocatoriaReunion.value;
    this.convocatoriaReunion.horaInicio = form.controls.horaInicio.value?.getHours();
    this.convocatoriaReunion.minutoInicio = form.controls.horaInicio.value?.getMinutes();
    this.convocatoriaReunion.lugar = form.controls.lugar.value;
    this.convocatoriaReunion.ordenDia = form.controls.ordenDia.value;
    return this.convocatoriaReunion;
  }

  saveOrUpdate(): Observable<number> {
    const datosGenerales = this.getValue();
    const obs$ = this.isEdit() ? this.update(datosGenerales) : this.create(datosGenerales);
    return obs$.pipe(
      map((value) => {
        this.convocatoriaReunion = Object.assign(this.convocatoriaReunion, value);
        return value.id;
      })
    );
  }

  private create(datosGenerales: IConvocatoriaReunion): Observable<IConvocatoriaReunion> {
    return this.convocatoriaReunionService.create(datosGenerales).pipe(
      switchMap((convocatoriaReunion) => {
        this.setKey(convocatoriaReunion.id);
        return this.saveAsistentes(convocatoriaReunion, this.evaluadoresComite);
      })
    );
  }

  private saveAsistentes(convocatoriaReunion: IConvocatoriaReunion, evaluadores: IEvaluador[]): Observable<IConvocatoriaReunion> {
    const asistentes = evaluadores.map((evaluador) => {
      const asistencia: IAsistente = {
        asistencia: this.getFormGroup().controls.convocantes.value.includes(evaluador),
        convocatoriaReunion,
        evaluador,
        id: undefined,
        motivo: undefined
      };
      return asistencia;
    });
    return from(asistentes).pipe(
      mergeMap((asistente) => {
        return this.asistenteService.create(asistente);
      }),
      takeLast(1),
      map(() => convocatoriaReunion)
    );
  }

  private update(datosGenerales: IConvocatoriaReunion): Observable<IConvocatoriaReunion> {
    return this.convocatoriaReunionService.update(datosGenerales.id, datosGenerales).pipe(
      switchMap((convocatoriaReunion) => {
        return this.updateAsistentes(convocatoriaReunion);
      })
    );
  }

  private updateAsistentes(convocatoriaReunion: IConvocatoriaReunion): Observable<IConvocatoriaReunion> {
    const evaluadores: IEvaluador[] = this.getFormGroup().controls.convocantes.value;
    this.asistentes.forEach(asistente => {
      asistente.asistencia = false;
      evaluadores.forEach(evaluador => {
        if (asistente.evaluador.id === evaluador.id) {
          asistente.asistencia = true;
        }
      });
    });

    return from(this.asistentes).pipe(
      mergeMap((asistente) => {
        return this.asistenteService.update(asistente.id, asistente);
      }),
      takeLast(1),
      map(() => convocatoriaReunion)
    );
  }
}
