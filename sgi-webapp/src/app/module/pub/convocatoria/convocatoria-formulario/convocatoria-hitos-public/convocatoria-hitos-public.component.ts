import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaHitosPublicFragment } from './convocatoria-hitos-public.fragment';

@Component({
  selector: 'sgi-convocatoria-hitos-public',
  templateUrl: './convocatoria-hitos-public.component.html',
  styleUrls: ['./convocatoria-hitos-public.component.scss']
})
export class ConvocatoriaHitosPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ConvocatoriaHitosPublicFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['fechaInicio', 'tipoHito', 'comentario', 'aviso', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaHito>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: ConvocatoriaPublicActionService
  ) {
    super(actionService.FRAGMENT.HITOS, actionService);
    this.formPart = this.fragment as ConvocatoriaHitosPublicFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaHito>, property: string) => {
        switch (property) {
          case 'fechaInicio':
            return wrapper.value.fecha;
          case 'tipoHito':
            return wrapper.value.tipoHito.nombre;
          case 'comentario':
            return wrapper.value.comentario;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.hitos$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
