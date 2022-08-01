import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaRequisitoEquipoService } from '@core/services/csp/convocatoria-requisito-equipo.service';
import { CategoriaProfesionalService } from '@core/services/sgp/categoria-profesional.service';
import { NivelAcademicosService } from '@core/services/sgp/nivel-academico.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateValidator } from '@core/validators/date-validator';
import { BehaviorSubject, Observable, of, zip } from 'rxjs';
import { map, switchMap, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaRequisitosEquipoFragment extends FormFragment<IConvocatoriaRequisitoEquipo> {

  private requisitoEquipo: IConvocatoriaRequisitoEquipo;

  nivelesAcademicos$ = new BehaviorSubject<StatusWrapper<IRequisitoEquipoNivelAcademico>[]>([]);
  categoriasProfesionales$ = new BehaviorSubject<StatusWrapper<IRequisitoEquipoCategoriaProfesional>[]>([]);
  nivelesAcademicosEliminados: IRequisitoEquipoNivelAcademico[] = [];
  categoriasProfesionalesEliminadas: IRequisitoEquipoCategoriaProfesional[] = [];

  constructor(
    private fb: FormBuilder,
    key: number,
    private convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoService,
    private nivelAcademicoService: NivelAcademicosService,
    private categoriaProfesionalService: CategoriaProfesionalService,
    public hasEditPerm: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.requisitoEquipo = {} as IConvocatoriaRequisitoEquipo;
  }

  protected loadNivelesAcademicos(requisitoIPId: number): void {
    const subscription = this.convocatoriaRequisitoEquipoService.findNivelesAcademicos(requisitoIPId).pipe(
      switchMap((requisitoIpNivelesAcademicos) => {
        if (requisitoIpNivelesAcademicos.length === 0) {
          return of([]);
        }
        const nivelesAcademicosObservable = requisitoIpNivelesAcademicos.
          map(requisitoIpNivelAcademico => {

            return this.nivelAcademicoService.findById(requisitoIpNivelAcademico.nivelAcademico.id).pipe(
              map(nivelAcademico => {
                const reqNivAcademico = {
                  id: requisitoIpNivelAcademico.id,
                  nivelAcademico,
                  requisitoEquipo: {
                    id: requisitoIPId
                  } as IConvocatoriaRequisitoEquipo
                } as IRequisitoEquipoNivelAcademico;
                return new StatusWrapper<IRequisitoEquipoNivelAcademico>(reqNivAcademico);
              }),
            );
          });

        return zip(...nivelesAcademicosObservable);
      })
    ).subscribe(
      (nivelesAcademicos) => {
        this.nivelesAcademicos$.next(nivelesAcademicos);
      }
    );
    this.subscriptions.push(subscription);
  }

  protected loadCategoriasProfesionales(requisitoIPId: number): void {
    const subscription = this.convocatoriaRequisitoEquipoService.findCategoriaProfesional(requisitoIPId).pipe(
      switchMap((requisitoIpCategoriasProfesionales) => {
        if (requisitoIpCategoriasProfesionales.length === 0) {
          return of([]);
        }
        const categoriasProfesionalesObservable = requisitoIpCategoriasProfesionales.
          map(requisitoIpCategoriaProfesional => {

            return this.categoriaProfesionalService.findById(requisitoIpCategoriaProfesional.categoriaProfesional.id).pipe(
              map(categoriaProfesional => {
                const reqCategoriaProfesional = {
                  id: requisitoIpCategoriaProfesional.id,
                  categoriaProfesional,
                  requisitoEquipo: {
                    id: requisitoIPId
                  } as IConvocatoriaRequisitoEquipo
                } as IRequisitoEquipoCategoriaProfesional;
                return new StatusWrapper<IRequisitoEquipoCategoriaProfesional>(reqCategoriaProfesional);
              }),
            );
          });

        return zip(...categoriasProfesionalesObservable);
      })
    ).subscribe(
      (categoriasProfesionales) => {
        this.categoriasProfesionales$.next(categoriasProfesionales);
      }
    );
    this.subscriptions.push(subscription);
  }


  protected buildFormGroup(): FormGroup {
    const form = this.fb.group({
      fechaMaximaNivelAcademico: [null],
      fechaMinimaNivelAcademico: [null],
      edadMaxima: ['', Validators.compose(
        [Validators.min(0), Validators.max(99)])],
      sexo: [null],
      ratioSexo: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999), Validators.pattern('^[0-9]*$')])],
      vinculacionUniversidad: [null],
      fechaMaximaCategoriaProfesional: [null],
      fechaMinimaCategoriaProfesional: [null],
      numMinimoCompetitivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      numMinimoNoCompetitivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      numMaximoCompetitivosActivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      numMaximoNoCompetitivosActivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      otrosRequisitos: ['']
    }, {
      validators: [
        DateValidator.isAfter('fechaMinimaNivelAcademico', 'fechaMaximaNivelAcademico', false),
        DateValidator.isAfter('fechaMinimaCategoriaProfesional', 'fechaMaximaCategoriaProfesional', false)]
    });
    if (!this.hasEditPerm) {
      form.disable();
    }
    return form;
  }

  protected initializer(key: number): Observable<IConvocatoriaRequisitoEquipo> {
    if (this.getKey()) {
      return this.convocatoriaRequisitoEquipoService.findById(key).pipe(
        tap(() => this.loadNivelesAcademicos(key)),
        tap(() => this.loadCategoriasProfesionales(key))
      );
    }
  }

  buildPatch(value: IConvocatoriaRequisitoEquipo): { [key: string]: any } {
    this.requisitoEquipo = value;
    return {
      fechaMaximaNivelAcademico: value ? value.fechaMaximaNivelAcademico : null,
      fechaMinimaNivelAcademico: value ? value.fechaMinimaNivelAcademico : null,
      edadMaxima: value ? value.edadMaxima : null,
      sexo: value ? value.sexo : null,
      ratioSexo: value ? value.ratioSexo : null,
      vinculacionUniversidad: value ? value.vinculacionUniversidad : null,
      fechaMaximaCategoriaProfesional: value ? value.fechaMaximaCategoriaProfesional : null,
      fechaMinimaCategoriaProfesional: value ? value.fechaMinimaCategoriaProfesional : null,
      numMinimoCompetitivos: value ? value.numMinimoCompetitivos : null,
      numMinimoNoCompetitivos: value ? value.numMinimoNoCompetitivos : null,
      numMaximoCompetitivosActivos: value ? value.numMaximoCompetitivosActivos : null,
      numMaximoNoCompetitivosActivos: value ? value.numMaximoNoCompetitivosActivos : null,
      otrosRequisitos: value ? value.otrosRequisitos : null
    };
  }

  getValue(): IConvocatoriaRequisitoEquipo {
    if (this.requisitoEquipo === null) {
      this.requisitoEquipo = {} as IConvocatoriaRequisitoEquipo;
    }
    const form = this.getFormGroup().getRawValue();
    this.requisitoEquipo.fechaMaximaNivelAcademico = form.fechaMaximaNivelAcademico;
    this.requisitoEquipo.fechaMinimaNivelAcademico = form.fechaMinimaNivelAcademico;
    this.requisitoEquipo.edadMaxima = form.edadMaxima;
    this.requisitoEquipo.sexo = form.sexo;
    this.requisitoEquipo.ratioSexo = form.ratioSexo;
    this.requisitoEquipo.vinculacionUniversidad = form.vinculacionUniversidad;
    this.requisitoEquipo.fechaMaximaCategoriaProfesional = form.fechaMaximaCategoriaProfesional;
    this.requisitoEquipo.fechaMinimaCategoriaProfesional = form.fechaMinimaCategoriaProfesional;
    this.requisitoEquipo.numMinimoCompetitivos = form.numMinimoCompetitivos;
    this.requisitoEquipo.numMinimoNoCompetitivos = form.numMinimoNoCompetitivos;
    this.requisitoEquipo.numMaximoCompetitivosActivos = form.numMaximoCompetitivosActivos;
    this.requisitoEquipo.numMaximoNoCompetitivosActivos = form.numMaximoNoCompetitivosActivos;
    this.requisitoEquipo.otrosRequisitos = form.otrosRequisitos;
    return this.requisitoEquipo;
  }

  saveOrUpdate(): Observable<number> {
    const datosrequisitoEquipo = this.getValue();
    datosrequisitoEquipo.convocatoriaId = this.getKey() as number;
    const obs = datosrequisitoEquipo.id ? this.update(datosrequisitoEquipo) : this.create(datosrequisitoEquipo);
    return obs.pipe(
      tap(result => this.requisitoEquipo = result),
      switchMap((result) => this.saveOrUpdateNivelesAcademicos(result)),
      switchMap((result) => this.saveOrUpdateCategoriasProfesionales(result)),
      map((value) => {
        this.requisitoEquipo = value;
        return this.requisitoEquipo.id;
      })
    );
  }

  public deleteNivelAcademico(wrapper: StatusWrapper<IRequisitoEquipoNivelAcademico>): void {
    const current = this.nivelesAcademicos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.nivelesAcademicosEliminados.push(wrapper.value);
      }
      current.splice(index, 1);
      this.nivelesAcademicos$.next(current);
      this.setChanges(true);
    }
  }

  public deleteCategoriaProfesional(wrapper: StatusWrapper<IRequisitoEquipoCategoriaProfesional>): void {
    const current = this.categoriasProfesionales$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.categoriasProfesionalesEliminadas.push(wrapper.value);
      }
      current.splice(index, 1);
      this.categoriasProfesionales$.next(current);
      this.setChanges(true);
    }
  }

  public addNivelAcademico(nivelAcademico: INivelAcademico) {
    if (nivelAcademico) {
      const requisitoEquipoNivelAcademico = {
        nivelAcademico
      } as IRequisitoEquipoNivelAcademico;
      const wrapped = new StatusWrapper<IRequisitoEquipoNivelAcademico>(requisitoEquipoNivelAcademico);
      wrapped.setCreated();
      const current = this.nivelesAcademicos$.value;
      current.push(wrapped);
      this.nivelesAcademicos$.next(current);

      this.setChanges(true);
    }
  }

  public addCategoriaProfesional(categoriaProfesional: ICategoriaProfesional) {
    if (categoriaProfesional) {
      const requisitoEquipoCategoriaProfesional = {
        categoriaProfesional
      } as IRequisitoEquipoCategoriaProfesional;
      const wrapped = new StatusWrapper<IRequisitoEquipoCategoriaProfesional>(requisitoEquipoCategoriaProfesional);
      wrapped.setCreated();
      const current = this.categoriasProfesionales$.value;
      current.push(wrapped);
      this.categoriasProfesionales$.next(current);

      this.setChanges(true);
    }
  }

  private create(requisitoEquipo: IConvocatoriaRequisitoEquipo): Observable<IConvocatoriaRequisitoEquipo> {
    if (!requisitoEquipo.id) {
      requisitoEquipo.id = this.getKey() as number;
    }
    return this.convocatoriaRequisitoEquipoService.create(requisitoEquipo);
  }

  private update(requisitoEquipo: IConvocatoriaRequisitoEquipo): Observable<IConvocatoriaRequisitoEquipo> {
    return this.convocatoriaRequisitoEquipoService.update(Number(this.getKey()), requisitoEquipo);
  }

  saveOrUpdateNivelesAcademicos(requisitoEquipo: IConvocatoriaRequisitoEquipo): Observable<IConvocatoriaRequisitoEquipo> {
    const hasChanges = this.nivelesAcademicosEliminados.length > 0
      || this.nivelesAcademicos$.value.some(nivelAcademico => nivelAcademico.created);

    if (!hasChanges) {
      return of(requisitoEquipo);
    }

    const values = this.nivelesAcademicos$.value.map(wrapper => {
      wrapper.value.requisitoEquipo = requisitoEquipo;
      return wrapper.value;
    }
    );
    const id = this.getKey() as number;
    return this.convocatoriaRequisitoEquipoService.updateNivelesAcademicos(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.nivelesAcademicosEliminados = [];
          this.nivelesAcademicos$.next(
            results.map(
              (value) => {
                value.nivelAcademico = values.find(
                  nivelAcademico => nivelAcademico.nivelAcademico.id === value.nivelAcademico.id
                ).nivelAcademico;
                return new StatusWrapper<IRequisitoEquipoNivelAcademico>(value);
              })
          );
        }),
        switchMap(() => of(requisitoEquipo))
      );
  }

  saveOrUpdateCategoriasProfesionales(requisitoEquipo: IConvocatoriaRequisitoEquipo): Observable<IConvocatoriaRequisitoEquipo> {
    const hasChanges = this.categoriasProfesionalesEliminadas.length > 0
      || this.categoriasProfesionales$.value.some(nivelAcademico => nivelAcademico.created);

    if (!hasChanges) {
      return of(requisitoEquipo);
    }

    const values = this.categoriasProfesionales$.value.map(wrapper => {
      wrapper.value.requisitoEquipo = requisitoEquipo;
      return wrapper.value;
    }
    );
    const id = this.getKey() as number;
    return this.convocatoriaRequisitoEquipoService.updateCategoriasProfesionales(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.categoriasProfesionalesEliminadas = [];
          this.categoriasProfesionales$.next(
            results.map(
              (value) => {
                value.categoriaProfesional = values.find(
                  categoriaProfesional => categoriaProfesional.categoriaProfesional.id === value.categoriaProfesional.id
                ).categoriaProfesional;
                return new StatusWrapper<IRequisitoEquipoCategoriaProfesional>(value);
              })
          );
        }),
        switchMap(() => of(requisitoEquipo))
      );
  }

}
