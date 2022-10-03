import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaRequisitoIPPublicService } from '@core/services/csp/convocatoria-requisito-ip-public.service';
import { CategoriaProfesionalPublicService } from '@core/services/sgp/categoria-profesional-public.service';
import { NivelAcademicoPublicService } from '@core/services/sgp/nivel-academico-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, of, zip } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';


export interface RequisitoIPNivelAcademicoPublicListado extends IRequisitoIPNivelAcademico {
  eliminable: boolean;
}

export interface RequisitoIPCategoriaProfesionalPublicListado extends IRequisitoIPCategoriaProfesional {
  eliminable: boolean;
}

export class ConvocatoriaRequisitosIPPublicFragment extends FormFragment<IConvocatoriaRequisitoIP> {
  private requisitoIP: IConvocatoriaRequisitoIP;
  nivelesAcademicos$ = new BehaviorSubject<StatusWrapper<RequisitoIPNivelAcademicoPublicListado>[]>([]);
  categoriasProfesionales$ = new BehaviorSubject<StatusWrapper<RequisitoIPCategoriaProfesionalPublicListado>[]>([]);
  nivelesAcademicosEliminados: RequisitoIPNivelAcademicoPublicListado[] = [];
  categoriasProfesionalesEliminadas: RequisitoIPCategoriaProfesionalPublicListado[] = [];

  constructor(
    private fb: FormBuilder,
    key: number,
    private convocatoriaRequisitoIPService: ConvocatoriaRequisitoIPPublicService,
    private nivelAcademicoService: NivelAcademicoPublicService,
    private categoriaProfesionalService: CategoriaProfesionalPublicService
  ) {
    super(key, true);
    this.setComplete(true);
    this.requisitoIP = {} as IConvocatoriaRequisitoIP;
  }

  private loadNivelesAcademicos(requisitoIPId: number): void {
    const subscription = this.convocatoriaRequisitoIPService.findNivelesAcademicos(requisitoIPId).pipe(
      switchMap((requisitoIpNivelesAcademicos) => {
        if (requisitoIpNivelesAcademicos.length === 0) {
          return of([]);
        }
        const nivelesAcademicosObservable = requisitoIpNivelesAcademicos.
          map(requisitoIpNivelAcademico => {

            return this.nivelAcademicoService.findById(requisitoIpNivelAcademico.nivelAcademico.id).pipe(
              map(nivelAcademico => {
                const reqNivAcademico: RequisitoIPNivelAcademicoPublicListado = {
                  id: requisitoIpNivelAcademico.id,
                  nivelAcademico,
                  requisitoIP: {
                    id: requisitoIPId
                  } as IConvocatoriaRequisitoIP,
                  eliminable: false
                };
                return new StatusWrapper<RequisitoIPNivelAcademicoPublicListado>(reqNivAcademico);
              })
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
    const subscription = this.convocatoriaRequisitoIPService.findCategoriaProfesional(requisitoIPId).pipe(
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
                  requisitoIP: {
                    id: requisitoIPId
                  } as IConvocatoriaRequisitoIP,
                  eliminable: false
                } as RequisitoIPCategoriaProfesionalPublicListado;
                return new StatusWrapper<RequisitoIPCategoriaProfesionalPublicListado>(reqCategoriaProfesional);
              })
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
      numMaximoIP: [null, Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      fechaMaximaNivelAcademico: [null],
      fechaMinimaNivelAcademico: [null],
      edadMaxima: ['', Validators.compose(
        [Validators.min(0), Validators.max(99)])],
      sexo: [null],
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

  protected initializer(key: number): Observable<IConvocatoriaRequisitoIP> {
    if (this.getKey()) {
      return this.convocatoriaRequisitoIPService.getRequisitoIPConvocatoria(key).pipe(
        tap(() => this.loadNivelesAcademicos(key)),
        tap(() => this.loadCategoriasProfesionales(key))
      );
    }
  }

  buildPatch(value: IConvocatoriaRequisitoIP): { [key: string]: any } {
    this.requisitoIP = value;
    return {
      numMaximoIP: value ? value.numMaximoIP : null,
      fechaMaximaNivelAcademico: value ? value.fechaMaximaNivelAcademico : null,
      fechaMinimaNivelAcademico: value ? value.fechaMinimaNivelAcademico : null,
      edadMaxima: value ? value.edadMaxima : null,
      sexo: value ? value.sexo : null,
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

  getValue(): IConvocatoriaRequisitoIP {
    if (this.requisitoIP === null) {
      this.requisitoIP = {} as IConvocatoriaRequisitoIP;
    }
    const form = this.getFormGroup().getRawValue();
    this.requisitoIP.numMaximoIP = form.numMaximoIP;
    this.requisitoIP.fechaMaximaNivelAcademico = form.fechaMaximaNivelAcademico;
    this.requisitoIP.fechaMinimaNivelAcademico = form.fechaMinimaNivelAcademico;
    this.requisitoIP.edadMaxima = form.edadMaxima;
    this.requisitoIP.sexo = form.sexo;
    this.requisitoIP.vinculacionUniversidad = form.vinculacionUniversidad;
    this.requisitoIP.fechaMaximaCategoriaProfesional = form.fechaMaximaCategoriaProfesional;
    this.requisitoIP.fechaMinimaCategoriaProfesional = form.fechaMinimaCategoriaProfesional;
    this.requisitoIP.numMinimoCompetitivos = form.numMinimoCompetitivos;
    this.requisitoIP.numMinimoNoCompetitivos = form.numMinimoNoCompetitivos;
    this.requisitoIP.numMaximoCompetitivosActivos = form.numMaximoCompetitivosActivos;
    this.requisitoIP.numMaximoNoCompetitivosActivos = form.numMaximoNoCompetitivosActivos;
    this.requisitoIP.otrosRequisitos = form.otrosRequisitos;
    return this.requisitoIP;
  }

  saveOrUpdate(): Observable<number> {
    throw new Error('Method not implemented.');
  }

}
