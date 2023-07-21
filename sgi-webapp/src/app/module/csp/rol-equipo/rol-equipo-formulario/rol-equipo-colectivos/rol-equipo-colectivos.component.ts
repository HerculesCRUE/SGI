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
import { RolEquipoActionService } from '../../rol-equipo.action.service';
import { RolProyectoColectivoListado, RolEquipoColectivosFragment } from './rol-equipo-colectivos.fragment';
import { RolEquipoColectivoModalComponent, ColectivoModalData } from '../../modals/rol-equipo-colectivo-modal/rol-equipo-colectivo-modal.component';

const MSG_DELETE = marker('msg.delete.entity');
const ROL_EQUIPO_COLECTIVO_KEY = marker('csp.rol-equipo-colectivo');

@Component({
  selector: 'sgi-rol-equipo-colectivos',
  templateUrl: './rol-equipo-colectivos.component.html',
  styleUrls: ['./rol-equipo-colectivos.component.scss']
})
export class RolEquipoColectivosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: RolEquipoColectivosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['colectivo', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<RolProyectoColectivoListado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private actionService: RolEquipoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.ROL_EQUIPO_COLECTIVOS, actionService);

    this.formPart = this.fragment as RolEquipoColectivosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<RolProyectoColectivoListado>, property: string) => {
        switch (property) {
          case 'colectivo':
            return wrapper.value.colectivo;
          default:
            return wrapper[property];
        }
      };

    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.colectivos$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      ROL_EQUIPO_COLECTIVO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      ROL_EQUIPO_COLECTIVO_KEY,
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

  openModal(): void {

    const colectivos: RolProyectoColectivoListado[] = [];
    this.dataSource.data.forEach((wrapper: StatusWrapper<RolProyectoColectivoListado>) => {
      colectivos.push(wrapper.value);
    });

    const config = {
      data: { rolEquipo: this.actionService.getRolEquipo, colectivos } as ColectivoModalData
    };
    const dialogRef = this.matDialog.open(RolEquipoColectivoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: RolProyectoColectivoListado) => {
        if (result?.colectivoRef) {
          this.formPart.addColectivo(result);
        }
      }
    );
  }

  /**
   * Elimina el proyecto colectivo
   *
   * @param wrapper el proyecto colectivo
   */
  deleteColectivo(wrapper: StatusWrapper<RolProyectoColectivoListado>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteColectivo(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
