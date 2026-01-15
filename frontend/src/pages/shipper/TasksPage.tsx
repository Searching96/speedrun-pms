import { Package } from 'lucide-react';
import { useShipperTasks } from '@/features/shipper/hooks/useShipperTasks';
import { TaskCard } from '@/features/shipper/components/TaskCard';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

export default function TasksPage() {
    const { data: tasks, isLoading, error } = useShipperTasks();

    if (isLoading) {
        return (
            <div className="flex items-center justify-center py-12">
                <div className="text-center">
                    <Package className="h-12 w-12 mx-auto mb-4 animate-pulse text-muted-foreground" />
                    <p className="text-muted-foreground">Loading tasks...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <Alert variant="destructive">
                <AlertDescription>
                    {error instanceof Error ? error.message : 'Failed to load tasks'}
                </AlertDescription>
            </Alert>
        );
    }

    const assignedTasks = tasks?.filter((t) => t.status === 'ASSIGNED') || [];
    const inProgressTasks = tasks?.filter((t) => t.status === 'IN_PROGRESS') || [];
    const completedTasks = tasks?.filter((t) => t.status === 'COMPLETED') || [];
    const failedTasks = tasks?.filter((t) => t.status === 'FAILED') || [];

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">My Tasks</h1>
                <p className="text-muted-foreground">
                    Manage your pickup and delivery assignments
                </p>
            </div>

            <Tabs defaultValue="assigned" className="w-full">
                <TabsList className="grid w-full grid-cols-4">
                    <TabsTrigger value="assigned">
                        Assigned ({assignedTasks.length})
                    </TabsTrigger>
                    <TabsTrigger value="in-progress">
                        In Progress ({inProgressTasks.length})
                    </TabsTrigger>
                    <TabsTrigger value="completed">
                        Completed ({completedTasks.length})
                    </TabsTrigger>
                    <TabsTrigger value="failed">
                        Failed ({failedTasks.length})
                    </TabsTrigger>
                </TabsList>

                <TabsContent value="assigned" className="space-y-4 mt-6">
                    {assignedTasks.length === 0 ? (
                        <div className="text-center py-12 text-muted-foreground">
                            <Package className="h-16 w-16 mx-auto mb-4 opacity-20" />
                            <p>No assigned tasks</p>
                        </div>
                    ) : (
                        <div className="grid gap-4 md:grid-cols-2">
                            {assignedTasks.map((task) => (
                                <TaskCard key={task.id} task={task} />
                            ))}
                        </div>
                    )}
                </TabsContent>

                <TabsContent value="in-progress" className="space-y-4 mt-6">
                    {inProgressTasks.length === 0 ? (
                        <div className="text-center py-12 text-muted-foreground">
                            <Package className="h-16 w-16 mx-auto mb-4 opacity-20" />
                            <p>No tasks in progress</p>
                        </div>
                    ) : (
                        <div className="grid gap-4 md:grid-cols-2">
                            {inProgressTasks.map((task) => (
                                <TaskCard key={task.id} task={task} />
                            ))}
                        </div>
                    )}
                </TabsContent>

                <TabsContent value="completed" className="space-y-4 mt-6">
                    {completedTasks.length === 0 ? (
                        <div className="text-center py-12 text-muted-foreground">
                            <Package className="h-16 w-16 mx-auto mb-4 opacity-20" />
                            <p>No completed tasks</p>
                        </div>
                    ) : (
                        <div className="grid gap-4 md:grid-cols-2">
                            {completedTasks.map((task) => (
                                <TaskCard key={task.id} task={task} />
                            ))}
                        </div>
                    )}
                </TabsContent>

                <TabsContent value="failed" className="space-y-4 mt-6">
                    {failedTasks.length === 0 ? (
                        <div className="text-center py-12 text-muted-foreground">
                            <Package className="h-16 w-16 mx-auto mb-4 opacity-20" />
                            <p>No failed tasks</p>
                        </div>
                    ) : (
                        <div className="grid gap-4 md:grid-cols-2">
                            {failedTasks.map((task) => (
                                <TaskCard key={task.id} task={task} />
                            ))}
                        </div>
                    )}
                </TabsContent>
            </Tabs>
        </div>
    );
}
