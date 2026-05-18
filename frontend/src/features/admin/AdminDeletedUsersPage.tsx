import { useMutation, useQuery } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import { RotateCcw } from 'lucide-react';
import { queryClient } from '../../app/queryClient';
import { Button } from '../../shared/components/Button';
import { DataTable } from '../../shared/components/DataTable';
import { listDeletedAdminUsers, restoreAdminUser } from './adminUsersApi';

export function AdminDeletedUsersPage() {
  const queryKey = ['admin-users', 'deleted'];
  const { data: users = [], isLoading } = useQuery({ queryKey, queryFn: listDeletedAdminUsers });
  const restoreMutation = useMutation({
    mutationFn: restoreAdminUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey });
      queryClient.invalidateQueries({ queryKey: ['admin-users', 'CUSTOMER'] });
      queryClient.invalidateQueries({ queryKey: ['admin-users', 'STAFF'] });
      queryClient.invalidateQueries({ queryKey: ['staff'] });
    },
  });

  return (
    <section className="grid gap-5">
      <div>
        <h2 className="text-xl font-semibold text-slate-900">Deleted Users</h2>
        <p className="text-sm text-slate-500">Restore deleted customer and staff accounts.</p>
      </div>

      {restoreMutation.error ? <p className="text-sm text-rose-600">{getErrorMessage(restoreMutation.error)}</p> : null}
      {isLoading ? (
        <p className="text-sm text-slate-500">Loading...</p>
      ) : (
        <DataTable
          data={users}
          emptyText="No deleted customer or staff accounts found."
          columns={[
            { key: 'name', header: 'Name', render: (row) => row.fullName },
            { key: 'email', header: 'Email', render: (row) => row.email },
            { key: 'role', header: 'Role', render: (row) => row.role },
            { key: 'deleted', header: 'Deleted', render: (row) => new Date(row.deletedAt).toLocaleString() },
            {
              key: 'actions',
              header: 'Actions',
              render: (row) => (
                <Button
                  type="button"
                  variant="secondary"
                  className="h-9 px-3"
                  disabled={restoreMutation.isPending}
                  title={`Restore ${row.fullName}`}
                  aria-label={`Restore ${row.fullName}`}
                  onClick={() => restoreMutation.mutate(row.id)}
                >
                  <RotateCcw className="mr-2 h-4 w-4" />
                  Restore
                </Button>
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
  return 'Could not restore user.';
}
