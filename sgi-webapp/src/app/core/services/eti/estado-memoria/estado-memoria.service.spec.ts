import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EstadoMemoriaService } from './estado-memoria.service';

describe('EstadoMemoriaService', () => {
  let service: EstadoMemoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(EstadoMemoriaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
