import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { HeaderComponent } from '@block/header/header.component';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';

import { IncidenciaDocumentoRequerimientoModalComponent } from './incidencia-documento-requerimiento-modal.component';

describe('IncidenciaDocumentoRequerimientoModalComponent', () => {
  let component: IncidenciaDocumentoRequerimientoModalComponent;
  let fixture: ComponentFixture<IncidenciaDocumentoRequerimientoModalComponent>;

  beforeEach(waitForAsync(() => {
    const data = {} as IIncidenciaDocumentacionRequerimiento;


    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        BrowserAnimationsModule,
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        MatDialogModule,
        TestUtils.getIdiomas(),
        FormsModule,
        ReactiveFormsModule,
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: data },
      ],
      declarations: [IncidenciaDocumentoRequerimientoModalComponent, HeaderComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IncidenciaDocumentoRequerimientoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
