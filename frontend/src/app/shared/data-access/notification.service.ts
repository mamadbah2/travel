import { Injectable, signal } from '@angular/core';

export interface ToastMessage {
  id: string;
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly messagesSignal = signal<ToastMessage[]>([]);
  readonly messages = this.messagesSignal.asReadonly();

  showSuccess(message: string): void {
    this.addMessage('success', message);
  }

  showError(message: string): void {
    this.addMessage('error', message);
  }

  showInfo(message: string): void {
    this.addMessage('info', message);
  }

  showWarning(message: string): void {
    this.addMessage('warning', message);
  }

  dismiss(id: string): void {
    this.messagesSignal.update((msgs) => msgs.filter((m) => m.id !== id));
  }

  private addMessage(type: ToastMessage['type'], message: string): void {
    const id = crypto.randomUUID();
    this.messagesSignal.update((msgs) => [...msgs, { id, type, message }]);

    // Auto-dismiss after 5 seconds
    setTimeout(() => this.dismiss(id), 5000);
  }
}
