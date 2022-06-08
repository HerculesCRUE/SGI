import { FormBuilder, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { FormFragment } from '@core/services/action-service';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { DateValidator } from '@core/validators/date-validator';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

export class GrupoLineaInvestigacionDatosGeneralesFragment extends FormFragment<IGrupoLineaInvestigacion>  {
  private grupoLineaInvestigacion: IGrupoLineaInvestigacion;
  public readonly: boolean;

  constructor(
    private fb: FormBuilder,
    readonly: boolean,
    key: number,
    private grupo: IGrupo,
    private service: GrupoLineaInvestigacionService) {
    super(key);
    this.grupoLineaInvestigacion = {} as IGrupoLineaInvestigacion;
    this.readonly = readonly;
  }

  protected buildFormGroup(): FormGroup {
    const fechaFinValidators: Array<ValidatorFn> = [];
    const fechaInicioValidators: Array<ValidatorFn> = [Validators.required];
    const form = new FormGroup(
      {
        lineaInvestigacion: new FormControl(null, [
          Validators.required
        ]),
        fechaInicio: new FormControl(!this.isEdit() ? this.grupo?.fechaInicio : null),
        fechaFin: new FormControl(null),
      },
    );

    fechaFinValidators.push(DateValidator.isAfterOther(form.controls.fechaInicio));
    if (this.grupo?.fechaFin) {
      fechaFinValidators.push(DateValidator.isBetween(this.grupo?.fechaInicio, this.grupo?.fechaFin));
      fechaInicioValidators.push(DateValidator.isBetween(this.grupo?.fechaInicio, this.grupo?.fechaFin));
    } else {
      fechaInicioValidators.push(DateValidator.minDate(this.grupo?.fechaInicio));
    }

    form.controls.fechaInicio.setValidators(fechaInicioValidators);
    form.controls.fechaFin.setValidators(fechaFinValidators);

    if (this.readonly) {
      form.disable();
    }

    return form;
  }

  buildPatch(value: IGrupoLineaInvestigacion): { [key: string]: any } {
    this.grupoLineaInvestigacion = value;

    return {
      lineaInvestigacion: value.lineaInvestigacion,
      fechaInicio: value.fechaInicio,
      fechaFin: value.fechaFin
    };
  }

  getValue(): IGrupoLineaInvestigacion {
    const form = this.getFormGroup().controls;
    this.grupoLineaInvestigacion.lineaInvestigacion = form.lineaInvestigacion.value;
    this.grupoLineaInvestigacion.fechaInicio = form.fechaInicio.value;
    this.grupoLineaInvestigacion.fechaFin = form.fechaFin.value;
    return this.grupoLineaInvestigacion;
  }

  saveOrUpdate(): Observable<number> {
    const datosGenerales = this.getValue();
    datosGenerales.grupo = {} as IGrupo;
    datosGenerales.grupo.id = this.grupo.id;
    const obs = this.isEdit()
      ? this.service.update(this.getKey() as number, datosGenerales) : this.service.create(datosGenerales);
    return obs.pipe(
      map((value) => {
        value.lineaInvestigacion = datosGenerales.lineaInvestigacion;
        this.grupoLineaInvestigacion = value;
        this.setKey(this.grupoLineaInvestigacion.id);
        return this.grupoLineaInvestigacion.id;
      })
    );
  }

  protected initializer(key: number): Observable<IGrupoLineaInvestigacion> {
    if (this.getKey()) {
      return this.service.findById(key).pipe(
        switchMap((grupoLineaInvestigacion) => {
          return of(grupoLineaInvestigacion);
        })
      );
    }
  }

}
