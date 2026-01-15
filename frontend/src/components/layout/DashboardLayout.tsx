import { useState } from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import { Menu, Package2, LogOut, User } from 'lucide-react';
import { useAuth } from '@/features/auth';
import { NAV_ITEMS } from '@/config/nav';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';

export function DashboardLayout() {
    const { user, logout } = useAuth();
    const location = useLocation();
    const [isMobileOpen, setIsMobileOpen] = useState(false);

    // Filter nav items based on user role
    const filteredNavItems = NAV_ITEMS.filter((item) => {
        if (!item.roles) return true;
        return user?.role && item.roles.includes(user.role);
    });

    return (
        <div className="grid min-h-screen w-full md:grid-cols-[220px_1fr] lg:grid-cols-[280px_1fr]">
            <div className="hidden border-r bg-muted/40 md:block">
                <div className="flex h-full max-h-screen flex-col gap-2">
                    <div className="flex h-14 items-center border-b px-4 lg:h-[60px] lg:px-6">
                        <Link to="/" className="flex items-center gap-2 font-semibold">
                            <Package2 className="h-6 w-6" />
                            <span className="">Postal Manager</span>
                        </Link>
                    </div>
                    <div className="flex-1">
                        <nav className="grid items-start px-2 text-sm font-medium lg:px-4">
                            {filteredNavItems.map((item) => (
                                <Link
                                    key={item.href}
                                    to={item.href}
                                    className={cn(
                                        "flex items-center gap-3 rounded-lg px-3 py-2 transition-all hover:text-primary",
                                        location.pathname === item.href
                                            ? "bg-muted text-primary"
                                            : "text-muted-foreground"
                                    )}
                                >
                                    <item.icon className="h-4 w-4" />
                                    {item.title}
                                </Link>
                            ))}
                        </nav>
                    </div>
                    <div className="mt-auto p-4">
                        <Card className="bg-muted/50 p-4 border-0 shadow-none">
                            <h4 className="font-medium text-sm">Need Help?</h4>
                            <p className="text-xs text-muted-foreground mt-1">Contact system administrator for support.</p>
                        </Card>
                    </div>
                </div>
            </div>
            <div className="flex flex-col">
                <header className="flex h-14 items-center gap-4 border-b bg-muted/40 px-4 lg:h-[60px] lg:px-6">
                    <Sheet open={isMobileOpen} onOpenChange={setIsMobileOpen}>
                        <SheetTrigger asChild>
                            <Button
                                variant="outline"
                                size="icon"
                                className="shrink-0 md:hidden"
                            >
                                <Menu className="h-5 w-5" />
                                <span className="sr-only">Toggle navigation menu</span>
                            </Button>
                        </SheetTrigger>
                        <SheetContent side="left" className="flex flex-col">
                            <nav className="grid gap-2 text-lg font-medium">
                                <Link
                                    to="/"
                                    className="flex items-center gap-2 text-lg font-semibold"
                                    onClick={() => setIsMobileOpen(false)}
                                >
                                    <Package2 className="h-6 w-6" />
                                    <span className="sr-only">Postal Manager</span>
                                </Link>
                                {filteredNavItems.map((item) => (
                                    <Link
                                        key={item.href}
                                        to={item.href}
                                        onClick={() => setIsMobileOpen(false)}
                                        className={cn(
                                            "mx-[-0.65rem] flex items-center gap-4 rounded-xl px-3 py-2 hover:text-foreground",
                                            location.pathname === item.href
                                                ? "bg-muted text-foreground"
                                                : "text-muted-foreground"
                                        )}
                                    >
                                        <item.icon className="h-5 w-5" />
                                        {item.title}
                                    </Link>
                                ))}
                            </nav>
                        </SheetContent>
                    </Sheet>
                    <div className="w-full flex-1">
                    </div>
                    <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                            <Button variant="secondary" size="icon" className="rounded-full">
                                <Avatar className="h-8 w-8">
                                    <AvatarFallback><User className="h-4 w-4" /></AvatarFallback>
                                </Avatar>
                                <span className="sr-only">Toggle user menu</span>
                            </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                            <DropdownMenuLabel>My Account</DropdownMenuLabel>
                            <DropdownMenuSeparator />
                            <DropdownMenuItem disabled>Settings</DropdownMenuItem>
                            <DropdownMenuItem disabled>Support</DropdownMenuItem>
                            <DropdownMenuSeparator />
                            <DropdownMenuItem onClick={logout} className="text-destructive cursor-pointer">
                                <LogOut className="mr-2 h-4 w-4" />
                                Logout
                            </DropdownMenuItem>
                        </DropdownMenuContent>
                    </DropdownMenu>
                </header>
                <main className="flex flex-1 flex-col gap-4 p-4 lg:gap-6 lg:p-6">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
