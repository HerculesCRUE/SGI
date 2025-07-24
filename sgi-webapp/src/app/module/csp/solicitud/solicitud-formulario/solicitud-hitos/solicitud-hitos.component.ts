import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { DialogService } from '@core/services/dialog.service';
import { LanguageService } from '@core/services/language.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SolicitudHitosModalComponent, SolicitudHitosModalComponentData } from '../../modals/solicitud-hitos-modal/solicitud-hitos-modal.component';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudHitosFragment } from './solicitud-hitos.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const SOLICITUD_HITO_KEY = marker('csp.solicitud-hito');

@Component({
  selector: 'sgi-solicitud-hitos',
  templateUrl: './solicitud-hitos.component.html',
  styleUrls: ['./solicitud-hitos.component.scss']
})
export class SolicitudHitosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudHitosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['fechaInicio', 'tipoHito', 'comentario', 'aviso', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudHito>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected convocatoriaReunionService: ConvocatoriaService,
    public readonly actionService: SolicitudActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    private authService: SgiAuthService,
    private readonly languageService: LanguageService
  ) {
    super(actionService.FRAGMENT.HITOS, actionService, translate);

    this.formPart = this.fragment as SolicitudHitosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();


    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<ISolicitudHito>, property: string) => {
        switch (property) {
          case 'fechaInicio':
            return wrapper.value.fecha;
          case 'tipoHito':
            return this.languageService.getFieldValue(wrapper.value.tipoHito.nombre);
          case 'comentario':
            return this.languageService.getFieldValue(wrapper.value.comentario);
          default:
            return wrapper[property];
        }
      };

    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.hitos$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  protected setupI18N(): void {
    this.translate.get(
      SOLICITUD_HITO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_HITO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  /**
   * Apertura de modal de hitos (edición/creación)
   * @param idHito Identificador de hito a editar.
   */
  openModal(wrapper?: StatusWrapper<ISolicitudHito>): void {
    const data: SolicitudHitosModalComponentData = {
      hitos: this.dataSource.data.filter(existing => existing !== wrapper).map(hito => hito.value),
      hito: wrapper ? wrapper.value : {} as ISolicitudHito,
      idModeloEjecucion: this.actionService.modeloEjecucionId,
      readonly: wrapper ? !this.modificable(wrapper) : this.formPart.readonly,
      unidadGestionId: this.actionService.solicitud?.unidadGestion?.id,
      tituloSolicitud: this.actionService.solicitud?.titulo,
      tituloConvocatoria: this.actionService.convocatoriaTitulo
    };
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(SolicitudHitosModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: SolicitudHitosModalComponentData) => {
        if (modalData) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addHito(modalData.hito);
          }
        }
      }
    );
  }

  /**
   * Desactivar solicitud hito
   */
  deleteHito(wrapper: StatusWrapper<ISolicitudHito>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteHito(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  modificable(solicitudHito: StatusWrapper<ISolicitudHito>): boolean {
    return !this.formPart.readonly
      && (!solicitudHito.value?.createdBy || solicitudHito.value.createdBy === this.authService.authStatus$?.getValue()?.userRefId);
  }

}
