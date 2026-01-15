type LogLevel = 'debug' | 'info' | 'warn' | 'error';

interface LogEntry {
    level: LogLevel;
    message: string;
    details?: unknown;
    timestamp: string;
}

class LoggerService {
    private isDevelopment = import.meta.env.DEV;

    private formatEntry(level: LogLevel, message: string, details?: unknown): LogEntry {
        return {
            level,
            message,
            details,
            timestamp: new Date().toISOString(),
        };
    }

    private print(entry: LogEntry) {
        if (this.isDevelopment) {
            const styles = {
                debug: 'color: #808080',
                info: 'color: #00bfff',
                warn: 'color: #ffa500',
                error: 'color: #ff0000; font-weight: bold',
            };

            console.groupCollapsed(
                `%c[${entry.timestamp}] [${entry.level.toUpperCase()}]: ${entry.message}`,
                styles[entry.level]
            );
            if (entry.details) {
                console.log('Details:', entry.details);
            }
            console.trace('Stack Trace');
            console.groupEnd();
        } else {
            // PROD: Send to logging service (Sentry, Datadog, etc.)
            this.sendToMonitoring(entry);
        }
    }

    private sendToMonitoring(entry: LogEntry) {
        // TODO: Integrate actual monitoring service here
        // Example: Sentry.captureMessage(entry.message, { level: entry.level, extra: entry.details });

        // Fallback security: Don't let errors go completely silent even in prod if monitoring fails
        if (entry.level === 'error') {
            console.error(entry.message, entry.details);
        }
    }

    debug(message: string, details?: unknown) {
        this.print(this.formatEntry('debug', message, details));
    }

    info(message: string, details?: unknown) {
        this.print(this.formatEntry('info', message, details));
    }

    warn(message: string, details?: unknown) {
        this.print(this.formatEntry('warn', message, details));
    }

    error(message: string, details?: unknown) {
        this.print(this.formatEntry('error', message, details));
    }
}

export const logger = new LoggerService();
