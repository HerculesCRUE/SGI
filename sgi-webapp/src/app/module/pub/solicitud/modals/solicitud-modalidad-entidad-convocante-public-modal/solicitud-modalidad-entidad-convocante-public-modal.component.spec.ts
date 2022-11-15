import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IEmpresa } from '@core/models/sgemp/empresa';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudModalidadEntidadConvocantePublicModalComponent, SolicitudModalidadEntidadConvocantePublicModalData } from './solicitud-modalidad-entidad-convocante-public-modal.component';

describe('SolicitudModalidadEntidadConvocantePublicModalComponent', () => {
  let component: SolicitudModalidadEntidadConvocantePublicModalComponent;
  let fixture: ComponentFixture<SolicitudModalidadEntidadConvocantePublicModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SolicitudModalidadEntidadConvocantePublicModalComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            entidad: {} as IEmpresa,
            plan: {} as IPrograma,
            programa: { id: 1 } as IPrograma,
            modalidad: {} as ISolicitudModalidad
          } as SolicitudModalidadEntidadConvocantePublicModalData
        }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudModalidadEntidadConvocantePublicModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
