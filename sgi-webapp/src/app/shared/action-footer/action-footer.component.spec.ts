import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ActionStatus, IActionService } from '@core/services/action-service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { BehaviorSubject } from 'rxjs';
import { ActionFooterComponent } from './action-footer.component';

describe('ActionFooterComponent', () => {
  let component: ActionFooterComponent;
  let fixture: ComponentFixture<ActionFooterComponent>;

  const behaviorSubject = new BehaviorSubject<ActionStatus>(
    { changes: false, complete: false, errors: false, edit: false, problems: false });
  const mockActionService = {
    status$: behaviorSubject
  } as IActionService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ActionFooterComponent],
      imports: [
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionFooterComponent);
    component = fixture.componentInstance;
    component.actionService = mockActionService;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
