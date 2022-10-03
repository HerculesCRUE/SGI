import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaRequisitoEquipoPublicService } from '@core/services/csp/convocatoria-requisito-equipo-public.service';
import { CategoriaProfesionalPublicService } from '@core/services/sgp/categoria-profesional-public.service';
import { NivelAcademicoPublicService } from '@core/services/sgp/nivel-academico-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, of, zip } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

export class ConvocatoriaRequisitosEquipoPublicFragment extends FormFragment<IConvocatoriaRequisitoEquipo> {

  private requisitoEquipo: IConvocatoriaRequisitoEquipo;

  nivelesAcademicos$ = new BehaviorSubject<StatusWrapper<IRequisitoEquipoNivelAcademico>[]>([]);
  categoriasProfesionales$ = new BehaviorSubject<StatusWrapper<IRequisitoEquipoCategoriaProfesional>[]>([]);
  nivelesAcademicosEliminados: IRequisitoEquipoNivelAcademico[] = [];
  categoriasProfesionalesEliminadas: IRequisitoEquipoCategoriaProfesional[] = [];

  constructor(
    private fb: FormBuilder,
    key: number,
    private convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoPublicService,
    private nivelAcademicoService: NivelAcademicoPublicService,
    private categoriaProfesionalService: CategoriaProfesionalPublicService
  ) {
    super(key, true);
    this.setComplete(true);
    this.requisitoEquipo = {} as IConvocatoriaRequisitoEquipo;
  }

  private loadNivelesAcademicos(requisitoIPId: number): void {
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

  private loadCategoriasProfesionales(requisitoIPId: number): void {
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
    });

    form.disable();

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
    throw new Error('Method not implemented.');
  }

}
