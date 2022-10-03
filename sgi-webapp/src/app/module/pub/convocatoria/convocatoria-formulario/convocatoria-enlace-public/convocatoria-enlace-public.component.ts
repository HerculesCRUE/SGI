import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaEnlacePublicFragment } from './convocatoria-enlace-public.fragment';

@Component({
  selector: 'sgi-convocatoria-enlace-public',
  templateUrl: './convocatoria-enlace-public.component.html',
  styleUrls: ['./convocatoria-enlace-public.component.scss']
})
export class ConvocatoriaEnlacePublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaEnlacePublicFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['url', 'descripcion', 'tipoEnlace', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaEnlace>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: ConvocatoriaPublicActionService
  ) {
    super(actionService.FRAGMENT.ENLACES, actionService);
    this.formPart = this.fragment as ConvocatoriaEnlacePublicFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaEnlace>, property: string) => {
        switch (property) {
          case 'url':
            return wrapper.value.url;
          case 'descripcion':
            return wrapper.value.descripcion;
          case 'tipoEnlace':
            return wrapper.value.tipoEnlace.nombre;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.enlace$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
