import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { ReactNode } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { queryClient } from '../../app/queryClient';
import { Button } from '../../shared/components/Button';
import { Input } from '../../shared/components/Input';
import { AuthResponse, Role } from '../../shared/types/domain';
import { login } from './authApi';

const schema = z.object({
  email: z.string().email(),
  password: z.string().min(1),
});

type FormValues = z.infer<typeof schema>;

type LoginLocationState = {
  from?: {
    pathname?: string;
  };
};

function getRoleHomePath(role: Role) {
  if (role === 'ADMIN') {
    return '/admin/bookings';
  }

  if (role === 'STAFF') {
    return '/staff/bookings';
  }

  return '/bookings';
}

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { email: 'customer@example.com', password: 'Password123!' },
  });
  const mutation = useMutation({
    mutationFn: login,
    onSuccess: (auth: AuthResponse) => {
      queryClient.setQueryData(['me'], auth.user);
      queryClient.removeQueries({ queryKey: ['bookings'] });

      const state = location.state as LoginLocationState | null;
      const returnPath = state?.from?.pathname;
      navigate(returnPath ?? getRoleHomePath(auth.user.role), { replace: true });
    },
  });

  return (
    <AuthShell title="Welcome back">
      <form className="grid gap-4" onSubmit={form.handleSubmit((values) => mutation.mutate(values))}>
        <Input label="Email" type="email" error={form.formState.errors.email?.message} {...form.register('email')} />
        <Input label="Password" type="password" error={form.formState.errors.password?.message} {...form.register('password')} />
        {mutation.error ? <p className="text-sm text-rose-600">Login failed. Check your credentials.</p> : null}
        <Button type="submit" disabled={mutation.isPending}>
          Sign in
        </Button>
        <p className="text-center text-sm text-slate-600">
          New customer? <Link className="font-medium text-brand-700" to="/register">Create an account</Link>
        </p>
      </form>
    </AuthShell>
  );
}

export function AuthShell({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="grid min-h-screen place-items-center bg-slate-100 px-4">
      <div className="w-full max-w-md rounded-md border border-slate-200 bg-white p-6 shadow-sm">
        <h1 className="mb-2 text-2xl font-semibold text-slate-900">Pet Service Booking</h1>
        <p className="mb-6 text-sm text-slate-500">{title}</p>
        {children}
      </div>
    </div>
  );
}
