import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AreaConocimientoDataModal, AreaConocimientoModalComponent } from 'src/app/esb/sgo/shared/area-conocimiento-modal/area-conocimiento-modal.component';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudProyectoAreaConocimientoFragment, SolicitudProyectoAreaConocimientoListado } from './solicitud-proyecto-area-conocimiento.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const SOLICITUD_PROYECTO_AREA_CONOCIMIENTO_KEY = marker('title.csp.solicitud-proyecto-area-conocimiento');

@Component({
  selector: 'sgi-solicitud-proyecto-area-conocimiento',
  templateUrl: './solicitud-proyecto-area-conocimiento.component.html',
  styleUrls: ['./solicitud-proyecto-area-conocimiento.component.scss']
})
export class SolicitudProyectoAreaConocimientoComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudProyectoAreaConocimientoFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = ['niveles', 'nivelSeleccionado', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<SolicitudProyectoAreaConocimientoListado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  textoDelete: string;

  constructor(
    protected actionService: SolicitudActionService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
  ) {
    super(actionService.FRAGMENT.PROYECTO_AREA_CONOCIMIENTO, actionService);
    this.formPart = this.fragment as SolicitudProyectoAreaConocimientoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<SolicitudProyectoAreaConocimientoListado>, property: string) => {
        switch (property) {
          case 'niveles':
            return wrapper.value.nivelesTexto;
          case 'nivelSeleccionado':
            return wrapper.value.nivelSeleccionado.nombre;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.areasConocimiento$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_AREA_CONOCIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_AREA_CONOCIMIENTO_KEY,
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
   * Apertura de modal de Areas Conocimiento
   */
  openModal(): void {
    const data: AreaConocimientoDataModal = {
      selectedAreasConocimiento: this.formPart.areasConocimiento$.value.map(wrapper => wrapper.value.nivelSeleccionado),
      multiSelect: true
    };
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(AreaConocimientoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (areas) => {
        if (areas && areas.length > 0) {
          this.formPart.addAreas(areas);
        }
      }
    );
  }

  /**
   * Elimina la clasificacion
   *
   * @param wrapper la clasificacion
   */
  deleteArea(wrapper: StatusWrapper<SolicitudProyectoAreaConocimientoListado>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteArea(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
