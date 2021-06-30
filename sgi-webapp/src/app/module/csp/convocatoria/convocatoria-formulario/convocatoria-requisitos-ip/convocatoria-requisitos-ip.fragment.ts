import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export class ConvocatoriaRequisitosIPFragment extends FormFragment<IConvocatoriaRequisitoIP> {
  private requisitoIP: IConvocatoriaRequisitoIP;

  constructor(
    private fb: FormBuilder,
    key: number,
    private convocatoriaRequisitoIPService: ConvocatoriaRequisitoIPService,
    public readonly: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.requisitoIP = {} as IConvocatoriaRequisitoIP;
  }

  protected buildFormGroup(): FormGroup {
    const form = this.fb.group({
      numMaximoIP: [null, Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      nivelAcademicoRef: ['', Validators.maxLength(50)],
      aniosNivelAcademico: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      edadMaxima: ['', Validators.compose(
        [Validators.min(0), Validators.max(99)])],
      sexo: [null],
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

  protected initializer(key: number): Observable<IConvocatoriaRequisitoIP> {
    if (this.getKey()) {
      return this.convocatoriaRequisitoIPService.getRequisitoIPConvocatoria(key);
    }
  }

  buildPatch(value: IConvocatoriaRequisitoIP): { [key: string]: any } {
    this.requisitoIP = value;
    return {
      numMaximoIP: value ? value.numMaximoIP : null,
      nivelAcademicoRef: value ? value.nivelAcademicoRef : null,
      aniosNivelAcademico: value ? value.aniosNivelAcademico : null,
      edadMaxima: value ? value.edadMaxima : null,
      sexo: value ? value.sexo : null,
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

  getValue(): IConvocatoriaRequisitoIP {
    if (this.requisitoIP === null) {
      this.requisitoIP = {} as IConvocatoriaRequisitoIP;
    }
    const form = this.getFormGroup().value;
    this.requisitoIP.numMaximoIP = form.numMaximoIP;
    this.requisitoIP.nivelAcademicoRef = form.nivelAcademicoRef;
    this.requisitoIP.aniosNivelAcademico = form.aniosNivelAcademico;
    this.requisitoIP.edadMaxima = form.edadMaxima;
    this.requisitoIP.sexo = form.sexo;
    this.requisitoIP.vinculacionUniversidad = form.vinculacionUniversidad;
    this.requisitoIP.modalidadContratoRef = form.modalidadContratoRef;
    this.requisitoIP.aniosVinculacion = form.aniosVinculacion;
    this.requisitoIP.numMinimoCompetitivos = form.numMinimoCompetitivos;
    this.requisitoIP.numMinimoNoCompetitivos = form.numMinimoNoCompetitivos;
    this.requisitoIP.numMaximoCompetitivosActivos = form.numMaximoCompetitivosActivos;
    this.requisitoIP.numMaximoNoCompetitivosActivos = form.numMaximoNoCompetitivosActivos;
    this.requisitoIP.otrosRequisitos = form.otrosRequisitos;
    return this.requisitoIP;
  }

  saveOrUpdate(): Observable<number> {
    const datosRequisitoIP = this.getValue();
    datosRequisitoIP.convocatoriaId = this.getKey() as number;
    const obs = datosRequisitoIP.id ? this.update(datosRequisitoIP) :
      this.create(datosRequisitoIP);
    return obs.pipe(
      map((value) => {
        this.requisitoIP = value;
        return this.requisitoIP.id;
      })
    );
  }

  private create(requisitoIP: IConvocatoriaRequisitoIP): Observable<IConvocatoriaRequisitoIP> {
    return this.convocatoriaRequisitoIPService.create(requisitoIP).pipe(
      tap(result => this.requisitoIP = result)
    );
  }

  private update(requisitoIP: IConvocatoriaRequisitoIP): Observable<IConvocatoriaRequisitoIP> {
    return this.convocatoriaRequisitoIPService.update(Number(this.getKey()), requisitoIP).pipe(
      tap(result => this.requisitoIP = result)
    );
  }

}
