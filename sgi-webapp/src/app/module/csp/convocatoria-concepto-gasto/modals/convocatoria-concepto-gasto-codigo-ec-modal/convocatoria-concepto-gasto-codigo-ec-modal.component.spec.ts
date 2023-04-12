import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaConceptoGastoCodigoEcModalComponent, IConvocatoriaConceptoGastoCodigoEcModalComponent } from './convocatoria-concepto-gasto-codigo-ec-modal.component';

describe('ConvocatoriaConceptoGastoCodigoEcModalComponent', () => {
  let component: ConvocatoriaConceptoGastoCodigoEcModalComponent;
  let fixture: ComponentFixture<ConvocatoriaConceptoGastoCodigoEcModalComponent>;

  const data = {
    convocatoriaConceptoGastoCodigoEc: {
      id: 1,
      convocatoriaConceptoGastoId: 1
    } as IConvocatoriaConceptoGastoCodigoEc,
    convocatoriaConceptoGastoCodigoEcsTabla:
      [{
        id: 1,
        convocatoriaConceptoGastoId: 1
      },
      {
        id: 2,
        convocatoriaConceptoGastoId: 1
      },
      ] as IConvocatoriaConceptoGastoCodigoEc[],
    convocatoriaConceptoGastos: [
      {
        conceptoGasto: {
          id: 1,
          nombre: 'conceptoGasto1'
        } as IConceptoGasto,
        id: 1,
        permitido: true
      },
      {
        conceptoGasto: {
          id: 1,
          nombre: 'conceptoGasto1'
        } as IConceptoGasto,
        id: 2,
        permitido: true
      }
    ] as IConvocatoriaConceptoGasto[],
    convocatoriaConceptoGasto: null,
    convocatoriaConceptoGastoCodigoEcsConvocatoria: [],
    editModal: true,
    permitido: true,
    readonly: false,
    canEdit: false
  } as IConvocatoriaConceptoGastoCodigoEcModalComponent;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaConceptoGastoCodigoEcModalComponent
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
        SharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: data },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaConceptoGastoCodigoEcModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
