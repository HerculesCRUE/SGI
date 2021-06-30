import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SearchProyectoEconomicoModalData, SearchProyectosEconomicosModalComponent } from '../../../shared/search-proyectos-economicos-modal/search-proyectos-economicos-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoProyectosSgeFragment } from './proyecto-proyectos-sge.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const IDENTIFICADOR_SGE_KEY = marker('csp.proyecto-proyecto-sge.identificador-sge');

@Component({
  selector: 'sgi-proyecto-proyectos-sge',
  templateUrl: './proyecto-proyectos-sge.component.html',
  styleUrls: ['./proyecto-proyectos-sge.component.scss']
})
export class ProyectoProyectosSgeComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ProyectoProyectosSgeFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['proyectoSgeRef', 'sectorIva', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoProyectoSge>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PROYECTOS_SGE, actionService);

    this.formPart = this.fragment as ProyectoProyectosSgeFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoProyectoSge>, property: string) => {
        switch (property) {
          case 'proyectoSgeRef':
            return wrapper.value.proyectoSge.id;
          default:
            return wrapper[property];
        }
      };

    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.proyectosSge$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      IDENTIFICADOR_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      IDENTIFICADOR_SGE_KEY,
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
   * Apertura de modal de clasificaciones
   */
  openModal(): void {
    const data: SearchProyectoEconomicoModalData = {
      selectedProyectos: this.dataSource.data.map((proyectoProyectoSge) => proyectoProyectoSge.value.proyectoSge),
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(SearchProyectosEconomicosModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (proyectoSge) => {
        if (proyectoSge) {
          this.formPart.addProyectoSge(proyectoSge);
        }
      }
    );
  }

  /**
   * Elimina la clasificacion
   *
   * @param wrapper la clasificacion
   */
  deleteProyectoSge(wrapper: StatusWrapper<IProyectoProyectoSge>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteProyectoSge(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
