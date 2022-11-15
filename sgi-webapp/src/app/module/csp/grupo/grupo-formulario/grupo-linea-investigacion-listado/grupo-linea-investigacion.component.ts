import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { ROUTE_NAMES } from '@core/route.names';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { GrupoActionService } from '../../grupo.action.service';
import { GrupoLineaInvestigacionFragment } from './grupo-linea-investigacion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const GRUPO_LINEA_INVESTIGACION_KEY = marker('csp.grupo-linea-investigacion');
const MODAL_TITLE_KEY = marker('csp.grupo-linea-investigacion');

@Component({
  selector: 'sgi-grupo-linea-investigacion',
  templateUrl: './grupo-linea-investigacion.component.html',
  styleUrls: ['./grupo-linea-investigacion.component.scss']
})
export class GrupoLineaInvestigacionComponent extends FragmentComponent implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;
  private subscriptions: Subscription[] = [];
  formPart: GrupoLineaInvestigacionFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['nombre', 'fechaInicio', 'fechaFin', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IGrupoLineaInvestigacion>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected proyectoService: GrupoService,
    public actionService: GrupoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.LINEA_INVESTIGACION, actionService);
    this.formPart = this.fragment as GrupoLineaInvestigacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IGrupoLineaInvestigacion>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.lineaInvestigacion?.nombre;
          case 'fechaInicio':
            return wrapper.value.fechaInicio;
          case 'fechaFin':
            return wrapper.value.fechaFin;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.lineasInvestigaciones$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_LINEA_INVESTIGACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      GRUPO_LINEA_INVESTIGACION_KEY,
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

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Eliminar grupo equipo
   */
  deleteEquipo(wrapper: StatusWrapper<IGrupoLineaInvestigacion>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteGrupoLineaInvestigacion(wrapper);
          }
        }
      )
    );
  }

}
