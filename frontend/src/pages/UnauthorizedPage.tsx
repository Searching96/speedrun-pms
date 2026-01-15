

export function UnauthorizedPage() {
    return (
        <div className="flex min-h-screen flex-col items-center justify-center bg-background">
            <div className="space-y-4 text-center">
                <h1 className="text-4xl font-bold text-destructive">403</h1>
                <p className="text-lg text-muted-foreground">
                    You don't have permission to access this page.
                </p>
                <a
                    href="/"
                    className="inline-block rounded-md bg-primary px-4 py-2 text-primary-foreground transition-colors hover:bg-primary/90"
                >
                    Go Home
                </a>
            </div>
        </div>
    );
}
