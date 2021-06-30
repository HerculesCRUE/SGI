import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ActionFooterComponent } from './action-footer.component';
import TestUtils from '@core/utils/test-utils';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { ActionStatus, IActionService } from '@core/services/action-service';
import { BehaviorSubject } from 'rxjs';

describe('ActionFooterComponent', () => {
  let component: ActionFooterComponent;
  let fixture: ComponentFixture<ActionFooterComponent>;

  const behaviorSubject = new BehaviorSubject<ActionStatus>(
    { changes: false, complete: false, errors: false, edit: false });
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
