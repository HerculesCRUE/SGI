import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { CategoriaProfesionalService } from '@core/services/sgp/categoria-profesional.service';
import { NivelAcademicosService } from '@core/services/sgp/nivel-academico.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const REQUISITO_IP_KEY = marker('csp.convocatoria-requisito-ip');
const REQ_IP_KEY = marker('csp.convocatoria-req-ip');
const NOMBRE_KEY = marker('sgp.nombre');
const REQUISITO_IP_NUMERO_MAXIMO_KEY = marker('csp.convocatoria-requisito-ip.numero-maximo');
const REQUISITO_IP_EDAD_MAXIMA_KEY = marker('csp.convocatoria-requisito-ip.edad-maxima');
const REQUISITO_IP_SEXO_KEY = marker('csp.convocatoria-requisito-ip.sexo');
const REQUISITO_IP_NIVEL_ACADEMICO_KEY = marker('csp.convocatoria.nivel-academico');
const REQUISITO_IP_FECHA_POSTERIOR_KEY = marker('csp.convocatoria-requisito-ip.fecha-posterior');
const REQUISITO_IP_FECHA_ANTERIOR_KEY = marker('csp.convocatoria-requisito-ip.fecha-anterior');
const REQUISITO_IP_NIVEL_ACADEMICO_VINCULACION_UNIVERSIDAD_KEY = marker('csp.convocatoria-requisito-ip.vinculacion-universidad');
const REQUISITO_IP_CATEGORIA_PROFESIONAL_KEY = marker('csp.convocatoria.categoria-profesional');
const REQUISITO_IP_NUM_MINIMO_COMPETITIVO_KEY = marker('csp.convocatoria-requisito-ip.proyecto-competitivo.minimo');
const REQUISITO_IP_NUM_MINIMO_NO_COMPETITIVO_KEY = marker('csp.convocatoria-requisito-ip.proyecto-no-competitivo.minimo');
const REQUISITO_IP_NUM_MAXIMO_COMPETITIVO_KEY = marker('csp.convocatoria-requisito-ip.proyecto-competitivo.maximo');
const REQUISITO_IP_NUM_MAXIMO_NO_COMPETITIVO_KEY = marker('csp.convocatoria-requisito-ip.proyecto-no-competitivo.maximo');

const REQUISITO_IP_FIELD = 'requisitoIP';
const REQUISITO_IP_NUMERO_MAXIMO_FIELD = 'numeroMaximoReqIP';
const REQUISITO_IP_EDAD_MAXIMA_FIELD = 'edadMaximaReqIP';
const REQUISITO_IP_SEXO_FIELD = 'sexoReqIP';

const REQUISITO_IP_NIVEL_ACADEMICO_FIELD = 'nivelAcademicoIP';
const REQUISITO_IP_NIVEL_ACADEMICO_FECHA_POSTERIOR_FIELD = 'fechaPosteriorNivelAcademicoIP';
const REQUISITO_IP_NIVEL_ACADEMICO_FECHA_ANTERIOR_FIELD = 'fechaAnteriorNivelAcademicoIP';
const REQUISITO_IP_NIVEL_ACADEMICO_VINCULACION_UNIVERSIDAD_FIELD = 'vinculacionUniversidadNivelAcademicoIP';
const REQUISITO_IP_CATEGORIA_PROFESIONAL_FIELD = 'categoriaProfesionalIP';
const REQUISITO_IP_CATEGORIA_PROFESIONAL_FECHA_POSTERIOR_FIELD = 'fechaPosteriorCategoriaProfesionalIP';
const REQUISITO_IP_CATEGORIA_PROFESIONAL_FECHA_ANTERIOR_FIELD = 'fechaAnteriorCategoriaProfesionalIP';
const REQUISITO_IP_NUM_MINIMO_COMPETITIVO_FIELD = 'numMinimoCompetitivoIP';
const REQUISITO_IP_NUM_MINIMO_NO_COMPETITIVO_FIELD = 'numMinimoNoCompetitivoIP';
const REQUISITO_IP_NUM_MAXIMO_COMPETITIVO_FIELD = 'numMaximoCompetitivoIP';
const REQUISITO_IP_NUM_MAXIMO_NO_COMPETITIVO_FIELD = 'numMaximoNoCompetitivoIP';

@Injectable()
export class ConvocatoriaRequisitoIPListadoExportService extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private convocatoriaRequisitoIPService: ConvocatoriaRequisitoIPService,
    private nivelAcademicoService: NivelAcademicosService,
    private categoriaProfesionalService: CategoriaProfesionalService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return this.convocatoriaRequisitoIPService.getRequisitoIPConvocatoria(convocatoriaData?.convocatoria?.id).pipe(
      map(responseRequisitosIP => {
        convocatoriaData.requisitoIP = responseRequisitosIP;
        return convocatoriaData;
      }),
      switchMap(() => {
        if (convocatoriaData.requisitoIP) {
          return this.convocatoriaRequisitoIPService.findNivelesAcademicos(convocatoriaData.requisitoIP.id).pipe(
            map((requisitoIpNivelesAcademicos) => {
              if (requisitoIpNivelesAcademicos.length === 0) {
                return convocatoriaData;
              }
              if (requisitoIpNivelesAcademicos) {
                convocatoriaData.nivelesAcademicos = [];
                requisitoIpNivelesAcademicos.forEach(requisitoIpNivelAcademico => {
                  return this.nivelAcademicoService.findById(requisitoIpNivelAcademico.nivelAcademico.id).subscribe(
                    (nivelAcademico => {
                      const reqNivAcademico = {
                        id: requisitoIpNivelAcademico.id,
                        nivelAcademico,
                        requisitoIP: convocatoriaData.requisitoIP
                      } as IRequisitoIPNivelAcademico;
                      convocatoriaData.nivelesAcademicos.push(reqNivAcademico);
                      return convocatoriaData;
                    })
                  );
                });
              }

              return convocatoriaData;
            })
          );
        } else {
          return of(convocatoriaData);
        }
      }),
      switchMap(() => {
        if (convocatoriaData.requisitoIP) {
          return this.convocatoriaRequisitoIPService.findCategoriaProfesional(convocatoriaData.requisitoIP.id).pipe(
            map((requisitoIpCategorias) => {
              if (requisitoIpCategorias.length === 0) {
                return convocatoriaData;
              }
              if (requisitoIpCategorias) {
                convocatoriaData.categoriasProfesionales = [];
                requisitoIpCategorias.forEach(requisitoIpCategoriaProfesional => {
                  return this.categoriaProfesionalService.findById(requisitoIpCategoriaProfesional.categoriaProfesional.id).subscribe(
                    (categoriaProfesional => {
                      const reqCatProfesional = {
                        id: requisitoIpCategoriaProfesional.id,
                        categoriaProfesional,
                        requisitoIP: convocatoriaData.requisitoIP
                      } as IRequisitoIPCategoriaProfesional;
                      convocatoriaData.categoriasProfesionales.push(reqCatProfesional);
                      return convocatoriaData;
                    })
                  );
                });
              }

              return convocatoriaData;
            })
          );
        } else {
          return of(convocatoriaData);
        }
      })
    );
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsRequisitoIPNotExcel();
    } else {
      return this.getColumnsRequisitoIPExcel(convocatorias);
    }
  }

  private getColumnsRequisitoIPNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: REQUISITO_IP_FIELD,
      title: this.translate.instant(REQUISITO_IP_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(REQUISITO_IP_KEY) +
      ' (' + this.translate.instant(REQUISITO_IP_NUMERO_MAXIMO_KEY) +
      ' - ' + this.translate.instant(REQUISITO_IP_EDAD_MAXIMA_KEY) +
      ' - ' + this.translate.instant(REQUISITO_IP_SEXO_KEY) +
      ')' + '\n' +
      ' (' + this.translate.instant(REQUISITO_IP_NIVEL_ACADEMICO_KEY) + ' ' + this.translate.instant(NOMBRE_KEY) +
      ' - ' + this.translate.instant(REQUISITO_IP_NIVEL_ACADEMICO_KEY) + ' ' + this.translate.instant(REQUISITO_IP_FECHA_POSTERIOR_KEY) +
      ' - ' + this.translate.instant(REQUISITO_IP_NIVEL_ACADEMICO_KEY) + ' ' + this.translate.instant(REQUISITO_IP_FECHA_ANTERIOR_KEY) +
      ' - ' + this.translate.instant(REQUISITO_IP_NIVEL_ACADEMICO_KEY) + ' ' + this.translate.instant(REQUISITO_IP_NIVEL_ACADEMICO_VINCULACION_UNIVERSIDAD_KEY) +
      ')' + '\n' +
      ' (' + this.translate.instant(REQUISITO_IP_CATEGORIA_PROFESIONAL_KEY) + ' ' + this.translate.instant(NOMBRE_KEY) +
      ' - ' + this.translate.instant(REQUISITO_IP_CATEGORIA_PROFESIONAL_KEY) + ' ' + this.translate.instant(REQUISITO_IP_FECHA_POSTERIOR_KEY) +
      ' - ' + this.translate.instant(REQUISITO_IP_CATEGORIA_PROFESIONAL_KEY) + ' ' + this.translate.instant(REQUISITO_IP_FECHA_ANTERIOR_KEY) +
      ')' + '\n' +
      ' (' + this.translate.instant(REQUISITO_IP_NUM_MINIMO_COMPETITIVO_KEY) +
      ' - ' + this.translate.instant(REQUISITO_IP_NUM_MINIMO_NO_COMPETITIVO_KEY) +
      ' - ' + this.translate.instant(REQUISITO_IP_NUM_MAXIMO_COMPETITIVO_KEY) +
      ' - ' + this.translate.instant(REQUISITO_IP_NUM_MAXIMO_NO_COMPETITIVO_KEY) +
      ')';
    const columnEntidad: ISgiColumnReport = {
      name: REQUISITO_IP_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsRequisitoIPExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumNivelesAcademicos = Math.max(...convocatorias.map(c => c.nivelesAcademicos ? c.nivelesAcademicos?.length : 0));
    const maxNumCategoriasProfesionales = Math.max(...convocatorias.map(c => c.categoriasProfesionales ? c.categoriasProfesionales?.length : 0));
    const titleRequisitoIP = this.translate.instant(REQ_IP_KEY);
    const titleNivelAcademico = this.translate.instant(REQUISITO_IP_NIVEL_ACADEMICO_KEY);
    const titleCategoriaProfesional = this.translate.instant(REQUISITO_IP_CATEGORIA_PROFESIONAL_KEY);

    const columnNumMaximoIP: ISgiColumnReport = {
      name: REQUISITO_IP_NUMERO_MAXIMO_FIELD,
      title: titleRequisitoIP + ': ' + this.translate.instant(REQUISITO_IP_NUMERO_MAXIMO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnNumMaximoIP);

    const columnEdadMaxima: ISgiColumnReport = {
      name: REQUISITO_IP_EDAD_MAXIMA_FIELD,
      title: titleRequisitoIP + ': ' + this.translate.instant(REQUISITO_IP_EDAD_MAXIMA_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnEdadMaxima);

    const columnSexo: ISgiColumnReport = {
      name: REQUISITO_IP_SEXO_FIELD,
      title: titleRequisitoIP + ': ' + this.translate.instant(REQUISITO_IP_SEXO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnSexo);

    for (let i = 0; i < maxNumNivelesAcademicos; i++) {
      const idNivelesAcademicos: string = String(i + 1);
      const columnNivelAcademicoIP: ISgiColumnReport = {
        name: REQUISITO_IP_NIVEL_ACADEMICO_FIELD + idNivelesAcademicos,
        title: titleRequisitoIP + ': ' + titleNivelAcademico + idNivelesAcademicos,
        type: ColumnType.STRING,
      };
      columns.push(columnNivelAcademicoIP);
    }

    const columnPosteriorA: ISgiColumnReport = {
      name: REQUISITO_IP_NIVEL_ACADEMICO_FECHA_POSTERIOR_FIELD,
      title: titleRequisitoIP + ': ' + titleNivelAcademico + ' ' + this.translate.instant(REQUISITO_IP_FECHA_POSTERIOR_KEY),
      type: ColumnType.DATE,
    };
    columns.push(columnPosteriorA);

    const columnAnteriorA: ISgiColumnReport = {
      name: REQUISITO_IP_NIVEL_ACADEMICO_FECHA_ANTERIOR_FIELD,
      title: titleRequisitoIP + ': ' + titleNivelAcademico + ' ' + this.translate.instant(REQUISITO_IP_FECHA_ANTERIOR_KEY),
      type: ColumnType.DATE,
    };
    columns.push(columnAnteriorA);

    const columnVinculacion: ISgiColumnReport = {
      name: REQUISITO_IP_NIVEL_ACADEMICO_VINCULACION_UNIVERSIDAD_FIELD,
      title: titleRequisitoIP + ': ' + titleNivelAcademico + ' ' + this.translate.instant(REQUISITO_IP_NIVEL_ACADEMICO_VINCULACION_UNIVERSIDAD_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnVinculacion);

    for (let i = 0; i < maxNumCategoriasProfesionales; i++) {
      const idCategoriaProfesional: string = String(i + 1);
      const columnNivelAcademicoIP: ISgiColumnReport = {
        name: REQUISITO_IP_CATEGORIA_PROFESIONAL_FIELD + idCategoriaProfesional,
        title: titleRequisitoIP + ': ' + titleCategoriaProfesional + idCategoriaProfesional,
        type: ColumnType.STRING,
      };
      columns.push(columnNivelAcademicoIP);
    }

    const columnCatPosteriorA: ISgiColumnReport = {
      name: REQUISITO_IP_CATEGORIA_PROFESIONAL_FECHA_POSTERIOR_FIELD,
      title: titleRequisitoIP + ': ' + titleCategoriaProfesional + ' ' + this.translate.instant(REQUISITO_IP_FECHA_POSTERIOR_KEY),
      type: ColumnType.DATE,
    };
    columns.push(columnCatPosteriorA);

    const columnCatAnteriorA: ISgiColumnReport = {
      name: REQUISITO_IP_CATEGORIA_PROFESIONAL_FECHA_ANTERIOR_FIELD,
      title: titleRequisitoIP + ': ' + titleCategoriaProfesional + ' ' + this.translate.instant(REQUISITO_IP_FECHA_ANTERIOR_KEY),
      type: ColumnType.DATE,
    };
    columns.push(columnCatAnteriorA);

    const columnNumMinCompetitivos: ISgiColumnReport = {
      name: REQUISITO_IP_NUM_MINIMO_COMPETITIVO_FIELD,
      title: titleRequisitoIP + ': ' + this.translate.instant(REQUISITO_IP_NUM_MINIMO_COMPETITIVO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnNumMinCompetitivos);

    const columnNumMinNoCompetitivos: ISgiColumnReport = {
      name: REQUISITO_IP_NUM_MINIMO_NO_COMPETITIVO_FIELD,
      title: titleRequisitoIP + ': ' + this.translate.instant(REQUISITO_IP_NUM_MINIMO_NO_COMPETITIVO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnNumMinNoCompetitivos);

    const columnNumMaxCompetitivos: ISgiColumnReport = {
      name: REQUISITO_IP_NUM_MAXIMO_COMPETITIVO_FIELD,
      title: titleRequisitoIP + ': ' + this.translate.instant(REQUISITO_IP_NUM_MAXIMO_COMPETITIVO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnNumMaxCompetitivos);

    const columnNumMaxNoCompetitivos: ISgiColumnReport = {
      name: REQUISITO_IP_NUM_MAXIMO_NO_COMPETITIVO_FIELD,
      title: titleRequisitoIP + ': ' + this.translate.instant(REQUISITO_IP_NUM_MAXIMO_NO_COMPETITIVO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnNumMaxNoCompetitivos);

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsRequisitoIPNotExcel(convocatoria, elementsRow);
    } else {
      this.fillRowsEntidadExcel1(elementsRow, convocatoria.requisitoIP);
      const maxNumNivelesAcademicos = Math.max(...convocatorias.map(c => c.nivelesAcademicos ? c.nivelesAcademicos?.length : 0));
      for (let i = 0; i < maxNumNivelesAcademicos; i++) {
        const nivelAcademico = convocatoria.nivelesAcademicos ? convocatoria.nivelesAcademicos[i] ?? null : null;
        this.fillRowsEntidadExcelNivelAcademico(elementsRow, nivelAcademico);
      }
      this.fillRowsEntidadExcelNivelAcademicoFechas(elementsRow, convocatoria.requisitoIP);
      const maxNumCategoriasProfesionales = Math.max(...convocatorias.map(c => c.categoriasProfesionales ? c.categoriasProfesionales?.length : 0));
      for (let i = 0; i < maxNumCategoriasProfesionales; i++) {
        const categoriaProfesional = convocatoria.categoriasProfesionales ? convocatoria.categoriasProfesionales[i] ?? null : null;
        this.fillRowsEntidadExcelCategoriaProfesional(elementsRow, categoriaProfesional);
      }
      this.fillRowsEntidadExcelCategoriaProfesionalFechas(elementsRow, convocatoria.requisitoIP);
      this.fillRowsEntidadExcel2(elementsRow, convocatoria.requisitoIP);
    }
    return elementsRow;
  }

  private fillRowsRequisitoIPNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];
    if (convocatoria.requisitoIP) {
      const requisitoIPElementsRow: any[] = [];
      let requisitoIPContent = convocatoria?.requisitoIP.numMaximoIP ? convocatoria.requisitoIP?.numMaximoIP.toString() ?? '' : '';
      requisitoIPContent += '\n';
      requisitoIPContent += convocatoria?.requisitoIP.edadMaxima ? convocatoria.requisitoIP?.edadMaxima.toString() ?? '' : '';
      requisitoIPContent += '\n';
      requisitoIPContent += convocatoria?.requisitoIP.sexo ? convocatoria.requisitoIP?.sexo.id ?? '' : '';

      convocatoria.nivelesAcademicos?.filter(n => n.requisitoIP.id === convocatoria.requisitoIP.id).forEach((nivelAcademicoIP, index) => {
        requisitoIPContent += '\n';
        requisitoIPContent += (this.translate.instant(REQUISITO_IP_NIVEL_ACADEMICO_KEY) + (index + 1) + ': ') + (nivelAcademicoIP.nivelAcademico ? nivelAcademicoIP.nivelAcademico.nombre ?? '' : '');
      });

      requisitoIPContent += '\n';
      requisitoIPContent += convocatoria?.requisitoIP.fechaMinimaNivelAcademico
        ? this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoria.requisitoIP.fechaMinimaNivelAcademico, true), 'shortDate') ?? '' : '';
      requisitoIPContent += '\n';
      requisitoIPContent += convocatoria.requisitoIP.fechaMaximaNivelAcademico
        ? this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoria.requisitoIP.fechaMaximaNivelAcademico, true), 'shortDate') ?? '' : '';
      requisitoIPContent += '\n';
      requisitoIPContent += this.notIsNullAndNotUndefined(convocatoria.requisitoIP.vinculacionUniversidad)
        ? this.getI18nBooleanYesNo(convocatoria.requisitoIP.vinculacionUniversidad) ?? '' : '';

      convocatoria.categoriasProfesionales?.filter(c => c.requisitoIP.id === convocatoria.requisitoIP.id).forEach((categoriaProfesionalIP, index) => {
        requisitoIPContent += '\n';
        requisitoIPContent += (this.translate.instant(REQUISITO_IP_CATEGORIA_PROFESIONAL_KEY) + (index + 1) + ': ') + (categoriaProfesionalIP.categoriaProfesional ? categoriaProfesionalIP.categoriaProfesional.nombre ?? '' : '');
      });

      requisitoIPContent += '\n';
      requisitoIPContent += convocatoria.requisitoIP.fechaMinimaCategoriaProfesional
        ? this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoria.requisitoIP.fechaMinimaCategoriaProfesional, true), 'shortDate') ?? '' : '';
      requisitoIPContent += '\n';
      requisitoIPContent += convocatoria.requisitoIP.fechaMaximaCategoriaProfesional
        ? this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoria.requisitoIP.fechaMaximaCategoriaProfesional, true), 'shortDate') ?? '' : '';

      requisitoIPContent += '\n';
      requisitoIPContent += convocatoria?.requisitoIP && convocatoria.requisitoIP?.numMinimoCompetitivos
        ? convocatoria.requisitoIP?.numMinimoCompetitivos.toString() ?? '' : '';
      requisitoIPContent += '\n';
      requisitoIPContent += convocatoria?.requisitoIP && convocatoria.requisitoIP?.numMinimoNoCompetitivos
        ? convocatoria.requisitoIP?.numMinimoNoCompetitivos.toString() ?? '' : '';
      requisitoIPContent += '\n';
      requisitoIPContent += convocatoria?.requisitoIP && convocatoria.requisitoIP?.numMaximoCompetitivosActivos
        ? convocatoria.requisitoIP?.numMaximoCompetitivosActivos.toString() ?? '' : '';
      requisitoIPContent += '\n';
      requisitoIPContent += convocatoria?.requisitoIP && convocatoria.requisitoIP?.numMaximoNoCompetitivosActivos
        ? convocatoria.requisitoIP?.numMaximoNoCompetitivosActivos.toString() ?? '' : '';

      requisitoIPElementsRow.push(requisitoIPContent);

      const rowReport: ISgiRowReport = {
        elements: requisitoIPElementsRow
      };
      rowsReport.push(rowReport);
    }
    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel1(elementsRow: any[], requisitosIP: IConvocatoriaRequisitoIP) {
    if (requisitosIP) {
      elementsRow.push(requisitosIP.numMaximoIP ? requisitosIP.numMaximoIP.toString() ?? '' : '');
      elementsRow.push(requisitosIP.edadMaxima ? requisitosIP.edadMaxima.toString() ?? '' : '');
      elementsRow.push(requisitosIP.sexo ? requisitosIP.sexo.id ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcel2(elementsRow: any[], requisitosIP: IConvocatoriaRequisitoIP) {
    if (requisitosIP) {
      elementsRow.push(requisitosIP.numMinimoCompetitivos ? requisitosIP.numMinimoCompetitivos.toString() ?? '' : '');
      elementsRow.push(requisitosIP.numMinimoNoCompetitivos ? requisitosIP.numMinimoNoCompetitivos.toString() ?? '' : '');
      elementsRow.push(requisitosIP.numMaximoCompetitivosActivos ? requisitosIP.numMaximoCompetitivosActivos.toString() ?? '' : '');
      elementsRow.push(requisitosIP.numMaximoNoCompetitivosActivos ? requisitosIP.numMaximoNoCompetitivosActivos.toString() ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcelNivelAcademico(elementsRow: any[], nivelAcademicoIP: IRequisitoIPNivelAcademico) {
    if (nivelAcademicoIP) {
      elementsRow.push(nivelAcademicoIP.nivelAcademico ? nivelAcademicoIP.nivelAcademico.nombre ?? '' : '');
    } else {
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcelNivelAcademicoFechas(elementsRow: any[], requisitosIP: IConvocatoriaRequisitoIP) {
    if (requisitosIP) {
      elementsRow.push(requisitosIP.fechaMinimaNivelAcademico
        ? LuxonUtils.toBackend(requisitosIP.fechaMinimaNivelAcademico) ?? '' : '');
      elementsRow.push(requisitosIP.fechaMaximaNivelAcademico
        ? LuxonUtils.toBackend(requisitosIP.fechaMaximaNivelAcademico) ?? '' : '');
      elementsRow.push(this.notIsNullAndNotUndefined(requisitosIP.vinculacionUniversidad)
        ? this.getI18nBooleanYesNo(requisitosIP.vinculacionUniversidad) ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcelCategoriaProfesional(elementsRow: any[], categoriaProfesionalIP: IRequisitoIPCategoriaProfesional) {
    if (categoriaProfesionalIP) {
      elementsRow.push(categoriaProfesionalIP.categoriaProfesional ? categoriaProfesionalIP.categoriaProfesional.nombre ?? '' : '');
    } else {
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcelCategoriaProfesionalFechas(elementsRow: any[], requisitosIP: IConvocatoriaRequisitoIP) {
    if (requisitosIP) {
      elementsRow.push(requisitosIP.fechaMinimaCategoriaProfesional
        ? LuxonUtils.toBackend(requisitosIP.fechaMinimaCategoriaProfesional) ?? '' : '');
      elementsRow.push(requisitosIP.fechaMaximaCategoriaProfesional
        ? LuxonUtils.toBackend(requisitosIP.fechaMaximaCategoriaProfesional) ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private notIsNullAndNotUndefined(value): boolean {
    return value !== null && value !== undefined;
  }
}
