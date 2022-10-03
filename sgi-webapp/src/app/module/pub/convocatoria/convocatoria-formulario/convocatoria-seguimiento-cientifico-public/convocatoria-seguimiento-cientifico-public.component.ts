import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortable } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { TIPO_SEGUIMIENTO_MAP } from '@core/enums/tipo-seguimiento';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaSeguimientoCientificoPublicFragment } from './convocatoria-seguimiento-cientifico-public.fragment';

@Component({
  selector: 'sgi-convocatoria-seguimiento-cientifico-public',
  templateUrl: './convocatoria-seguimiento-cientifico-public.component.html',
  styleUrls: ['./convocatoria-seguimiento-cientifico-public.component.scss']
})
export class ConvocatoriaSeguimientoCientificoPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaSeguimientoCientificoPublicFragment;
  private subscriptions: Subscription[] = [];

  columnas = ['numPeriodo', 'mesInicial', 'mesFinal', 'fechaInicio', 'fechaFin', 'tipoSeguimiento', 'observaciones', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get TIPO_SEGUIMIENTO_MAP() {
    return TIPO_SEGUIMIENTO_MAP;
  }

  constructor(
    protected actionService: ConvocatoriaPublicActionService
  ) {
    super(actionService.FRAGMENT.SEGUIMIENTO_CIENTIFICO, actionService);
    this.formPart = this.fragment as ConvocatoriaSeguimientoCientificoPublicFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>, property: string) => {
        switch (property) {
          case 'numPeriodo':
            return wrapper.value.numPeriodo;
          case 'mesInicial':
            return wrapper.value.mesInicial;
          case 'mesFinal':
            return wrapper.value.mesFinal;
          case 'fechaInicio':
            return wrapper.value.fechaInicioPresentacion;
          case 'fechaFin':
            return wrapper.value.fechaFinPresentacion;
          case 'tipoSeguimiento':
            return wrapper.value.tipoSeguimiento;
          default:
            return wrapper[property];
        }
      };
    this.sort.sort(({ id: 'numPeriodo', start: 'asc' }) as MatSortable);
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.seguimientosCientificos$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
