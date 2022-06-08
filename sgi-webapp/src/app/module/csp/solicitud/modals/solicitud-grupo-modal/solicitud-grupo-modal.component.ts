import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { ISolicitudGrupo } from '@core/models/csp/solicitud-grupo';
import { Module } from '@core/module';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';

const GRUPO_FECHA_INICIO_KEY = marker('label.fecha-inicio');
const GRUPO_FECHA_FIN_KEY = marker('label.fecha-fin');
const GRUPO_NOMBRE_KEY = marker('csp.solicitud.grupo.nombre');
const GRUPO_KEY = marker('csp.grupo');

const SGI_DEP = 'SGIDEP';

@Component({
  selector: 'sgi-solicitud-grupo-modal',
  templateUrl: './solicitud-grupo-modal.component.html',
  styleUrls: ['./solicitud-grupo-modal.component.scss']
})
export class SolicitudGrupoModalComponent extends DialogActionComponent<ISolicitudGrupo> implements OnInit {

  solicitudGrupo: ISolicitudGrupo;

  textSaveOrUpdate: string;

  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamNombreEntity = {};
  msgParamGrupoEntity = {};

  constructor(
    matDialogRef: MatDialogRef<SolicitudGrupoModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ISolicitudGrupo,
    private readonly translate: TranslateService,
    private readonly router: Router,
    private grupoService: GrupoService,
    private solicitudService: SolicitudService,
    private vinculacionService: VinculacionService,
  ) {
    super(matDialogRef, false);
    this.solicitudGrupo = { ...data };
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
    this.setupI18N();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup(
      {
        nombre: new FormControl(this.solicitudGrupo.solicitud.titulo, [Validators.required, Validators.maxLength(250)]),
        fechaInicio: new FormControl(null, [Validators.required]),
        fechaFin: new FormControl(null),
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin', false)
        ]
      }
    );
  }

  protected getValue(): ISolicitudGrupo {
    this.solicitudGrupo.grupo = {
      nombre: this.formGroup.controls.nombre.value,
      fechaInicio: this.formGroup.controls.fechaInicio.value,
      fechaFin: this.formGroup.controls.fechaFin.value
    } as IGrupo;

    return this.solicitudGrupo;
  }

  protected saveOrUpdate(): Observable<ISolicitudGrupo> {
    const solicitudGrupoNew = this.getValue();

    return this.vinculacionService.findByPersonaId(solicitudGrupoNew.solicitud.solicitante.id).pipe(
      switchMap(vinculacion => this.grupoService.getNextCodigo(vinculacion ? vinculacion.departamento.id : SGI_DEP)),
      switchMap(codigoGenerated => {
        solicitudGrupoNew.grupo.codigo = codigoGenerated;
        return this.solicitudService.createGrupoBySolicitud(solicitudGrupoNew.solicitud.id, solicitudGrupoNew.grupo);
      }),
      map((grupo) => {
        solicitudGrupoNew.grupo = grupo;
        return solicitudGrupoNew;
      }),
      tap(({ grupo }) => this.openWindowEditGrupo(grupo.id))
    );
  }

  private openWindowEditGrupo(grupoId: number): void {
    window.open(this.router.serializeUrl(this.router.createUrlTree(['/', Module.CSP.path, CSP_ROUTE_NAMES.GRUPO, grupoId])), '_blank');
  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamGrupoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

}
