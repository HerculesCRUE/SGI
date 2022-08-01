import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { CategoriaProfesionalService } from '@core/services/sgp/categoria-profesional.service';
import { NivelAcademicosService } from '@core/services/sgp/nivel-academico.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateValidator } from '@core/validators/date-validator';
import { BehaviorSubject, Observable, of, zip } from 'rxjs';
import { map, switchMap, takeLast, tap } from 'rxjs/operators';

export interface SolicitudProyectoClasificacionListado {
  id: number;
  proyectoId: number;
  clasificacion: IClasificacion;
  niveles: IClasificacion[];
  nivelesTexto: string;
  nivelSeleccionado: IClasificacion;
}

export interface RequisitoIPNivelAcademicoListado extends IRequisitoIPNivelAcademico {
  eliminable: boolean;
}

export interface RequisitoIPCategoriaProfesionalListado extends IRequisitoIPCategoriaProfesional {
  eliminable: boolean;
}

export class ConvocatoriaRequisitosIPFragment extends FormFragment<IConvocatoriaRequisitoIP> {
  private requisitoIP: IConvocatoriaRequisitoIP;
  nivelesAcademicos$ = new BehaviorSubject<StatusWrapper<RequisitoIPNivelAcademicoListado>[]>([]);
  categoriasProfesionales$ = new BehaviorSubject<StatusWrapper<RequisitoIPCategoriaProfesionalListado>[]>([]);
  nivelesAcademicosEliminados: RequisitoIPNivelAcademicoListado[] = [];
  categoriasProfesionalesEliminadas: RequisitoIPCategoriaProfesionalListado[] = [];

  constructor(
    private fb: FormBuilder,
    key: number,
    private convocatoriaRequisitoIPService: ConvocatoriaRequisitoIPService,
    private nivelAcademicoService: NivelAcademicosService,
    private categoriaProfesionalService: CategoriaProfesionalService,
    public hasEditPerm: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.requisitoIP = {} as IConvocatoriaRequisitoIP;
  }

  protected loadNivelesAcademicos(requisitoIPId: number): void {
    const subscription = this.convocatoriaRequisitoIPService.findNivelesAcademicos(requisitoIPId).pipe(
      switchMap((requisitoIpNivelesAcademicos) => {
        if (requisitoIpNivelesAcademicos.length === 0) {
          return of([]);
        }
        const nivelesAcademicosObservable = requisitoIpNivelesAcademicos.
          map(requisitoIpNivelAcademico => {

            return this.nivelAcademicoService.findById(requisitoIpNivelAcademico.nivelAcademico.id).pipe(
              map(nivelAcademico => {
                const reqNivAcademico: RequisitoIPNivelAcademicoListado = {
                  id: requisitoIpNivelAcademico.id,
                  nivelAcademico,
                  requisitoIP: {
                    id: requisitoIPId
                  } as IConvocatoriaRequisitoIP,
                  eliminable: false
                };
                return new StatusWrapper<RequisitoIPNivelAcademicoListado>(reqNivAcademico);
              }),
              switchMap(requisitoIpNivelAcademicoListado =>
                this.convocatoriaRequisitoIPService.isRequisitoNivelAcademicoEliminable(requisitoIpNivelAcademicoListado.value)
                  .pipe(
                    map(value => {
                      requisitoIpNivelAcademicoListado.value.eliminable = value;
                      return requisitoIpNivelAcademicoListado;
                    })
                  )
              )
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
                } as RequisitoIPCategoriaProfesionalListado;
                return new StatusWrapper<RequisitoIPCategoriaProfesionalListado>(reqCategoriaProfesional);
              }),
              switchMap(categoriaProfesionalListado =>
                this.convocatoriaRequisitoIPService.isRequisitoCategoriaProfesionalEliminable(categoriaProfesionalListado.value)
                  .pipe(
                    map(value => {
                      categoriaProfesionalListado.value.eliminable = value;
                      return categoriaProfesionalListado;
                    })
                  )
              )
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

  public addNivelAcademico(nivelAcademico: INivelAcademico) {
    if (nivelAcademico) {
      const requisitoIPNivelAcademico = {
        nivelAcademico,
        eliminable: true
      } as RequisitoIPNivelAcademicoListado;
      const wrapped = new StatusWrapper<RequisitoIPNivelAcademicoListado>(requisitoIPNivelAcademico);
      wrapped.setCreated();
      const current = this.nivelesAcademicos$.value;
      current.push(wrapped);
      this.nivelesAcademicos$.next(current);

      this.setChanges(true);
    }
  }

  public addCategoriaProfesional(categoriaProfesional: ICategoriaProfesional) {
    if (categoriaProfesional) {
      const requisitoIPCategoriaProfesional = {
        categoriaProfesional,
        eliminable: true
      } as RequisitoIPCategoriaProfesionalListado;
      const wrapped = new StatusWrapper<RequisitoIPCategoriaProfesionalListado>(requisitoIPCategoriaProfesional);
      wrapped.setCreated();
      const current = this.categoriasProfesionales$.value;
      current.push(wrapped);
      this.categoriasProfesionales$.next(current);

      this.setChanges(true);
    }
  }

  public deleteNivelAcademico(wrapper: StatusWrapper<RequisitoIPNivelAcademicoListado>): void {
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

  public deleteCategoriaProfesional(wrapper: StatusWrapper<RequisitoIPCategoriaProfesionalListado>): void {
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

  saveOrUpdate(): Observable<number> {
    const datosRequisitoIP = this.getValue();
    datosRequisitoIP.convocatoriaId = this.getKey() as number;

    const obs = datosRequisitoIP.id ? this.update(datosRequisitoIP) : this.create(datosRequisitoIP);
    return obs.pipe(
      tap(result => this.requisitoIP = result),
      switchMap((result) => this.saveOrUpdateNivelesAcademicos(result)),
      switchMap((result) => this.saveOrUpdateCategoriasProfesionales(result)),
      map((value) => {
        this.requisitoIP = value;
        return this.requisitoIP.id;
      })
    );
  }

  private create(requisitoIP: IConvocatoriaRequisitoIP): Observable<IConvocatoriaRequisitoIP> {
    if (!requisitoIP.id) {
      requisitoIP.id = this.getKey() as number;
    }
    return this.convocatoriaRequisitoIPService.create(requisitoIP);
  }

  private update(requisitoIP: IConvocatoriaRequisitoIP): Observable<IConvocatoriaRequisitoIP> {
    return this.convocatoriaRequisitoIPService.update(Number(this.getKey()), requisitoIP);
  }

  private saveOrUpdateNivelesAcademicos(requisitoIp: IConvocatoriaRequisitoIP): Observable<IConvocatoriaRequisitoIP> {
    const hasChanges = this.nivelesAcademicosEliminados.length > 0
      || this.nivelesAcademicos$.value.some(nivelAcademico => nivelAcademico.created);

    if (!hasChanges) {
      return of(requisitoIp);
    }

    const values = this.nivelesAcademicos$.value.map(wrapper => {
      wrapper.value.requisitoIP = requisitoIp;
      return wrapper.value;
    }
    );
    const id = this.getKey() as number;
    return this.convocatoriaRequisitoIPService.updateNivelesAcademicos(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.nivelesAcademicosEliminados = [];
          this.nivelesAcademicos$.next(
            results.map(
              (value) => {
                const valueListado = values.find(
                  nivelAcademico => nivelAcademico.nivelAcademico.id === value.nivelAcademico.id
                );
                const nivelAcademicoListado: RequisitoIPNivelAcademicoListado = {
                  ...value,
                  nivelAcademico: valueListado?.nivelAcademico,
                  eliminable: valueListado?.eliminable
                };
                return new StatusWrapper<RequisitoIPNivelAcademicoListado>(nivelAcademicoListado);
              })
          );
        }),
        switchMap(() => of(requisitoIp))
      );
  }

  private saveOrUpdateCategoriasProfesionales(requisitoIp: IConvocatoriaRequisitoIP): Observable<IConvocatoriaRequisitoIP> {
    const hasChanges = this.categoriasProfesionalesEliminadas.length > 0
      || this.categoriasProfesionales$.value.some(nivelAcademico => nivelAcademico.created);

    if (!hasChanges) {
      return of(requisitoIp);
    }

    const values = this.categoriasProfesionales$.value.map(wrapper => {
      wrapper.value.requisitoIP = requisitoIp;
      return wrapper.value;
    }
    );
    const id = this.getKey() as number;
    return this.convocatoriaRequisitoIPService.updateCategoriasProfesionales(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.categoriasProfesionalesEliminadas = [];
          this.categoriasProfesionales$.next(
            results.map(
              (value) => {
                const valueListado = values.find(
                  categoriaProfesional => categoriaProfesional.categoriaProfesional.id === value.categoriaProfesional.id
                );
                const categoriaProfesionalListado: RequisitoIPCategoriaProfesionalListado = {
                  ...value,
                  categoriaProfesional: valueListado?.categoriaProfesional,
                  eliminable: valueListado?.eliminable
                };

                value.categoriaProfesional = values.find(
                  categoriaProfesional => categoriaProfesional.categoriaProfesional.id === value.categoriaProfesional.id
                ).categoriaProfesional;
                return new StatusWrapper<RequisitoIPCategoriaProfesionalListado>(categoriaProfesionalListado);
              })
          );
        }),
        switchMap(() => of(requisitoIp))
      );
  }

}
