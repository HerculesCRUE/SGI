import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { Estado } from '@core/models/csp/estado-proyecto';
import { IProyectoProrroga, TIPO_MAP, Tipo } from '@core/models/csp/proyecto-prorroga';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoProrrogasFragment } from './proyecto-prorrogas.fragment';

const PROYECTO_PRORROGA_KEY = marker('csp.proyecto-prorroga');

@Component({
  selector: 'sgi-proyecto-prorrogas',
  templateUrl: './proyecto-prorrogas.component.html',
  styleUrls: ['./proyecto-prorrogas.component.scss']
})
export class ProyectoProrrogasComponent extends FragmentComponent implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  private subscriptions: Subscription[] = [];
  formPart: ProyectoProrrogasFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  lastProyectoProrroga: IProyectoProrroga;

  displayedColumns = ['numProrroga', 'fechaConcesion', 'tipo', 'fechaFin', 'importe', 'observaciones', 'acciones'];

  msgParamEntity = {};

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoProrroga>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get TIPO_MAP() {
    return TIPO_MAP;
  }

  constructor(
    public actionService: ProyectoActionService,
    private dialogService: DialogService,
    private proyectoProrrogaService: ProyectoProrrogaService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PRORROGAS, actionService);
    this.formPart = this.fragment as ProyectoProrrogasFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subscription = this.formPart.prorrogas$.subscribe(
      (proyectoProrrogas) => {
        this.dataSource.data = proyectoProrrogas;
        if (proyectoProrrogas.length > 0) {
          this.lastProyectoProrroga = proyectoProrrogas.map(wrapper => wrapper.value).reduce((ultimaProrroga: IProyectoProrroga | null, prorroga: IProyectoProrroga) => {
            if (!ultimaProrroga || prorroga.fechaConcesion > ultimaProrroga.fechaConcesion) {
              return prorroga;
            }
            return ultimaProrroga;
          }, null);
        }
      }
    );
    this.subscriptions.push(subscription);

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoProrroga>, property: string) => {
        switch (property) {
          default:
            return wrapper.value[property];
        }
      };
    this.dataSource.sort = this.sort;
  }


  private setupI18N(): void {
    this.translate.get(
      PROYECTO_PRORROGA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  deleteProyectoProrroga(wrapper: StatusWrapper<IProyectoProrroga>) {
    this.proyectoProrrogaService.existsDocumentos(wrapper.value.id).subscribe(res => {
      this.subscriptions.push(
        this.dialogService.showConfirmation(this.getInfoMensajeDelete(wrapper.value, res)).subscribe(
          (aceptado) => {
            if (aceptado) {
              this.formPart.deleteProrroga(wrapper);
              this.recalcularNumProrrogas();
              const ultimaProrroga =
                this.formPart.prorrogas$.value.length > 0 ?
                  this.formPart.prorrogas$.value[this.formPart.prorrogas$.value.length - 1].value : null;
              this.formPart.ultimaProrroga$.next(ultimaProrroga);

              const ultimaFechaProrrogas = this.formPart.prorrogas$.value.reverse().find(prorroga => prorroga.value.fechaFin !== null)?.value.fechaFin ?? null;
              this.formPart.ultimaFechaFinProrrogas$.next(ultimaFechaProrrogas);
            }
          }
        )
      );
    });
  }

  isProyectoEstadoConcedido(): boolean {
    return this.actionService.estado === Estado.CONCEDIDO;
  }

  /**
   * Recalcula los numeros de las prórrogas de todos las prórrogas de la tabla en funcion de su fecha de inicio.
   */
  private recalcularNumProrrogas(): void {
    let numProrroga = 1;
    this.dataSource.data
      .sort((a, b) => (a.value.fechaConcesion > b.value.fechaConcesion) ? 1 : ((b.value.fechaConcesion > a.value.fechaConcesion) ? -1 : 0));

    this.dataSource.data.forEach(c => {
      c.value.numProrroga = numProrroga++;
    });

    this.formPart.prorrogas$.next(this.dataSource.data);
  }

  private getInfoMensajeDelete(proyectoProrroga: IProyectoProrroga, documento: boolean) {
    switch (proyectoProrroga.tipo) {
      case Tipo.IMPORTE:
        return documento
          ? marker('msg.csp.proyecto-prorroga.documentos.importe.delete')
          : marker('msg.csp.proyecto-prorroga.importe.delete');
      case Tipo.TIEMPO:
        return documento
          ? marker('msg.csp.proyecto-prorroga.documentos.tiempo.delete')
          : marker('msg.csp.proyecto-prorroga.tiempo.delete');
      case Tipo.TIEMPO_IMPORTE:
        return documento
          ? marker('msg.csp.proyecto-prorroga.documentos.tiempo-importe.delete')
          : marker('msg.csp.proyecto-prorroga.tiempo-importe.delete');
      default:
        return '';
    }
  }

}
