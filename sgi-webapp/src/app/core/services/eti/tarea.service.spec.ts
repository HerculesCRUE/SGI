import { TestBed } from '@angular/core/testing';

import { TareaService } from './tarea.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('TareaService', () => {
  let service: TareaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TareaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
