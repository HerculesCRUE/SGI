import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { ModeloEjecucionTipoHitoModalComponent } from './modelo-ejecucion-tipo-hito-modal.component';

describe('ModeloEjecucionTipoHitoModalComponent', () => {
  let component: ModeloEjecucionTipoHitoModalComponent;
  let fixture: ComponentFixture<ModeloEjecucionTipoHitoModalComponent>;

  const data = {
    modeloTipoHito: {} as IModeloTipoHito,
    tipoHitos: [] as ITipoHito[]
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ModeloEjecucionTipoHitoModalComponent
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
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: data },
        { provide: MAT_DIALOG_DATA, useValue: data },
        SgiAuthService
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ModeloEjecucionTipoHitoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
