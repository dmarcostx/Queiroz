import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Component, inject, OnInit} from '@angular/core';
import {OidcSecurityService} from 'angular-auth-oidc-client';

@Component({
  selector: 'app-root', templateUrl: './app.component.html', styleUrls: ['./app.component.css'], standalone: false
})
export class AppComponent implements OnInit {
  title = 'frontend';
  isAuthenticated = false;
  entities: any[] = [];
  userData: any = null;
  users: any = [];
  newUsername: string = "";
  newPassword: string = "";
  disciplines: any = [];
  newRole: string = "";
  newDisciplineTitle: string = "";
  newCourseTitle: string = "";
  newSemesterNumber: number = 0;
  loading = false;
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly http = inject(HttpClient);
  private backendApiUrl = 'http://localhost:8080';

  ngOnInit() {
    this.oidcSecurityService.checkAuth().subscribe(({isAuthenticated, userData}) => {
      this.isAuthenticated = isAuthenticated;
      this.userData = userData;
      if (isAuthenticated) this.load(); else this.oidcSecurityService.authorize();
    });
  }

  load() {
    this.startLoading();
    this.oidcSecurityService.getAccessToken().subscribe((token) => {
      if (token) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        if (this.hasRole('administrador')) this.oidcSecurityService.getAccessToken().subscribe((token) => {
          if (token) {
            const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
            this.http.get(`${this.backendApiUrl}/user`, {
              headers, observe: 'response'
            }).subscribe((users) => {
              this.users = (<[string, string, boolean, string][]>users.body).map(([id, username, enabled, role]) => ({
                id, username, enabled, role
              }));
              this.loading = false;
            });
          }
        }); else if (this.hasRole('coordenador') || this.hasRole('professor') || this.hasRole('aluno')) this.http.get(`${this.backendApiUrl}/discipline`, {
          headers, responseType: 'json'
        }).subscribe({
          next: (response) => {
            this.disciplines = response;
            this.loading = false;
          }
        });
      }
    });
  }

  logout() {
    this.loading = true;
    this.oidcSecurityService.logoffAndRevokeTokens().subscribe(() => {
      this.oidcSecurityService.logoffLocal();
    });
  }

  hasRole(role: string) {
    return this.userData && this.userData.roles && this.userData.roles.includes(role);
  }

  createDiscipline() {
    this.startLoading();
    this.oidcSecurityService.getAccessToken().subscribe((token) => {
      if (token) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        this.http.post(`${this.backendApiUrl}/discipline`, {
          name: this.newDisciplineTitle.trim(), semester: this.newSemesterNumber, course: this.newCourseTitle.trim()
        }, {headers, responseType: 'json'}).subscribe(() => {
          this.newCourseTitle = "";
          this.newSemesterNumber = 0;
          this.newDisciplineTitle = "";
          this.loading = false;
          this.load()
        });
      }
    });
  }

  deleteDiscipline(discipline: any) {
    this.oidcSecurityService.getAccessToken().subscribe((token) => {
      if (token) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        this.http.delete(`${this.backendApiUrl}/discipline`, {
          headers, responseType: 'json', body: discipline
        }).subscribe(() => this.load());
      }
    });
  }

  createUser() {
    this.startLoading();
    this.oidcSecurityService.getAccessToken().subscribe((token) => {
      if (token) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        this.http.post(`${this.backendApiUrl}/user`, {
          name: this.newUsername, role: this.newRole, password: this.newPassword
        }, {headers, observe: 'response'}).subscribe(() => {
          this.loading = false;
          this.newUsername = '';
          this.newPassword = '';
          this.newRole = '';
          this.load();
        });
      }
    });
  }

  enableUser(user: any) {
    this.startLoading();
    this.oidcSecurityService.getAccessToken().subscribe((token) => {
      if (token) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        this.http.patch(`${this.backendApiUrl}/user`, {id: user.id}, {
          headers, observe: 'response'
        }).subscribe(() => {
          this.loading = false;
          this.load();
        });
      }
    });
  }

  newUserInvalid() {
    if (this.loading) return 'Carregando';

    this.newUsername = this.newUsername.trim();
    if (!this.newUsername) return 'Preencha o nome';
    if (!this.newPassword.trim()) return 'Preencha a senha';
    if (!this.newRole) return 'Preencha o tipo';
    return false;
  }

  newDisciplineInvalid() {
    if (this.loading) return 'Carregando';

    this.newDisciplineTitle = this.newDisciplineTitle.trim();
    this.newCourseTitle = this.newCourseTitle.trim();
    if (!this.newDisciplineTitle) return 'Preencha a Disciplina';
    if (!this.newCourseTitle) return 'Preencha o Curso';
    if (this.newSemesterNumber <= 0) return 'Semestre deve ser positivo';
    if (this.newSemesterNumber > 99) return 'Semestre com mais de dois dígitos';
    if (this.disciplines.some((discipline: {
      name: string; course: string;
    }) => discipline.name === this.newDisciplineTitle && discipline.course === this.newCourseTitle)) return 'Disciplina já cadastrada para esse curso esse semestre';
    return false;
  }

  startLoading() {
    this.loading = true;
    setTimeout(() => this.loading = false, 3000);
  }
}
