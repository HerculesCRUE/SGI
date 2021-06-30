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
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoAreaConocimientoFragment, ProyectoAreaConocimientoListado } from './proyecto-area-conocimiento.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_AREA_CONOCIMIENTO_KEY = marker('title.csp.proyecto-area-conocimiento');

@Component({
  selector: 'sgi-proyecto-area-conocimiento',
  templateUrl: './proyecto-area-conocimiento.component.html',
  styleUrls: ['./proyecto-area-conocimiento.component.scss']
})
export class ProyectoAreaConocimientoComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ProyectoAreaConocimientoFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = ['niveles', 'nivelSeleccionado', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<ProyectoAreaConocimientoListado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  textoDelete: string;

  constructor(
    protected actionService: ProyectoActionService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
  ) {
    super(actionService.FRAGMENT.AREA_CONOCIMIENTO, actionService);
    this.formPart = this.fragment as ProyectoAreaConocimientoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<ProyectoAreaConocimientoListado>, property: string) => {
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
      PROYECTO_AREA_CONOCIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_AREA_CONOCIMIENTO_KEY,
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
      panelClass: 'sgi-dialog-container',
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
   * Elimina el área de conocimiento
   *
   * @param wrapper el área
   */
  deleteArea(wrapper: StatusWrapper<ProyectoAreaConocimientoListado>): void {
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
