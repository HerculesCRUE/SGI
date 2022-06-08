import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { AgrupacionGastoConceptoModalComponent, AgrupacionGastoConceptoModalData } from './agrupacion-gasto-concepto-modal.component';

describe('AgrupacionGastoConceptoModalComponent', () => {
  let component: AgrupacionGastoConceptoModalComponent;
  let fixture: ComponentFixture<AgrupacionGastoConceptoModalComponent>;

  const conceptoGasto: IConceptoGasto = {
    id: 1,
    nombre: undefined,
    descripcion: undefined,
    activo: undefined,
    costesIndirectos: true,
  };

  const newData: AgrupacionGastoConceptoModalData = {
    proyectoId: 1,
    agrupacionId: 1,
    conceptosEliminados: null,
    titleEntity: 'titulo',
    entidad: conceptoGasto,
    selectedEntidades: null,
    isEdit: false,
    readonly: false
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        AgrupacionGastoConceptoModalComponent
      ],
      imports: [
        SharedModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgrupacionGastoConceptoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
