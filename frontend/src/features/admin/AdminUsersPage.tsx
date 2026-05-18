import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQuery } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import { Edit2, Trash2, X } from 'lucide-react';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { queryClient } from '../../app/queryClient';
import { Button } from '../../shared/components/Button';
import { DataTable } from '../../shared/components/DataTable';
import { Input } from '../../shared/components/Input';
import { Role, UserResponse } from '../../shared/types/domain';
import { createAdminUser, deleteAdminUser, listAdminUsers, updateAdminUser } from './adminUsersApi';

const schema = z.object({
  fullName: z.string().min(1, 'Full name is required').max(120, 'Full name is too long'),
  email: z.string().min(1, 'Email is required').email('Enter a valid email').max(160, 'Email is too long'),
  password: z.string().max(120, 'Password is too long').optional(),
});

type FormValues = z.infer<typeof schema>;

const ROLE_LABELS: Record<Role, string> = {
  ADMIN: 'Admins',
  CUSTOMER: 'Customers',
  STAFF: 'Staff',
};

export function AdminUsersPage({ role }: { role: Extract<Role, 'CUSTOMER' | 'STAFF'> }) {
  const [editingUser, setEditingUser] = useState<UserResponse | null>(null);
  const queryKey = ['admin-users', role];
  const { data: users = [], isLoading } = useQuery({ queryKey, queryFn: () => listAdminUsers(role) });
  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { fullName: '', email: '', password: '' },
  });
  const createMutation = useMutation({
    mutationFn: createAdminUser,
    onSuccess: () => {
      clearForm();
      invalidateUsers();
    },
  });
  const updateMutation = useMutation({
    mutationFn: ({ id, values }: { id: number; values: FormValues }) => updateAdminUser(id, {
      fullName: values.fullName,
      email: values.email,
      password: values.password || undefined,
      role,
    }),
    onSuccess: () => {
      clearForm();
      invalidateUsers();
    },
  });
  const deleteMutation = useMutation({
    mutationFn: deleteAdminUser,
    onSuccess: invalidateUsers,
  });
  const saveError = createMutation.error || updateMutation.error;

  function invalidateUsers() {
    queryClient.invalidateQueries({ queryKey });
    if (role === 'STAFF') {
      queryClient.invalidateQueries({ queryKey: ['staff'] });
    }
  }

  function clearForm() {
    setEditingUser(null);
    form.reset({ fullName: '', email: '', password: '' });
  }

  function startEdit(user: UserResponse) {
    setEditingUser(user);
    form.reset({ fullName: user.fullName, email: user.email, password: '' });
  }

  function handleDelete(user: UserResponse) {
    if (window.confirm(`Delete ${user.fullName}?`)) {
      deleteMutation.mutate(user.id);
    }
  }

  function handleSubmit(values: FormValues) {
    if (editingUser) {
      updateMutation.mutate({ id: editingUser.id, values });
      return;
    }
    if (!values.password || values.password.length < 8) {
      form.setError('password', { message: 'Password must be at least 8 characters' });
      return;
    }
    createMutation.mutate({ ...values, password: values.password ?? '', role });
  }

  const title = ROLE_LABELS[role];
  const isSaving = createMutation.isPending || updateMutation.isPending;

  return (
    <section className="grid gap-5">
      <div>
        <h2 className="text-xl font-semibold text-slate-900">{title}</h2>
        <p className="text-sm text-slate-500">Create, update, and remove {role.toLowerCase()} accounts.</p>
      </div>

      <form
        className="grid gap-4 rounded-md border border-slate-200 bg-white p-5 md:grid-cols-2"
        onSubmit={form.handleSubmit(handleSubmit)}
      >
        <div className="md:col-span-2">
          <h3 className="text-base font-semibold text-slate-900">{editingUser ? `Edit ${role.toLowerCase()}` : `Add ${role.toLowerCase()}`}</h3>
        </div>
        <Input label="Full name" error={form.formState.errors.fullName} {...form.register('fullName')} />
        <Input label="Email" type="email" error={form.formState.errors.email} {...form.register('email')} />
        <Input
          className="md:max-w-sm"
          label={editingUser ? 'New password' : 'Password'}
          type="password"
          error={form.formState.errors.password}
          {...form.register('password')}
        />
        {saveError ? <p className="text-sm text-rose-600 md:col-span-2">{getErrorMessage(saveError)}</p> : null}
        <div className="flex flex-wrap gap-2 md:col-span-2">
          <Button className="w-fit" type="submit" disabled={isSaving}>
            {isSaving ? 'Saving...' : editingUser ? 'Save changes' : `Add ${role.toLowerCase()}`}
          </Button>
          {editingUser ? (
            <Button className="w-fit" type="button" variant="secondary" onClick={clearForm}>
              <X className="mr-2 h-4 w-4" />
              Cancel
            </Button>
          ) : null}
        </div>
      </form>

      {deleteMutation.error ? <p className="text-sm text-rose-600">{getErrorMessage(deleteMutation.error)}</p> : null}
      {isLoading ? (
        <p className="text-sm text-slate-500">Loading...</p>
      ) : (
        <DataTable
          data={users}
          emptyText={`No ${role.toLowerCase()} accounts found.`}
          columns={[
            { key: 'name', header: 'Name', render: (row) => row.fullName },
            { key: 'email', header: 'Email', render: (row) => row.email },
            { key: 'created', header: 'Created', render: (row) => new Date(row.createdAt).toLocaleDateString() },
            {
              key: 'actions',
              header: 'Actions',
              render: (row) => (
                <div className="flex gap-2">
                  <Button
                    type="button"
                    variant="secondary"
                    className="h-9 px-3"
                    title={`Edit ${row.fullName}`}
                    aria-label={`Edit ${row.fullName}`}
                    onClick={() => startEdit(row)}
                  >
                    <Edit2 className="h-4 w-4" />
                  </Button>
                  <Button
                    type="button"
                    variant="danger"
                    className="h-9 px-3"
                    disabled={deleteMutation.isPending}
                    title={`Delete ${row.fullName}`}
                    aria-label={`Delete ${row.fullName}`}
                    onClick={() => handleDelete(row)}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              ),
            },
          ]}
        />
      )}
    </section>
  );
}

function getErrorMessage(error: unknown) {
  if (error instanceof AxiosError) {
    const message = (error.response?.data as { message?: string } | undefined)?.message;
    if (message) {
      return message;
    }
  }
  return 'Could not save user changes.';
}
