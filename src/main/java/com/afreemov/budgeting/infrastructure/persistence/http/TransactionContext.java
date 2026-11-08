package com.afreemov.budgeting.infrastructure.persistence.http;

public class TransactionContext {
    private static final ThreadLocal<ContextData> context = new ThreadLocal<>();

    public record ContextData(String sourceOrigin, String sourceFileName, String createdBy) {}

    public static void set(String sourceOrigin, String sourceFileName, String createdBy) {
        context.set(new ContextData(sourceOrigin, sourceFileName, createdBy));
    }

    public static ContextData get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
