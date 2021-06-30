import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';


import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import TestUtils from '@core/utils/test-utils';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SnackBarService } from '@core/services/snack-bar.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SolicitudModalidadEntidadConvocanteModalComponent, SolicitudModalidadEntidadConvocanteModalData } from './solicitud-modalidad-entidad-convocante-modal.component';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IEmpresa } from '@core/models/sgemp/empresa';

describe('SolicitudModalidadEntidadConvocanteComponent', () => {
  let component: SolicitudModalidadEntidadConvocanteModalComponent;
  let fixture: ComponentFixture<SolicitudModalidadEntidadConvocanteModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SolicitudModalidadEntidadConvocanteModalComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: {} as SolicitudModalidadEntidadConvocanteModalData },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            entidad: {} as IEmpresa,
            plan: {} as IPrograma,
            programa: { id: 1 } as IPrograma,
            modalidad: {} as ISolicitudModalidad
          } as SolicitudModalidadEntidadConvocanteModalData
        },
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudModalidadEntidadConvocanteModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
