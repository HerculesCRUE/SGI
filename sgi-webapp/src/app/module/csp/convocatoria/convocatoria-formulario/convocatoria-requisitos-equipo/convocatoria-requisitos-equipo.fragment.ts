import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaRequisitoEquipoService } from '@core/services/csp/convocatoria-requisito-equipo.service';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export class ConvocatoriaRequisitosEquipoFragment extends FormFragment<IConvocatoriaRequisitoEquipo> {

  private requisitoEquipo: IConvocatoriaRequisitoEquipo;

  constructor(
    private fb: FormBuilder,
    key: number,
    private convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoService,
    public readonly: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.requisitoEquipo = {} as IConvocatoriaRequisitoEquipo;
  }

  protected buildFormGroup(): FormGroup {
    const form = this.fb.group({
      nivelAcademicoRef: ['', Validators.maxLength(50)],
      aniosNivelAcademico: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      edadMaxima: ['', Validators.compose(
        [Validators.min(0), Validators.max(99)])],
      vinculacionUniversidad: [null],
      modalidadContratoRef: ['', Validators.maxLength(50)],
      aniosVinculacion: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
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
    if (this.readonly) {
      form.disable();
    }
    return form;
  }

  protected initializer(key: number): Observable<IConvocatoriaRequisitoEquipo> {
    if (this.getKey()) {
      return this.convocatoriaRequisitoEquipoService.findById(key);
    }
  }

  buildPatch(value: IConvocatoriaRequisitoEquipo): { [key: string]: any } {
    this.requisitoEquipo = value;
    return {
      nivelAcademicoRef: value ? value.nivelAcademicoRef : null,
      aniosNivelAcademico: value ? value.aniosNivelAcademico : null,
      edadMaxima: value ? value.edadMaxima : null,
      vinculacionUniversidad: value ? value.vinculacionUniversidad : null,
      modalidadContratoRef: value ? value.modalidadContratoRef : null,
      aniosVinculacion: value ? value.aniosVinculacion : null,
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
    const form = this.getFormGroup().value;
    this.requisitoEquipo.nivelAcademicoRef = form.nivelAcademicoRef;
    this.requisitoEquipo.aniosNivelAcademico = form.aniosNivelAcademico;
    this.requisitoEquipo.edadMaxima = form.edadMaxima;
    this.requisitoEquipo.vinculacionUniversidad = form.vinculacionUniversidad;
    this.requisitoEquipo.modalidadContratoRef = form.modalidadContratoRef;
    this.requisitoEquipo.aniosVinculacion = form.aniosVinculacion;
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
    const obs = datosrequisitoEquipo.id ? this.update(datosrequisitoEquipo) :
      this.create(datosrequisitoEquipo);
    return obs.pipe(
      map((value) => {
        this.requisitoEquipo = value;
        return this.requisitoEquipo.id;
      })
    );
  }

  private create(requisitoEquipo: IConvocatoriaRequisitoEquipo): Observable<IConvocatoriaRequisitoEquipo> {
    return this.convocatoriaRequisitoEquipoService.create(requisitoEquipo).pipe(
      tap(result => this.requisitoEquipo = result)
    );
  }

  private update(requisitoEquipo: IConvocatoriaRequisitoEquipo): Observable<IConvocatoriaRequisitoEquipo> {
    return this.convocatoriaRequisitoEquipoService.update(Number(this.getKey()), requisitoEquipo).pipe(
      tap(result => this.requisitoEquipo = result)
    );
  }

}
