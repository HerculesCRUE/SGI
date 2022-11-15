import { DecimalPipe, PercentPipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { GrupoEnlaceListadoExportService } from '../../grupo-enlace-listado-export.service';
import { GrupoEquipoInstrumentalListadoExportService } from '../../grupo-equipo-instrumental-listado-export.service';
import { GrupoEquipoListadoExportService } from '../../grupo-equipo-listado-export.service';
import { GrupoFooterListadoExportService } from '../../grupo-footer-listado-export.service';
import { GrupoGeneralListadoExportService } from '../../grupo-general-listado-export.service';
import { GrupoHeaderListadoExportService } from '../../grupo-header-listado-export.service';
import { GrupoLineaInvestigacionListadoExportService } from '../../grupo-linea-investigacion-listado-export.service';
import { GrupoListadoExportService } from '../../grupo-listado-export.service';
import { GrupoPersonaAutorizadaListadoExportService } from '../../grupo-persona-autorizada-listado-export.service';
import { GrupoResponsableEconomicoListadoExportService } from '../../grupo-responsable-economico-listado-export.service';
import { GrupoListadoExportModalComponent, IGrupoListadoModalData } from './grupo-listado-export-modal.component';


describe('GrupoListadoExportModalComponent', () => {
  let component: GrupoListadoExportModalComponent;
  let fixture: ComponentFixture<GrupoListadoExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        GrupoListadoExportModalComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TranslateModule,
        TestUtils.getIdiomas(),
        FlexLayoutModule,
        FormsModule,
        ReactiveFormsModule,
        LoggerTestingModule,
        SharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: TestUtils.buildDialogActionMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: {} as IGrupoListadoModalData },
        SgiAuthService,
        GrupoListadoExportService,
        GrupoGeneralListadoExportService,
        GrupoEquipoListadoExportService,
        GrupoResponsableEconomicoListadoExportService,
        GrupoEnlaceListadoExportService,
        GrupoPersonaAutorizadaListadoExportService,
        GrupoEquipoInstrumentalListadoExportService,
        GrupoLineaInvestigacionListadoExportService,
        LuxonDatePipe,
        DecimalPipe,
        PercentPipe,
        GrupoHeaderListadoExportService,
        GrupoFooterListadoExportService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrupoListadoExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
