import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { FormFragment } from '@core/services/action-service';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NumberValidator } from '@core/validators/number-validator';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export class SolicitudProyectoSocioDatosGeneralesFragment extends FormFragment<ISolicitudProyectoSocio> {
  solicitudProyectoSocio: ISolicitudProyectoSocio;

  constructor(
    key: number,
    private solicitudProyectoId: number,
    private service: SolicitudProyectoSocioService,
    public readonly
  ) {
    super(key);
    this.solicitudProyectoSocio = {} as ISolicitudProyectoSocio;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup(
      {
        empresa: new FormControl('', [Validators.required]),
        rolSocio: new FormControl('', [
          Validators.required,
          IsEntityValidator.isValid()
        ]),
        numInvestigadores: new FormControl('', [
          Validators.min(1),
          Validators.max(9999)
        ]),
        importeSolicitado: new FormControl('', [
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]),
        importePresupuestado: new FormControl('', [
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]),
        mesInicio: new FormControl('', [
          Validators.min(1),
          Validators.max(9999)
        ]),
        mesFin: new FormControl('', [
          Validators.min(1),
          Validators.max(9999)
        ]),
      },
      {
        validators: [
          NumberValidator.isAfterOptional('mesInicio', 'mesFin')
        ]
      }
    );

    if (this.readonly) {
      form.disable();
    }

    return form;
  }

  protected buildPatch(solicitudProyectoSocio: ISolicitudProyectoSocio): { [key: string]: any; } {
    const result = {
      empresa: solicitudProyectoSocio.empresa,
      rolSocio: solicitudProyectoSocio.rolSocio,
      numInvestigadores: solicitudProyectoSocio.numInvestigadores,
      importeSolicitado: solicitudProyectoSocio.importeSolicitado,
      importePresupuestado: solicitudProyectoSocio.importePresupuestado,
      mesInicio: solicitudProyectoSocio.mesInicio,
      mesFin: solicitudProyectoSocio.mesFin,
    };
    this.solicitudProyectoSocio = solicitudProyectoSocio;
    return result;
  }

  protected initializer(key: number): Observable<ISolicitudProyectoSocio> {
    return this.service.findById(key);
  }

  getValue(): ISolicitudProyectoSocio {
    const form = this.getFormGroup().controls;
    this.solicitudProyectoSocio.empresa = form.empresa.value;
    this.solicitudProyectoSocio.rolSocio = form.rolSocio.value;
    this.solicitudProyectoSocio.numInvestigadores = form.numInvestigadores.value;
    this.solicitudProyectoSocio.importeSolicitado = form.importeSolicitado.value;
    this.solicitudProyectoSocio.importePresupuestado = form.importePresupuestado.value;
    this.solicitudProyectoSocio.mesInicio = form.mesInicio.value;
    this.solicitudProyectoSocio.mesFin = form.mesFin.value;
    return this.solicitudProyectoSocio;
  }

  saveOrUpdate(): Observable<number> {
    const solicitudProyectoSocio = this.getValue();
    const observable$ = this.isEdit() ? this.update(solicitudProyectoSocio) :
      this.create(solicitudProyectoSocio);
    return observable$.pipe(
      map(result => {
        this.solicitudProyectoSocio = result;
        return this.solicitudProyectoSocio.id;
      })
    );
  }

  private create(solicitudProyectoSocio: ISolicitudProyectoSocio): Observable<ISolicitudProyectoSocio> {
    solicitudProyectoSocio.solicitudProyectoId = this.solicitudProyectoId;
    return this.service.create(solicitudProyectoSocio);
  }

  private update(solicitudProyectoSocio: ISolicitudProyectoSocio): Observable<ISolicitudProyectoSocio> {
    return this.service.update(solicitudProyectoSocio.id, solicitudProyectoSocio);
  }
}
