import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IModeloUnidad } from '@core/models/csp/modelo-unidad';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ModeloEjecucionTipoUnidadGestionComponent } from '../../modelo-ejecucion-formulario/modelo-ejecucion-tipo-unidad-gestion/modelo-ejecucion-tipo-unidad-gestion.component';
import { ModeloEjecucionActionService } from '../../modelo-ejecucion.action.service';
import { IModeloEjecucionTipoUnidadModal } from './modelo-ejecucion-tipo-unidad-gestion-modal.component';

describe('ModeloEjecucionTipoUnidadGestionModalComponent', () => {
  let component: ModeloEjecucionTipoUnidadGestionComponent;
  let fixture: ComponentFixture<ModeloEjecucionTipoUnidadGestionComponent>;


  const modeloTipoUnidad: IModeloUnidad = {
    id: null,
    unidadGestion: null,
    modeloEjecucion: null,
    activo: true
  };

  const tipoUnidades: IUnidadGestion[] = [];


  const dataModal: IModeloEjecucionTipoUnidadModal = {
    modeloTipoUnidad,
    tipoUnidades
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ModeloEjecucionTipoUnidadGestionComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        LoggerTestingModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: dataModal },
        ModeloEjecucionActionService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ModeloEjecucionTipoUnidadGestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
