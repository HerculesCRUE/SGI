import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FieldInfoComponent } from './field-info.component';

describe('FieldInfoComponent', () => {
  let component: FieldInfoComponent;
  let fixture: ComponentFixture<FieldInfoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [FieldInfoComponent],
      imports: []
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FieldInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
