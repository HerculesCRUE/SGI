import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoAnualidadService } from './proyecto-anualidad.service';


describe('ProyectoAnualidadService', () => {
  let service: ProyectoAnualidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoAnualidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
