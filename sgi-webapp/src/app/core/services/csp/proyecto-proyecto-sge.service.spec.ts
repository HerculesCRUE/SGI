import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoProyectoSgeService } from './proyecto-proyecto-sge.service';

describe('ProyectoProyectoSgeService', () => {
  let service: ProyectoProyectoSgeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoProyectoSgeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
