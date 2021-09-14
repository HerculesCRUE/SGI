import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConceptoGastoModalComponent } from './concepto-gasto-modal.component';

describe('ConceptoGastoModalComponent', () => {
  let component: ConceptoGastoModalComponent;
  let fixture: ComponentFixture<ConceptoGastoModalComponent>;

  beforeEach(waitForAsync(() => {
    const mockDialogRef = {
      close: jasmine.createSpy('close'),
    };
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        FlexModule,
        FormsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        MaterialDesignModule,
        ReactiveFormsModule,
        RouterTestingModule,
        SharedModule,
        TestUtils.getIdiomas(),
      ],
      declarations: [ConceptoGastoModalComponent],
      providers: [{ provide: MatDialogRef, useValue: mockDialogRef },
      { provide: MAT_DIALOG_DATA, useValue: {} as IConceptoGasto }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConceptoGastoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
