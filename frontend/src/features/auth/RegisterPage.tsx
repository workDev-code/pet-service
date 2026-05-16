import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { Button } from '../../shared/components/Button';
import { Input } from '../../shared/components/Input';
import { Select } from '../../shared/components/Select';
import { AuthShell } from './LoginPage';
import { register } from './authApi';

const schema = z.object({
  fullName: z.string().min(2),
  email: z.string().email(),
  password: z.string().min(8),
  role: z.enum(['CUSTOMER', 'STAFF']),
});

type FormValues = z.infer<typeof schema>;

export function RegisterPage() {
  const navigate = useNavigate();
  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { role: 'CUSTOMER' },
  });
  const mutation = useMutation({
    mutationFn: register,
    onSuccess: () => navigate('/bookings'),
  });

  return (
    <AuthShell title="Create an MVP account">
      <form className="grid gap-4" onSubmit={form.handleSubmit((values) => mutation.mutate(values))}>
        <Input label="Full name" error={form.formState.errors.fullName?.message} {...form.register('fullName')} />
        <Input label="Email" type="email" error={form.formState.errors.email?.message} {...form.register('email')} />
        <Input label="Password" type="password" error={form.formState.errors.password?.message} {...form.register('password')} />
        <Select label="Role" error={form.formState.errors.role?.message} {...form.register('role')}>
          <option value="CUSTOMER">Customer</option>
          <option value="STAFF">Staff</option>
        </Select>
        {mutation.error ? <p className="text-sm text-rose-600">Registration failed. Try another email.</p> : null}
        <Button type="submit" disabled={mutation.isPending}>
          Create account
        </Button>
        <p className="text-center text-sm text-slate-600">
          Already registered? <Link className="font-medium text-brand-700" to="/login">Sign in</Link>
        </p>
      </form>
    </AuthShell>
  );
}
