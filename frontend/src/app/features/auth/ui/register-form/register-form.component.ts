import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RegisterRequest, UserRole } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-register-form',
  imports: [FormsModule],
  templateUrl: './register-form.component.html',
  styleUrl: './register-form.component.css',
})
export class RegisterFormComponent {
  readonly loading = input(false);
  readonly submitRegister = output<RegisterRequest>();

  firstName = '';
  lastName = '';
  email = '';
  phoneNumber = '';
  password = '';
  role: UserRole = 'TRAVELER';

  onSubmit(): void {
    if (this.firstName && this.lastName && this.email && this.password) {
      this.submitRegister.emit({
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email,
        password: this.password,
        phoneNumber: this.phoneNumber || null,
        role: this.role,
      });
    }
  }
}
