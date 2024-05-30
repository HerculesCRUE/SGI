import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IDocumentacionConvocatoriaReunion } from '@core/models/eti/documentacion-convocatoria-reunion';
import { IDocumento } from '@core/models/sgdoc/documento';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaReunionActionService } from '../../convocatoria-reunion.action.service';
import { ConvocatoriaReunionDocumentacionComponent } from './convocatoria-reunion-documentacion.component';

describe('ConvocatoriaReunionDocumentacionComponent', () => {
  let component: ConvocatoriaReunionDocumentacionComponent;
  let fixture: ComponentFixture<ConvocatoriaReunionDocumentacionComponent>;

  const snapshotData = {
    id: 1,
    convocatoriaReunion: {
      id: 1,
    } as IConvocatoriaReunion,
    documentacionConvocatoriaReunion: {
      documento: {
        documentoRef: "docRef"
      } as IDocumento,
      convocatoriaReunion: {
        id: 1,
      } as IConvocatoriaReunion,
      id: 1
    } as IDocumentacionConvocatoriaReunion
  };

  const formBuilder: FormBuilder = new FormBuilder();

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaReunionDocumentacionComponent],
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        SgiAuthModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { data: snapshotData } } },
        ConvocatoriaReunionActionService,
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaReunionDocumentacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
});
