import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ComiteService } from './comite.service';


describe('ComiteService', () => {
  let service: ComiteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ComiteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
