import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { VALIDACION_REQUISITOS_EQUIPO_IP_MAP } from '@core/enums/validaciones-requisitos-equipo-ip';
import { MSG_PARAMS } from '@core/i18n';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoPublicService } from '@core/services/sgdoc/documento-public.service';
import { triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { forkJoin, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent, SolicitudRrhhAcreditarCategoriaProfesionalPublicModalData } from '../../modals/solicitud-rrhh-acreditar-categoria-profesional-public/solicitud-rrhh-acreditar-categoria-profesional-public-modal.component';
import { SolicitudRrhhAcreditarNivelAcademicoPublicModalComponent, SolicitudRrhhAcreditarNivelAcademicoPublicModalData } from '../../modals/solicitud-rrhh-acreditar-nivel-academico-public/solicitud-rrhh-acreditar-nivel-academico-public-modal.component';
import { SolicitudPublicActionService } from '../../solicitud-public.action.service';
import {
  RequisitoCategoriaProfesionalExigido,
  RequisitoCategoriaProfesionalExigidoAndAcreditado,
  RequisitoNivelAcademicoExigido,
  RequisitoNivelAcademicoExigidoAndAcreditado,
  SolicitudRrhhRequisitosConvocatoriaPublicFragment
} from './solicitud-rrhh-requisitos-convocatoria-public.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const SOLICITUD_RRHH_REQUISITOS_ACREDITACION_KEY = marker('csp.solicitud.solicitud-rrhh.requisitos.acreditacion');

@Component({
  selector: 'sgi-solicitud-rrhh-requisitos-convocatoria-public',
  templateUrl: './solicitud-rrhh-requisitos-convocatoria-public.component.html',
  styleUrls: ['./solicitud-rrhh-requisitos-convocatoria-public.component.scss']
})
export class SolicitudRrhhRequisitosConvocatoriaPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudRrhhRequisitosConvocatoriaPublicFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumnsCategoriasProfesionalesSolicitante = ['helpIcon', 'categoriaProfesional', 'fechaMinima', 'fechaMaxima', 'documento', 'acciones'];
  displayedColumnsCategoriasProfesionalesTutor = ['helpIcon', 'categoriaProfesional', 'fechaMinima', 'fechaMaxima'];
  displayedColumnsNivelesAcademicosSolicitante = ['helpIcon', 'nivelAcademico', 'fechaMinima', 'fechaMaxima', 'documento', 'acciones'];
  displayedColumnsNivelesAcademicosTutor = ['helpIcon', 'nivelAcademico', 'fechaMinima', 'fechaMaxima'];
  elementsPage = [5, 10, 25, 100];

  msgParamEntity = {};
  msgDeleteAcreditacion: string;

  dataSourceCategoriasProfesionalesSolicitante = new MatTableDataSource<StatusWrapper<RequisitoCategoriaProfesionalExigidoAndAcreditado>>();
  dataSourceCategoriasProfesionalesTutor = new MatTableDataSource<StatusWrapper<RequisitoCategoriaProfesionalExigido>>();
  dataSourceNivelesAcademicosSolicitante = new MatTableDataSource<StatusWrapper<RequisitoNivelAcademicoExigidoAndAcreditado>>();
  dataSourceNivelesAcademicosTutor = new MatTableDataSource<StatusWrapper<RequisitoNivelAcademicoExigido>>();
  @ViewChild('sortCategoriasProfesionalesSolicitante', { static: true }) sortCategoriasProfesionalesSolicitante: MatSort;
  @ViewChild('sortCategoriasProfesionalesTutor', { static: true }) sortCategoriasProfesionalesTutor: MatSort;
  @ViewChild('sortNivelesAcademicosSolicitante', { static: true }) sortNivelesAcademicosSolicitante: MatSort;
  @ViewChild('sortNivelesAcademicosTutor', { static: true }) sortNivelesAcademicosTutor: MatSort;

  get VALIDACION_REQUISITOS_EQUIPO_IP_MAP() {
    return VALIDACION_REQUISITOS_EQUIPO_IP_MAP;
  }

  constructor(
    private actionService: SolicitudPublicActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    private readonly documentoService: DocumentoPublicService,
    private snackBarService: SnackBarService
  ) {
    super(actionService.FRAGMENT.REQUISITOS_CONVOCATORIA, actionService);
    this.formPart = this.fragment as SolicitudRrhhRequisitosConvocatoriaPublicFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();


    this.dataSourceCategoriasProfesionalesSolicitante.sortingDataAccessor =
      (wrapper: StatusWrapper<RequisitoCategoriaProfesionalExigidoAndAcreditado>, property: string) => {
        switch (property) {
          case 'nivelAcademico':
            return wrapper.value.nivelExigido.nombre;
          case 'fechaMinima':
            return wrapper.value.fechaMinimaObtencion;
          case 'fechaMaxima':
            return wrapper.value.fechaMaximaObtencion;
          default:
            return wrapper[property];
        }
      };

    this.dataSourceCategoriasProfesionalesSolicitante.sort = this.sortCategoriasProfesionalesSolicitante;


    this.dataSourceCategoriasProfesionalesTutor.sortingDataAccessor =
      (wrapper: StatusWrapper<RequisitoCategoriaProfesionalExigido>, property: string) => {
        switch (property) {
          case 'nivelAcademico':
            return wrapper.value.nivelExigido.nombre;
          case 'fechaMinima':
            return wrapper.value.fechaMinimaObtencion;
          case 'fechaMaxima':
            return wrapper.value.fechaMaximaObtencion;
          default:
            return wrapper[property];
        }
      };

    this.dataSourceNivelesAcademicosTutor.sort = this.sortCategoriasProfesionalesTutor;


    this.dataSourceNivelesAcademicosSolicitante.sortingDataAccessor =
      (wrapper: StatusWrapper<RequisitoNivelAcademicoExigidoAndAcreditado>, property: string) => {
        switch (property) {
          case 'nivelAcademico':
            return wrapper.value.nivelExigido.nombre;
          case 'fechaMinima':
            return wrapper.value.fechaMinimaObtencion;
          case 'fechaMaxima':
            return wrapper.value.fechaMaximaObtencion;
          default:
            return wrapper[property];
        }
      };

    this.dataSourceNivelesAcademicosSolicitante.sort = this.sortNivelesAcademicosSolicitante;


    this.dataSourceNivelesAcademicosTutor.sortingDataAccessor =
      (wrapper: StatusWrapper<RequisitoNivelAcademicoExigido>, property: string) => {
        switch (property) {
          case 'nivelAcademico':
            return wrapper.value.nivelExigido.nombre;
          case 'fechaMinima':
            return wrapper.value.fechaMinimaObtencion;
          case 'fechaMaxima':
            return wrapper.value.fechaMaximaObtencion;
          default:
            return wrapper[property];
        }
      };

    this.dataSourceNivelesAcademicosTutor.sort = this.sortNivelesAcademicosTutor;

    this.subscriptions.push(this.formPart.requisitosCategoriasExigidasSolicitante$.subscribe(elements => {
      this.dataSourceCategoriasProfesionalesSolicitante.data = elements;
    }));

    this.subscriptions.push(this.formPart.requisitosCategoriasExigidasTutor$.subscribe(elements => {
      this.dataSourceCategoriasProfesionalesTutor.data = elements;
    }));

    this.subscriptions.push(this.formPart.requisitosNivelesAcademicosExigidosSolicitante$.subscribe(elements => {
      this.dataSourceNivelesAcademicosSolicitante.data = elements;
    }));

    this.subscriptions.push(this.formPart.requisitosNivelesAcademicosExigidosTutor$.subscribe(elements => {
      this.dataSourceNivelesAcademicosTutor.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_RRHH_REQUISITOS_ACREDITACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_RRHH_REQUISITOS_ACREDITACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.msgDeleteAcreditacion = value);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModalCreateAcreditacionCategoriaProfesional(wrapper?: StatusWrapper<RequisitoCategoriaProfesionalExigidoAndAcreditado>): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSourceCategoriasProfesionalesSolicitante.sortData(
      this.dataSourceCategoriasProfesionalesSolicitante.filteredData,
      this.dataSourceCategoriasProfesionalesSolicitante.sort
    );

    const data: SolicitudRrhhAcreditarCategoriaProfesionalPublicModalData = {
      categoriaProfesional: wrapper.value.nivelExigido,
      fechaMinimaObtencion: wrapper.value.fechaMinimaObtencion,
      fechaMaximaObtencion: wrapper.value.fechaMaximaObtencion,
      documento: null
    };

    const config: MatDialogConfig = {
      data
    };

    const dialogRef = this.matDialog.open(SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent, config);
    dialogRef.afterClosed().subscribe((modalData: SolicitudRrhhAcreditarCategoriaProfesionalPublicModalData) => {
      if (modalData) {
        wrapper.value.nivelAcreditado.documento = modalData.documento;
        this.formPart.acreditarRequisito(wrapper);
      }
    });
  }

  openModalCreateAcreditacionNivelAcademico(wrapper?: StatusWrapper<RequisitoNivelAcademicoExigidoAndAcreditado>): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSourceNivelesAcademicosSolicitante.sortData(
      this.dataSourceNivelesAcademicosSolicitante.filteredData,
      this.dataSourceNivelesAcademicosSolicitante.sort
    );

    const data: SolicitudRrhhAcreditarNivelAcademicoPublicModalData = {
      nivelAcademico: wrapper.value.nivelExigido,
      fechaMinimaObtencion: wrapper.value.fechaMinimaObtencion,
      fechaMaximaObtencion: wrapper.value.fechaMaximaObtencion,
      documento: null
    };

    const config: MatDialogConfig = {
      data
    };

    const dialogRef = this.matDialog.open(SolicitudRrhhAcreditarNivelAcademicoPublicModalComponent, config);
    dialogRef.afterClosed().subscribe((modalData: SolicitudRrhhAcreditarNivelAcademicoPublicModalData) => {
      if (modalData) {
        wrapper.value.nivelAcreditado.documento = modalData.documento;
        this.formPart.acreditarRequisito(wrapper);
      }
    });
  }

  deleteAcreditacionCategoriaProfesional(wrapper: StatusWrapper<RequisitoCategoriaProfesionalExigidoAndAcreditado>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.msgDeleteAcreditacion).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteAcreditacionCategoriaProfesional(wrapper);
          }
        }
      )
    );
  }

  deleteAcreditacionNivelAcademico(wrapper: StatusWrapper<RequisitoNivelAcademicoExigidoAndAcreditado>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.msgDeleteAcreditacion).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteAcreditacionNivelAcademico(wrapper);
          }
        }
      )
    );
  }

  downloadFile(documentoRef: string): void {
    this.subscriptions.push(
      forkJoin({
        documento: this.documentoService.getInfoFichero(documentoRef),
        fichero: this.documentoService.downloadFichero(documentoRef),
      }).subscribe(
        ({ documento, fichero }) => {
          triggerDownloadToUser(fichero, documento.nombre);
        },
        () => {
          this.snackBarService.showError(MSG_DOWNLOAD_ERROR);
        }
      ));
  }

}
