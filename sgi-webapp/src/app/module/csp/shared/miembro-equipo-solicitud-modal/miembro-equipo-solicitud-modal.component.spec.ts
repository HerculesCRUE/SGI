import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogComponent } from '@block/dialog/dialog.component';
import { HeaderComponent } from '@block/header/header.component';
import { IMiembroEquipoSolicitud } from '@core/models/csp/miembro-equipo-solicitud';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../csp-shared.module';
import { MiembroEquipoSolicitudModalComponent, MiembroEquipoSolicitudModalData } from './miembro-equipo-solicitud-modal.component';

describe('MiembroEquipoSolicitudModalComponent', () => {
  let component: MiembroEquipoSolicitudModalComponent;
  let fixture: ComponentFixture<MiembroEquipoSolicitudModalComponent>;

  const data: MiembroEquipoSolicitudModalData = {
    titleEntity: 'title',
    selectedEntidades: [],
    entidad: {} as IMiembroEquipoSolicitud,
    mesInicialMin: 1,
    mesFinalMax: 20,
    isEdit: true,
    index: 0,
    readonly: true
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        MiembroEquipoSolicitudModalComponent,
        DialogComponent,
        HeaderComponent
      ],
      imports: [
        CspSharedModule,
        SharedModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SgpSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: data },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MiembroEquipoSolicitudModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
