import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPeriodoAmortizacion } from '@core/models/csp/proyecto-periodo-amortizacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { IProyectoPeriodoAmortizacionModalData, ProyectoPeriodoAmortizacionModalComponent } from '../../modals/proyecto-periodo-amortizacion-fondos-modal/proyecto-periodo-justificacion-modal/proyecto-periodo-amortizacion-fondos-modal.component';
import { PROYECTO_ROUTE_NAMES } from '../../proyecto-route-names';
import { ProyectoActionService } from '../../proyecto.action.service';
import { IEntidadFinanciadora } from '../proyecto-entidades-financiadoras/proyecto-entidades-financiadoras.fragment';
import { IProyectoPeriodoAmortizacionListado, ProyectoAmortizacionFondosFragment } from './proyecto-amortizacion-fondos.fragment';

const MSG_NUEVO = marker('title.new.entity');
const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_ENTIDAD_FINANCIADORA_KEY = marker('csp.proyecto-amortizacion-fondos.entidad-financiadora');
const PROYECTO_PERIODO_AMORTIZACION_KEY = marker('csp.proyecto-amortizacion-fondos.periodo-amortizacion');

@Component({
  selector: 'sgi-proyecto-amortizacion-fondos',
  templateUrl: './proyecto-amortizacion-fondos.component.html',
  styleUrls: ['./proyecto-amortizacion-fondos.component.scss']
})
export class ProyectoAmortizacionFondosComponent extends FragmentComponent implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  get PROYECTO_ROUTE_NAMES() {
    return PROYECTO_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formPart: ProyectoAmortizacionFondosFragment;
  private subscriptions: Subscription[] = [];
  private elements = [5, 10, 25, 100];

  msgParamEntityEntidades = {};
  msgParamEntityPeriodoAmortizacion = {};
  textoDeletePeriodosAmortizacion: string;
  private msgCrear: string;
  private modalTitle: string;

  columnsEntidadesFinanciadoras = ['nombre', 'cif', 'fuenteFinanciacion', 'ambito', 'tipoFinanciacion',
    'porcentajeFinanciacion', 'importeFinanciacion', 'ajena'];
  elementsEntidadesFinanciadoras = [...this.elements];
  columnsPeriodosAmortizacion = ['entidad', 'tipoFinanciacion', 'periodo', 'anualidad', 'fechaInicioAnualidad', 'fechaFinAnualidad', 'fechaLimite', 'importe', 'acciones'];
  elementsPeriodosAmortizacion = [...this.elements];

  dataSourceEntidades = new MatTableDataSource<StatusWrapper<IEntidadFinanciadora>>();
  dataSourcePeriodosAmortizacion = new MatTableDataSource<StatusWrapper<IProyectoPeriodoAmortizacionListado>>();
  @ViewChild('paginatorEntidades', { static: true }) paginatorEntidades: MatPaginator;
  @ViewChild('sortEntidades', { static: true }) sortEntidades: MatSort;
  @ViewChild('paginatorPeriodos', { static: true }) paginatorPeriodos: MatPaginator;
  @ViewChild('sortPeriodos', { static: true }) sortPeriodos: MatSort;

  constructor(
    protected actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.AMORTIZACION_FONDOS, actionService);
    this.formPart = this.fragment as ProyectoAmortizacionFondosFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '1';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSourceEntidades.paginator = this.paginatorEntidades;
    this.dataSourceEntidades.sort = this.sortEntidades;
    this.subscriptions.push(
      this.formPart.entidadesFinanciadoras$.subscribe((elements) => this.dataSourceEntidades.data = elements)
    );

    this.dataSourcePeriodosAmortizacion.paginator = this.paginatorPeriodos;
    this.dataSourcePeriodosAmortizacion.sort = this.sortPeriodos;
    this.subscriptions.push(
      this.formPart.periodosAmortizacion$.subscribe((elements) => this.dataSourcePeriodosAmortizacion.data = elements)
    );
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntityEntidades = { entity: value });

    this.translate.get(
      PROYECTO_PERIODO_AMORTIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntityPeriodoAmortizacion = { entity: value });

    this.translate.get(
      PROYECTO_PERIODO_AMORTIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitle = value);

    this.translate.get(
      PROYECTO_PERIODO_AMORTIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeletePeriodosAmortizacion = value);

    this.translate.get(
      PROYECTO_PERIODO_AMORTIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_NUEVO,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.msgCrear = value);
  }

  openModal(wrapper?: StatusWrapper<IProyectoPeriodoAmortizacionListado>): void {
    const data: IProyectoPeriodoAmortizacionModalData = {
      title: !!!wrapper ? this.msgCrear : this.modalTitle,
      periodoAmortizacion: wrapper?.value ?? {} as IProyectoPeriodoAmortizacionListado,
      proyectoId: this.formPart.getKey() as number,
      entidadesFinanciadoras: this.formPart.entidadesFinanciadoras$.value.map(entidad => entidad.value),
      proyectosSGE: this.formPart.proyectosSGE$.value,
      anualidadGenerica: !this.formPart.anualidades
    };
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(ProyectoPeriodoAmortizacionModalComponent, config);
    dialogRef.afterClosed().subscribe(periodoAmortizacionModalData => {
      if (periodoAmortizacionModalData) {
        if (!wrapper) {
          this.formPart.addPeriodoAmortizacion(periodoAmortizacionModalData.periodoAmortizacion);
        } else if (!wrapper.created) {
          const periodo = new StatusWrapper<IProyectoPeriodoAmortizacionListado>(wrapper.value);
          this.formPart.updatePeriodoAmortizacion(periodo);
        }
      }
      this.formPart.recalcularNumPeriodos();
    });
  }


  deletePeriodoAmortizacion(wrapper: StatusWrapper<IProyectoPeriodoAmortizacion>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDeletePeriodosAmortizacion).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePeriodoAmortizacion(wrapper);
            this.formPart.recalcularNumPeriodos();
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
