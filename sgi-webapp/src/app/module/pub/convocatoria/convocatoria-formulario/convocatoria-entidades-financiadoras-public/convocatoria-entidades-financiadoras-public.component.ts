import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { EmpresaPublicService } from '@core/services/sgemp/empresa-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { forkJoin, of, Subscription } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaEntidadesFinanciadorasPublicFragment } from './convocatoria-entidades-financiadoras-public.fragment';

@Component({
  selector: 'sgi-convocatoria-entidades-financiadoras-public',
  templateUrl: './convocatoria-entidades-financiadoras-public.component.html',
  styleUrls: ['./convocatoria-entidades-financiadoras-public.component.scss']
})
export class ConvocatoriaEntidadesFinanciadorasPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaEntidadesFinanciadorasPublicFragment;
  private subscriptions: Subscription[] = [];

  columns = ['nombre', 'cif', 'fuenteFinanciacion', 'ambito', 'tipoFinanciacion',
    'porcentajeFinanciacion', 'importeFinanciacion', 'acciones'];
  elementsPage = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaEntidadFinanciadora>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected actionService: ConvocatoriaPublicActionService,
    private empresaService: EmpresaPublicService
  ) {
    super(actionService.FRAGMENT.ENTIDADES_FINANCIADORAS, actionService);
    this.formPart = this.fragment as ConvocatoriaEntidadesFinanciadorasPublicFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.getDataSource();
  }

  private getDataSource(): void {
    this.dataSource.data = [];
    this.subscriptions.push(
      this.formPart.convocatoriaEntidadesFinanciadoras$.pipe(
        switchMap(wrappers => {
          return forkJoin(wrappers.map(
            wrapper => {
              return this.empresaService.findById(wrapper.value.empresa.id).pipe(
                map(empresa => {
                  wrapper.value.empresa = empresa;
                  return wrapper;
                }),
                catchError(() => {
                  return of(wrapper);
                })
              );
            })
          );
        })
      ).subscribe(elements => {
        this.dataSource.data = elements;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }
}
