import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IGrupoPersonaAutorizada } from '@core/models/csp/grupo-persona-autorizada';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { GrupoPersonaAutorizadaModalComponent, GrupoPersonaAutorizadaModalData } from './grupo-persona-autorizada-modal.component';

describe('GrupoPersonaAutorizadaModalComponent', () => {
  let component: GrupoPersonaAutorizadaModalComponent;
  let fixture: ComponentFixture<GrupoPersonaAutorizadaModalComponent>;

  const newData: GrupoPersonaAutorizadaModalData = {
    titleEntity: 'title',
    selectedEntidades: [],
    entidad: {
    } as IGrupoPersonaAutorizada,
    fechaInicioMin: undefined,
    fechaFinMax: undefined,
    isEdit: true
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        GrupoPersonaAutorizadaModalComponent
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
        SgpSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrupoPersonaAutorizadaModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
