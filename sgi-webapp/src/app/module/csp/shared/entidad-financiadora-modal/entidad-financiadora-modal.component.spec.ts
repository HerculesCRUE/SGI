import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../csp-shared.module';
import { EntidadFinanciadoraDataModal, EntidadFinanciadoraModalComponent } from './entidad-financiadora-modal.component';

describe('EntidadFinanciadoraModalComponent', () => {
  let component: EntidadFinanciadoraModalComponent;
  let fixture: ComponentFixture<EntidadFinanciadoraModalComponent>;

  const data: EntidadFinanciadoraDataModal = {
    title: 'title',
    entidad: {} as IEntidadFinanciadora,
    readonly: false
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EntidadFinanciadoraModalComponent
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
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EntidadFinanciadoraModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
