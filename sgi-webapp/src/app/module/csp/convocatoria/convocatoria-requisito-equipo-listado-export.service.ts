import { DataSource } from '@angular/cdk/collections';
import { DecimalPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ConvocatoriaRequisitoEquipoService } from '@core/services/csp/convocatoria-requisito-equipo.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { CategoriaProfesionalService } from '@core/services/sgp/categoria-profesional.service';
import { NivelAcademicosService } from '@core/services/sgp/nivel-academico.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const COLUMN_VALUE_PREFIX = ': ';

const REQUISITO_EQUIPO_KEY = marker('csp.convocatoria-requisito-equipo');
const REQ_EQ_KEY = marker('csp.convocatoria-req-equipo');
const NOMBRE_KEY = marker('sgp.nombre');
const REQUISITO_EQUIPO_SEXO_RATIO_KEY = marker('csp.convocatoria-requisito-equipo.sexo-ratio');
const REQUISITO_EQUIPO_EDAD_MAXIMA_KEY = marker('csp.convocatoria-requisito-equipo.nivel-academico.edad-maxima');
const REQUISITO_EQUIPO_SEXO_KEY = marker('csp.convocatoria-requisito-equipo.sexo');
const REQUISITO_EQUIPO_NIVEL_ACADEMICO_KEY = marker('csp.convocatoria.nivel-academico');
const REQUISITO_EQUIPO_FECHA_POSTERIOR_KEY = marker('csp.convocatoria-requisito-equipo.fecha-posterior');
const REQUISITO_EQUIPO_FECHA_ANTERIOR_KEY = marker('csp.convocatoria-requisito-equipo.fecha-anterior');
const REQUISITO_EQUIPO_NIVEL_ACADEMICO_VINCULACION_UNIVERSIDAD_KEY = marker('csp.convocatoria-requisito-equipo.vinculacion-universidad');
const REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_KEY = marker('csp.convocatoria.categoria-profesional');
const REQUISITO_EQUIPO_NUM_MINIMO_COMPETITIVO_KEY = marker('csp.convocatoria-requisito-equipo.proyectos-competitivos.minimo');
const REQUISITO_EQUIPO_NUM_MINIMO_NO_COMPETITIVO_KEY = marker('csp.convocatoria-requisito-equipo.proyectos-no-competitivos.minimo');
const REQUISITO_EQUIPO_NUM_MAXIMO_COMPETITIVO_KEY = marker('csp.convocatoria-requisito-equipo.proyectos-competitivos.maximo');
const REQUISITO_EQUIPO_NUM_MAXIMO_NO_COMPETITIVO_KEY = marker('csp.convocatoria-requisito-equipo.proyectos-no-competitivos.maximo');

const REQUISITO_EQUIPO_FIELD = 'requisitoEquipo';
const REQUISITO_EQUIPO_SEXO_RATIO_FIELD = 'sexoRatioReqEq';
const REQUISITO_EQUIPO_EDAD_MAXIMA_FIELD = 'edadMaximaReqEq';
const REQUISITO_EQUIPO_SEXO_FIELD = 'sexoReqEq';

const REQUISITO_EQUIPO_NIVEL_ACADEMICO_FIELD = 'nivelAcademicoEq';
const REQUISITO_EQUIPO_NIVEL_ACADEMICO_FECHA_POSTERIOR_FIELD = 'fechaPosteriorNivelAcademicoEq';
const REQUISITO_EQUIPO_NIVEL_ACADEMICO_FECHA_ANTERIOR_FIELD = 'fechaAnteriorNivelAcademicoEq';
const REQUISITO_EQUIPO_NIVEL_ACADEMICO_VINCULACION_UNIVERSIDAD_FIELD = 'vinculacionUniversidadNivelAcademicoEq';
const REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_FIELD = 'categoriaProfesionalEq';
const REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_FECHA_POSTERIOR_FIELD = 'fechaPosteriorCategoriaProfesionalEq';
const REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_FECHA_ANTERIOR_FIELD = 'fechaAnteriorCategoriaProfesionalEq';
const REQUISITO_EQUIPO_NUM_MINIMO_COMPETITIVO_FIELD = 'numMinimoCompetitivoEq';
const REQUISITO_EQUIPO_NUM_MINIMO_NO_COMPETITIVO_FIELD = 'numMinimoNoCompetitivoEq';
const REQUISITO_EQUIPO_NUM_MAXIMO_COMPETITIVO_FIELD = 'numMaximoCompetitivoEq';
const REQUISITO_EQUIPO_NUM_MAXIMO_NO_COMPETITIVO_FIELD = 'numMaximoNoCompetitivoEq';

@Injectable()
export class ConvocatoriaRequisitoEquipoListadoExportService extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoService,
    private nivelAcademicoService: NivelAcademicosService,
    private categoriaProfesionalService: CategoriaProfesionalService,
    private decimalPipe: DecimalPipe
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return this.convocatoriaRequisitoEquipoService.findByConvocatoriaId(convocatoriaData?.convocatoria?.id).pipe(
      map(responseRequisitosIP => {
        convocatoriaData.requisitoEquipo = responseRequisitosIP;
        return convocatoriaData;
      }),
      switchMap((data) => {
        if (!data.requisitoEquipo) {
          return of(data);
        }
        return this.convocatoriaRequisitoEquipoService.findNivelesAcademicos(data.requisitoEquipo.id).pipe(
          mergeMap((requisitoEquipoNivelesAcademicos) => this.getNivelesAcademicos(data, requisitoEquipoNivelesAcademicos))
        );
      }),
      switchMap((data) => {
        if (!convocatoriaData.requisitoEquipo) {
          return of(data);
        }
        return this.convocatoriaRequisitoEquipoService.findCategoriaProfesional(convocatoriaData.requisitoEquipo.id).pipe(
          mergeMap((requisitoEquipoCategorias) => this.getCategoriasProfesionales(data, requisitoEquipoCategorias))
        );
      })
    );
  }

  private getCategoriasProfesionales(data: IConvocatoriaReportData, requisitoEquipoCategorias: IRequisitoEquipoCategoriaProfesional[]):
    Observable<IConvocatoriaReportData> {

    if (!requisitoEquipoCategorias || requisitoEquipoCategorias.length === 0) {
      return of(data);
    }

    data.categoriasProfesionalesEquipo = [];

    return from(requisitoEquipoCategorias).pipe(
      mergeMap(requisitoEquipoCategoriaProfesional => {
        return this.categoriaProfesionalService.findById(requisitoEquipoCategoriaProfesional.categoriaProfesional.id).pipe(
          map(categoriaProfesional => {
            const reqCatProfesional = {
              id: requisitoEquipoCategoriaProfesional.id,
              categoriaProfesional,
              requisitoEquipo: data.requisitoEquipo
            } as IRequisitoEquipoCategoriaProfesional;
            data.categoriasProfesionalesEquipo.push(reqCatProfesional);
            return data;
          })
        );
      }));
  }

  private getNivelesAcademicos(data: IConvocatoriaReportData, requisitoEquipoNivelesAcademicos: IRequisitoEquipoNivelAcademico[]):
    Observable<IConvocatoriaReportData> {
    if (!requisitoEquipoNivelesAcademicos || requisitoEquipoNivelesAcademicos.length === 0) {
      return of(data);
    }

    data.nivelesAcademicosEquipo = [];
    return from(requisitoEquipoNivelesAcademicos).pipe(
      mergeMap(requisitoEquipoNivelAcademico => {
        return this.nivelAcademicoService.findById(requisitoEquipoNivelAcademico.nivelAcademico.id).pipe(
          map(nivelAcademico => {
            const reqNivAcademico = {
              id: requisitoEquipoNivelAcademico.id,
              nivelAcademico,
              requisitoEquipo: data.requisitoEquipo
            } as IRequisitoEquipoNivelAcademico;
            data.nivelesAcademicosEquipo.push(reqNivAcademico);
            return data;
          })
        );
      }));
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsRequisitoEquipoNotExcel(convocatorias);
    } else {
      const requisitosEquipoTitlePrefix = this.translate.instant(REQUISITO_EQUIPO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) + ': ';
      return this.getColumnsRequisitoEquipo(requisitosEquipoTitlePrefix, false, convocatorias);
    }
  }

  private getColumnsRequisitoEquipoNotExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = this.getColumnsRequisitoEquipo('', true, convocatorias);

    const titleI18n = this.translate.instant(REQUISITO_EQUIPO_KEY);

    const columnEntidad: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsRequisitoEquipo(prefix: string, allString: boolean, convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumNivelesAcademicosEquipo = Math.max(...convocatorias.map(c => c.nivelesAcademicosEquipo ? c.nivelesAcademicosEquipo?.length : 0));
    const maxNumCategoriasProfesionalesEquipo = Math.max(...convocatorias.map(c => c.categoriasProfesionalesEquipo ? c.categoriasProfesionalesEquipo?.length : 0));

    const titleNivelAcademico = this.translate.instant(REQUISITO_EQUIPO_NIVEL_ACADEMICO_KEY);
    const titleCategoriaProfesional = this.translate.instant(REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_KEY);

    const columnEdadMaxima: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_EDAD_MAXIMA_FIELD,
      title: prefix + this.translate.instant(REQUISITO_EQUIPO_EDAD_MAXIMA_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnEdadMaxima);

    const columnSexo: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_SEXO_FIELD,
      title: prefix + this.translate.instant(REQUISITO_EQUIPO_SEXO_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnSexo);

    const columnSexoRatio: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_SEXO_RATIO_FIELD,
      title: prefix + this.translate.instant(REQUISITO_EQUIPO_SEXO_RATIO_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.NUMBER,
    };
    columns.push(columnSexoRatio);

    for (let i = 0; i < maxNumNivelesAcademicosEquipo; i++) {
      const idNivelesAcademicos: string = String(i + 1);
      const columnNivelAcademicoIP: ISgiColumnReport = {
        name: REQUISITO_EQUIPO_NIVEL_ACADEMICO_FIELD + idNivelesAcademicos,
        title: prefix + titleNivelAcademico + idNivelesAcademicos + this.getValuePrefix(prefix),
        type: ColumnType.STRING,
      };
      columns.push(columnNivelAcademicoIP);
    }

    const columnPosteriorA: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_NIVEL_ACADEMICO_FECHA_POSTERIOR_FIELD,
      title: prefix + titleNivelAcademico + ' ' + this.translate.instant(REQUISITO_EQUIPO_FECHA_POSTERIOR_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.DATE,
    };
    columns.push(columnPosteriorA);

    const columnAnteriorA: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_NIVEL_ACADEMICO_FECHA_ANTERIOR_FIELD,
      title: prefix + titleNivelAcademico + ' ' + this.translate.instant(REQUISITO_EQUIPO_FECHA_ANTERIOR_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.DATE,
    };
    columns.push(columnAnteriorA);

    const columnVinculacion: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_NIVEL_ACADEMICO_VINCULACION_UNIVERSIDAD_FIELD,
      title: prefix + titleNivelAcademico + ' ' + this.translate.instant(REQUISITO_EQUIPO_NIVEL_ACADEMICO_VINCULACION_UNIVERSIDAD_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnVinculacion);

    for (let i = 0; i < maxNumCategoriasProfesionalesEquipo; i++) {
      const idCategoriaProfesional: string = String(i + 1);
      const columnNivelAcademicoIP: ISgiColumnReport = {
        name: REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_FIELD + idCategoriaProfesional,
        title: prefix + titleCategoriaProfesional + idCategoriaProfesional + this.getValuePrefix(prefix),
        type: ColumnType.STRING,
      };
      columns.push(columnNivelAcademicoIP);
    }

    const columnCatPosteriorA: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_FECHA_POSTERIOR_FIELD,
      title: prefix + titleCategoriaProfesional + ' ' + this.translate.instant(REQUISITO_EQUIPO_FECHA_POSTERIOR_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.DATE,
    };
    columns.push(columnCatPosteriorA);

    const columnCatAnteriorA: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_FECHA_ANTERIOR_FIELD,
      title: prefix + titleCategoriaProfesional + ' ' + this.translate.instant(REQUISITO_EQUIPO_FECHA_ANTERIOR_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.DATE,
    };
    columns.push(columnCatAnteriorA);

    const columnNumMinCompetitivos: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_NUM_MINIMO_COMPETITIVO_FIELD,
      title: prefix + this.translate.instant(REQUISITO_EQUIPO_NUM_MINIMO_COMPETITIVO_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.NUMBER,
    };
    columns.push(columnNumMinCompetitivos);

    const columnNumMinNoCompetitivos: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_NUM_MINIMO_NO_COMPETITIVO_FIELD,
      title: prefix + this.translate.instant(REQUISITO_EQUIPO_NUM_MINIMO_NO_COMPETITIVO_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.NUMBER,
    };
    columns.push(columnNumMinNoCompetitivos);

    const columnNumMaxCompetitivos: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_NUM_MAXIMO_COMPETITIVO_FIELD,
      title: prefix + this.translate.instant(REQUISITO_EQUIPO_NUM_MAXIMO_COMPETITIVO_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.NUMBER,
    };
    columns.push(columnNumMaxCompetitivos);

    const columnNumMaxNoCompetitivos: ISgiColumnReport = {
      name: REQUISITO_EQUIPO_NUM_MAXIMO_NO_COMPETITIVO_FIELD,
      title: prefix + this.translate.instant(REQUISITO_EQUIPO_NUM_MAXIMO_NO_COMPETITIVO_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.NUMBER,
    };
    columns.push(columnNumMaxNoCompetitivos);

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsRequisitoEquipoNotExcel(convocatoria, elementsRow);
    } else {
      this.fillRowsEntidadExcel1(elementsRow, convocatoria.requisitoEquipo);
      const maxNumNivelesAcademicosEquipo = Math.max(...convocatorias.map(c => c.nivelesAcademicosEquipo ? c.nivelesAcademicosEquipo?.length : 0));
      for (let i = 0; i < maxNumNivelesAcademicosEquipo; i++) {
        const nivelAcademicoEquipo = convocatoria.nivelesAcademicosEquipo ? convocatoria.nivelesAcademicosEquipo[i] ?? null : null;
        this.fillRowsEntidadExcelNivelAcademico(elementsRow, nivelAcademicoEquipo);
      }
      this.fillRowsEntidadExcelNivelAcademicoFechas(elementsRow, convocatoria.requisitoEquipo);
      const maxNumCategoriasProfesionalesEquipo = Math.max(...convocatorias.map(c => c.categoriasProfesionalesEquipo ? c.categoriasProfesionalesEquipo?.length : 0));
      for (let i = 0; i < maxNumCategoriasProfesionalesEquipo; i++) {
        const categoriaProfesionalEquipo = convocatoria.categoriasProfesionalesEquipo ? convocatoria.categoriasProfesionalesEquipo[i] ?? null : null;
        this.fillRowsEntidadExcelCategoriaProfesional(elementsRow, categoriaProfesionalEquipo);
      }
      this.fillRowsEntidadExcelCategoriaProfesionalFechas(elementsRow, convocatoria.requisitoEquipo);
      this.fillRowsEntidadExcel2(elementsRow, convocatoria.requisitoEquipo);
    }
    return elementsRow;
  }

  private fillRowsRequisitoEquipoNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];
    if (convocatoria.requisitoEquipo) {
      const requisitoEquipoElementsRow: any[] = [];
      requisitoEquipoElementsRow.push(convocatoria?.requisitoEquipo.edadMaxima ? convocatoria.requisitoEquipo?.edadMaxima.toString() ?? '' : '');
      requisitoEquipoElementsRow.push(convocatoria?.requisitoEquipo.sexo ? convocatoria.requisitoEquipo?.sexo.id ?? '' : '');
      requisitoEquipoElementsRow.push(convocatoria?.requisitoEquipo.ratioSexo ? this.decimalPipe.transform(convocatoria?.requisitoEquipo.ratioSexo, '2.2-2') ?? '' : '');

      convocatoria.nivelesAcademicosEquipo?.filter(n => n.requisitoEquipo.id === convocatoria.requisitoEquipo.id).forEach((nivelAcademicoEquipo, index) => {
        requisitoEquipoElementsRow.push((nivelAcademicoEquipo.nivelAcademico ? nivelAcademicoEquipo.nivelAcademico.nombre ?? '' : ''));
      });


      requisitoEquipoElementsRow.push(convocatoria.requisitoEquipo.fechaMinimaNivelAcademico
        ? this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoria.requisitoEquipo.fechaMinimaNivelAcademico, true), 'shortDate') ?? '' : '');

      requisitoEquipoElementsRow.push(convocatoria.requisitoEquipo.fechaMaximaNivelAcademico
        ? this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoria.requisitoEquipo.fechaMaximaNivelAcademico, true), 'shortDate') ?? '' : '');

      requisitoEquipoElementsRow.push(this.notIsNullAndNotUndefined(convocatoria.requisitoEquipo.vinculacionUniversidad)
        ? this.getI18nBooleanYesNo(convocatoria.requisitoEquipo.vinculacionUniversidad) ?? '' : '');

      convocatoria.categoriasProfesionalesEquipo?.filter(c => c.requisitoEquipo.id === convocatoria.requisitoEquipo.id).forEach((categoriaProfesionalEquipo, index) => {

        requisitoEquipoElementsRow.push((categoriaProfesionalEquipo.categoriaProfesional ? categoriaProfesionalEquipo.categoriaProfesional.nombre ?? '' : ''));
      });


      requisitoEquipoElementsRow.push(convocatoria.requisitoEquipo.fechaMinimaCategoriaProfesional
        ? this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoria.requisitoEquipo.fechaMinimaCategoriaProfesional, true), 'shortDate') ?? '' : '');

      requisitoEquipoElementsRow.push(convocatoria.requisitoEquipo.fechaMaximaCategoriaProfesional
        ? this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoria.requisitoEquipo.fechaMaximaCategoriaProfesional, true), 'shortDate') ?? '' : '');


      requisitoEquipoElementsRow.push(convocatoria?.requisitoEquipo && convocatoria.requisitoEquipo?.numMinimoCompetitivos
        ? convocatoria.requisitoEquipo?.numMinimoCompetitivos.toString() ?? '' : '');

      requisitoEquipoElementsRow.push(convocatoria?.requisitoEquipo && convocatoria.requisitoEquipo?.numMinimoNoCompetitivos
        ? convocatoria.requisitoEquipo?.numMinimoNoCompetitivos.toString() ?? '' : '');

      requisitoEquipoElementsRow.push(convocatoria?.requisitoEquipo && convocatoria.requisitoEquipo?.numMaximoCompetitivosActivos
        ? convocatoria.requisitoEquipo?.numMaximoCompetitivosActivos.toString() ?? '' : '');

      requisitoEquipoElementsRow.push(convocatoria?.requisitoEquipo && convocatoria.requisitoEquipo?.numMaximoNoCompetitivosActivos
        ? convocatoria.requisitoEquipo?.numMaximoNoCompetitivosActivos.toString() ?? '' : '');

      const rowReport: ISgiRowReport = {
        elements: requisitoEquipoElementsRow
      };
      rowsReport.push(rowReport);
    }
    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel1(elementsRow: any[], requisitosEquipo: IConvocatoriaRequisitoEquipo) {
    if (requisitosEquipo) {
      elementsRow.push(requisitosEquipo.edadMaxima ? requisitosEquipo.edadMaxima.toString() ?? '' : '');
      elementsRow.push(requisitosEquipo.sexo ? requisitosEquipo.sexo.id ?? '' : '');
      elementsRow.push(requisitosEquipo.ratioSexo ? requisitosEquipo.ratioSexo ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcel2(elementsRow: any[], requisitosEquipo: IConvocatoriaRequisitoEquipo) {
    if (requisitosEquipo) {
      elementsRow.push(requisitosEquipo.numMinimoCompetitivos ?? '');
      elementsRow.push(requisitosEquipo.numMinimoNoCompetitivos ?? '');
      elementsRow.push(requisitosEquipo.numMaximoCompetitivosActivos ?? '');
      elementsRow.push(requisitosEquipo.numMaximoNoCompetitivosActivos ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcelNivelAcademico(elementsRow: any[], nivelAcademicoEquipo: IRequisitoEquipoNivelAcademico) {
    if (nivelAcademicoEquipo) {
      elementsRow.push(nivelAcademicoEquipo.nivelAcademico ? nivelAcademicoEquipo.nivelAcademico.nombre ?? '' : '');
    } else {
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcelNivelAcademicoFechas(elementsRow: any[], requisitosEquipo: IConvocatoriaRequisitoEquipo) {
    if (requisitosEquipo) {
      elementsRow.push(requisitosEquipo.fechaMinimaNivelAcademico
        ? LuxonUtils.toBackend(requisitosEquipo.fechaMinimaNivelAcademico) ?? '' : '');
      elementsRow.push(requisitosEquipo.fechaMaximaNivelAcademico
        ? LuxonUtils.toBackend(requisitosEquipo.fechaMaximaNivelAcademico) ?? '' : '');
      elementsRow.push(this.notIsNullAndNotUndefined(requisitosEquipo.vinculacionUniversidad)
        ? this.getI18nBooleanYesNo(requisitosEquipo.vinculacionUniversidad) ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcelCategoriaProfesional(elementsRow: any[], categoriaProfesionalEquipo: IRequisitoEquipoCategoriaProfesional) {
    if (categoriaProfesionalEquipo) {
      elementsRow.push(categoriaProfesionalEquipo.categoriaProfesional ? categoriaProfesionalEquipo.categoriaProfesional.nombre ?? '' : '');
    } else {
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcelCategoriaProfesionalFechas(elementsRow: any[], requisitosEquipo: IConvocatoriaRequisitoEquipo) {
    if (requisitosEquipo) {
      elementsRow.push(requisitosEquipo.fechaMinimaCategoriaProfesional
        ? LuxonUtils.toBackend(requisitosEquipo.fechaMinimaCategoriaProfesional) ?? '' : '');
      elementsRow.push(requisitosEquipo.fechaMaximaCategoriaProfesional
        ? LuxonUtils.toBackend(requisitosEquipo.fechaMaximaCategoriaProfesional) ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getValuePrefix(prefix: string): string {
    if (!prefix) {
      return COLUMN_VALUE_PREFIX;
    }
    return '';
  }
}
