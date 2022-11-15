import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { MSG_PARAMS } from '@core/i18n';
import { SolicitudPublicService } from '@core/services/csp/solicitud-public.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';

const SOLICITUD_NUM_DOCUMENTO = marker('pub.solicitud.consultar.numero-documento');
const SOLICITID_UUID = marker('pub.solicitud.consultar.uuid-solicitud');

@Component({
  selector: 'sgi-solicitud-consultar',
  templateUrl: './solicitud-consultar.component.html',
  styleUrls: ['./solicitud-consultar.component.scss']
})
export class SolicitudConsultarComponent extends AbstractMenuContentComponent implements OnInit, OnDestroy {
  protected subscriptions: Subscription[] = [];
  formGroup: FormGroup;

  msgParamNumeroDocumento = {};
  msgParamUUID = {};

  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private solicitudService: SolicitudPublicService,
    private readonly translate: TranslateService
  ) {
    super();
  }

  ngOnInit(): void {
    this.setupI18N();

    this.formGroup = new FormGroup({
      numeroDocumento: new FormControl(undefined, Validators.required),
      uuid: new FormControl(undefined, Validators.required)
    });
  }


  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  onSearch(): void {
    if (!this.formGroup.valid) {
      return;
    }

    const numeroDocumento = this.formGroup.controls.numeroDocumento.value;
    const uuid = this.formGroup.controls.uuid.value;

    this.solicitudService.getPublicId(uuid, numeroDocumento)
      .pipe(
        tap(() => { },
          () => this.clearProblems(),
          () => this.clearProblems()
        ),
      ).subscribe(
        (value) => {
          this.router.navigate([value], { relativeTo: this.activatedRoute });
        },
        this.processError
      );
  }

  onClearFilters(): void {
    this.formGroup.reset();
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_NUM_DOCUMENTO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNumeroDocumento = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITID_UUID,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamUUID = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

}
