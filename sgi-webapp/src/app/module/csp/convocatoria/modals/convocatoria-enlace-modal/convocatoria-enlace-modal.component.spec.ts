import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { ConvocatoriaEnlaceModalComponent, ConvocatoriaEnlaceModalComponentData } from './convocatoria-enlace-modal.component';

describe('ConvocatoriaEnlaceModalComponent', () => {
  let component: ConvocatoriaEnlaceModalComponent;
  let fixture: ComponentFixture<ConvocatoriaEnlaceModalComponent>;

  const convocatoriaEnlace: IConvocatoriaEnlace = {
    activo: true,
    convocatoriaId: 1,
    descripcion: '',
    id: 1,
    tipoEnlace: undefined,
    url: ''
  };

  const data: ConvocatoriaEnlaceModalComponentData = {
    enlace: convocatoriaEnlace,
    idModeloEjecucion: 1,
    selectedUrls: [],
    readonly: false,
    canEdit: false
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaEnlaceModalComponent
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
        SgiAuthModule
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
    fixture = TestBed.createComponent(ConvocatoriaEnlaceModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
