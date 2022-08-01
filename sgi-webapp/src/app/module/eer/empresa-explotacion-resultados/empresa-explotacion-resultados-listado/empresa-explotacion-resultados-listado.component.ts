import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { EmpresaExplotacionResultadosService } from '@core/services/eer/empresa-explotacion-resultados/empresa-explotacion-resultados.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { EER_ROUTE_NAMES } from '../../eer-route-names';

const MSG_BUTTON_ADD = marker('btn.add.entity');
const MSG_ERROR_LOAD = marker('error.load');
const MSG_ERROR_DELETE = marker('error.delete.entity');
const MSG_DELETE = marker('msg.delete.entity');
const MSG_SUCCESS_DELETE = marker('msg.delete.entity.success');
const EMPRESA_KEY = marker('eer.empresa-explotacion-resultados');
const EMPRESA_ENTIDAD_KEY = marker('eer.empresa-explotacion-resultados.entidad');

@Component({
  selector: 'sgi-empresa-explotacion-resultados-listado',
  templateUrl: './empresa-explotacion-resultados-listado.component.html',
  styleUrls: ['./empresa-explotacion-resultados-listado.component.scss']
})
export class EmpresaExplotacionResultadosListadoComponent extends AbstractTablePaginationComponent<IEmpresaExplotacionResultados> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  EER_ROUTE_NAMES = EER_ROUTE_NAMES;

  empresas$: Observable<IEmpresaExplotacionResultados[]>;
  colectivosBusqueda: string[];
  isInvestigador: boolean;
  isVisor: boolean;

  textoCrear: string;
  textoErrorDelete: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  msgParamEntidadEntity = {};

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    private empresaExplotacionResultadosService: EmpresaExplotacionResultadosService,
    private dialogService: DialogService,
    public authService: SgiAuthService,
    private readonly translate: TranslateService,
    private empresaService: EmpresaService
  ) {
    super(snackBarService, MSG_ERROR_LOAD);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.buildFormGroup();
    this.filter = this.createFilter();
    this.isInvestigador = this.authService.hasAnyAuthority(['EER-AUT-INV-VR']);
    this.isVisor = this.authService.hasAnyAuthority(['EER-EER-V']);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IEmpresaExplotacionResultados>> {
    const empresasInvestigacion$ = this.empresaExplotacionResultadosService.findAll(this.getFindOptions(reset));
    return empresasInvestigacion$.pipe(
      switchMap(responseEmpresas => {
        if (responseEmpresas.total === 0) {
          return of(responseEmpresas);
        }
        const empresasExplotacionResultados = responseEmpresas.items;

        const entidadesIds = new Set<string>(responseEmpresas.items.map(
          (empresaExplotacionResultados) => empresaExplotacionResultados.entidad?.id).filter(ids => ids !== undefined)
        );

        if (entidadesIds.size > 0) {
          return this.empresaService.findAllByIdIn([...entidadesIds]).pipe(
            map((result) => {
              const empresas = result.items;

              empresasExplotacionResultados.forEach((empresaExplotacionResultados) => {
                empresaExplotacionResultados.entidad = empresas.find((empresa) => empresaExplotacionResultados.entidad?.id === empresa.id);
              });

              return responseEmpresas;
            })
          );
        } else {
          return of(responseEmpresas);
        }
      }));
  }

  protected initColumns(): void {
    this.columnas = [
      'identificacionFiscal',
      'nombreRazonSocial',
      'objetoSocial',
      'fechaConstitucion',
      'fechaIncorporacion',
      'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.empresas$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const rsqlFilter = new RSQLSgiRestFilter('nombreRazonSocial', SgiRestFilterOperator.LIKE_ICASE, controls.nombreRazonSocial.value)
      .and('entidadRef', SgiRestFilterOperator.LIKE_ICASE, controls.entidad.value?.id)
      .and('objetoSocial', SgiRestFilterOperator.LIKE_ICASE, controls.objetoSocial.value)
      .and('fechaConstitucion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaConstitucionDesde.value))
      .and('fechaConstitucion', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaConstitucionHasta.value))
      .and('fechaIncorporacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaIncorporacionDesde.value))
      .and('fechaIncorporacion', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaIncorporacionHasta.value));

    return rsqlFilter;
  }

  deactivate(empresa: IEmpresaExplotacionResultados): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar).pipe(
      filter(accept => !!accept),
      switchMap(() => this.empresaExplotacionResultadosService.desactivar(empresa.id))
    ).subscribe(
      () => {
        this.snackBarService.showSuccess(this.textoSuccessDesactivar);
        this.loadTable();
      },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          this.snackBarService.showError(error);
        } else {
          this.snackBarService.showError(this.textoErrorDesactivar);
        }
      }
    );
    this.suscripciones.push(subcription);
  }

  onClearFilters() {
    super.onClearFilters();
    this.buildFormGroup();
    this.onSearch();
  }

  private buildFormGroup() {
    this.formGroup = new FormGroup({
      fechaConstitucionDesde: new FormControl(),
      fechaConstitucionHasta: new FormControl(),
      fechaIncorporacionDesde: new FormControl(),
      fechaIncorporacionHasta: new FormControl(),
      nombreRazonSocial: new FormControl(null),
      entidad: new FormControl(null),
      objetoSocial: new FormControl(null)
    });
  }

  private setupI18N(): void {

    this.translate.get(
      EMPRESA_ENTIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntidadEntity = { entity: value });

    this.translate.get(
      EMPRESA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_ADD,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      EMPRESA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorDelete = value);

    this.translate.get(
      EMPRESA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      EMPRESA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesactivar = value);

    this.translate.get(
      EMPRESA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesactivar = value);
  }

}
