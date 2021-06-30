import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { AnualidadGastoService } from './anualidad-gasto.service';


describe('AnualidadGastoService', () => {
  let service: AnualidadGastoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(AnualidadGastoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
