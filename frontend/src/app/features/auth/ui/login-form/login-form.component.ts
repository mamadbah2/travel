import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginRequest } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-login-form',
  imports: [FormsModule],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.css',
})
export class LoginFormComponent {
  readonly loading = input(false);
  readonly submitLogin = output<LoginRequest>();

  email = '';
  password = '';

  onSubmit(): void {
    if (this.email && this.password) {
      this.submitLogin.emit({ email: this.email, password: this.password });
    }
  }
}
