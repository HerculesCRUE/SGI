import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EmpresaPublicService } from './empresa-public.service';

describe('EmpresaPublicService', () => {
  let service: EmpresaPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(EmpresaPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
