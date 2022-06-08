import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { StatusWrapper } from '@core/utils/status-wrapper';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { ConvocatoriaEntidadConvocanteModalComponent, ConvocatoriaEntidadConvocanteModalData } from './convocatoria-entidad-convocante-modal.component';

describe('ConvocatoriaEntidadConvocanteModalComponent', () => {
  let component: ConvocatoriaEntidadConvocanteModalComponent;
  let fixture: ComponentFixture<ConvocatoriaEntidadConvocanteModalComponent>;

  const data: IConvocatoriaEntidadConvocante = {
    convocatoriaId: undefined,
    entidad: undefined,
    id: 1,
    programa: undefined
  };

  const modalData: ConvocatoriaEntidadConvocanteModalData = {
    entidadConvocanteData: {
      empresa: {} as IEmpresa,
      entidadConvocante: new StatusWrapper<IConvocatoriaEntidadConvocante>(data),
      plan: undefined,
      programa: undefined,
      modalidad: undefined,
    },
    selectedEmpresas: [],
    readonly: false
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaEntidadConvocanteModalComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        CspSharedModule,
        SgempSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: modalData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaEntidadConvocanteModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
