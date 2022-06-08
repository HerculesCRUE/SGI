import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import TestUtils from '@core/utils/test-utils';
import { PeriodoTitularidadTitularModalComponent, PeriodoTitularidadTitularModalData } from './periodo-titularidad-titular-modal.component';

describe('PeriodoTitularidadTitularModalComponent', () => {
  let component: PeriodoTitularidadTitularModalComponent;
  let fixture: ComponentFixture<PeriodoTitularidadTitularModalComponent>;

  const data = {
    periodoTitularidadTitular: {
      value: {}
    },
    isEdit: true,
    titularesNotAllowed: []
  } as PeriodoTitularidadTitularModalData;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PeriodoTitularidadTitularModalComponent],
      imports: [
        TestUtils.getIdiomas(),
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: data },
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeriodoTitularidadTitularModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
