import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { I18nComponentsModule } from '@components/i18n/i18n-components.module';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IEmpresa } from '@core/models/sgemp/empresa';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SolicitudModalidadEntidadConvocanteModalComponent, SolicitudModalidadEntidadConvocanteModalData } from './solicitud-modalidad-entidad-convocante-modal.component';

describe('SolicitudModalidadEntidadConvocanteComponent', () => {
  let component: SolicitudModalidadEntidadConvocanteModalComponent;
  let fixture: ComponentFixture<SolicitudModalidadEntidadConvocanteModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SolicitudModalidadEntidadConvocanteModalComponent],
      imports: [
        BrowserAnimationsModule,
        FormsModule,
        HttpClientTestingModule,
        I18nComponentsModule,
        LoggerTestingModule,
        MaterialDesignModule,
        ReactiveFormsModule,
        RouterTestingModule,
        SgiAuthModule,
        SgempSharedModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
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
