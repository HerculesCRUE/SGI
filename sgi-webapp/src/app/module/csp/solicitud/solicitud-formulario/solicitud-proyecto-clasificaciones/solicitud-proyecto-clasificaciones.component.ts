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
import { ClasificacionDataModal, ClasificacionModalComponent } from 'src/app/esb/sgo/shared/clasificacion-modal/clasificacion-modal.component';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudProyectoClasificacionesFragment, SolicitudProyectoClasificacionListado } from './solicitud-proyecto-clasificaciones.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const SOLICITUD_PROYECTO_CLASIFICACION_KEY = marker('csp.solicitud-proyecto-clasificacion');

@Component({
  selector: 'sgi-solicitud-proyecto-clasificaciones',
  templateUrl: './solicitud-proyecto-clasificaciones.component.html',
  styleUrls: ['./solicitud-proyecto-clasificaciones.component.scss']
})
export class SolicitudProyectoClasificacionesComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudProyectoClasificacionesFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['clasificacion', 'niveles', 'nivelSeleccionado', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<SolicitudProyectoClasificacionListado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private actionService: SolicitudActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.CLASIFICACIONES, actionService);

    this.formPart = this.fragment as SolicitudProyectoClasificacionesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<SolicitudProyectoClasificacionListado>, property: string) => {
        switch (property) {
          case 'clasificacion':
            return wrapper.value.clasificacion.nombre;
          case 'niveles':
            return wrapper.value.nivelesTexto;
          case 'nivelSeleccionado':
            return wrapper.value.nivelSeleccionado.nombre;
          default:
            return wrapper[property];
        }
      };

    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.clasificaciones$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_CLASIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_CLASIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  /**
   * Apertura de modal de clasificaciones
   */
  openModal(): void {
    const data: ClasificacionDataModal = {
      selectedClasificaciones: this.formPart.clasificaciones$.value.map(wrapper => wrapper.value.nivelSeleccionado)
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(ClasificacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (clasificaciones) => {
        if (clasificaciones && clasificaciones.length > 0) {
          this.formPart.addClasificaciones(clasificaciones);
        }
      }
    );
  }

  /**
   * Elimina la clasificacion
   *
   * @param wrapper la clasificacion
   */
  deleteClasificacion(wrapper: StatusWrapper<SolicitudProyectoClasificacionListado>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteClasificacion(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
