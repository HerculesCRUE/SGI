import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IMiembroEquipoProyecto } from '@core/models/csp/miembro-equipo-proyecto';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../csp-shared.module';
import { MiembroEquipoProyectoModalComponent, MiembroEquipoProyectoModalData } from './miembro-equipo-proyecto-modal.component';

describe('MiembroEquipoProyectoModalComponent', () => {
  let component: MiembroEquipoProyectoModalComponent;
  let fixture: ComponentFixture<MiembroEquipoProyectoModalComponent>;

  const newData: MiembroEquipoProyectoModalData = {
    titleEntity: 'title',
    isEdit: false,
    selectedEntidades: [],
    entidad: {} as IMiembroEquipoProyecto,
    fechaFinMax: undefined,
    fechaInicioMin: undefined,
    readonly: false
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        MiembroEquipoProyectoModalComponent
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
        { provide: MAT_DIALOG_DATA, useValue: newData },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MiembroEquipoProyectoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
