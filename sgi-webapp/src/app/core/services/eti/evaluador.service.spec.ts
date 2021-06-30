import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EvaluadorService } from './evaluador.service';

describe('EvaluadorService', () => {
  let service: EvaluadorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(EvaluadorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});