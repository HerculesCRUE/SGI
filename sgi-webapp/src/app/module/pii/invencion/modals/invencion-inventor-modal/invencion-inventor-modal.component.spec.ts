import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { IPersona } from '@core/models/sgp/persona';
import { StatusWrapper } from '@core/utils/status-wrapper';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { InvencionInventorModalComponent, InvencionInventorModalData } from './invencion-inventor-modal.component';

describe('InvencionInventorModalComponent', () => {

  let component: InvencionInventorModalComponent;
  let fixture: ComponentFixture<InvencionInventorModalComponent>;

  beforeEach(async () => {

    const data = {
      invencionInventor: {
        value: {
          inventor: {
          } as IPersona,
          participacion: 0.01,
          repartoUniversidad: true
        }
      } as StatusWrapper<IInvencionInventor>,
      isEdit: true,
      inventoresNotAllowed: [] as IPersona[]
    } as InvencionInventorModalData;

    await TestBed.configureTestingModule({
      declarations: [InvencionInventorModalComponent],
      imports: [
        SharedModule,
        SgpSharedModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        MatDialogModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: data },
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvencionInventorModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
