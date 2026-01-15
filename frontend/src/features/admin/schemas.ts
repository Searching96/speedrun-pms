import { z } from 'zod';

// Employee creation schemas
export const createEmployeeSchema = z.object({
    fullName: z.string().min(1, 'Full name is required'),
    phoneNumber: z.string().regex(/^[0-9]{10}$/, 'Phone number must be exactly 10 digits'),
    password: z.string().min(6, 'Password must be at least 6 characters'),
    email: z.string().email('Invalid email address'),
    officeId: z.string().uuid().optional(),
});

export type CreateEmployeeFormData = z.infer<typeof createEmployeeSchema>;

export const updateEmployeeSchema = z.object({
    fullName: z.string().min(1, 'Full name is required'),
    phoneNumber: z.string().regex(/^[0-9]{10}$/, 'Phone number must be exactly 10 digits'),
    email: z.string().email('Invalid email address'),
    password: z.string().min(6, 'Password must be at least 6 characters').optional().or(z.literal('')),
});

export type UpdateEmployeeFormData = z.infer<typeof updateEmployeeSchema>;

// Ward office creation schema
export const createWardOfficeSchema = z.object({
    warehouseName: z.string().min(1, 'Warehouse name is required'),
    warehouseEmail: z.string().email('Invalid warehouse email'),
    warehousePhoneNumber: z.string().regex(/^[0-9]{10,11}$/, 'Phone number must be 10-11 digits'),
    warehouseAddress: z.string().min(1, 'Warehouse address is required'),
    postOfficeName: z.string().min(1, 'Post office name is required'),
    postOfficeEmail: z.string().email('Invalid post office email'),
    postOfficePhoneNumber: z.string().regex(/^[0-9]{10,11}$/, 'Phone number must be 10-11 digits'),
    postOfficeAddress: z.string().min(1, 'Post office address is required'),
    provinceCode: z.string().optional(),
});

export type CreateWardOfficeFormData = z.infer<typeof createWardOfficeSchema>;

// Ward assignment schema
export const assignWardsSchema = z.object({
    officePairId: z.string().uuid('Invalid office pair ID'),
    wardCodes: z.array(z.string()).min(1, 'At least one ward must be selected'),
});

export type AssignWardsFormData = z.infer<typeof assignWardsSchema>;
