import { z } from "zod"

export const createOrderSchema = z.object({
    senderName: z.string().min(1, "Sender name is required"),
    senderPhone: z.string().min(10, "Phone number must be at least 10 digits"),
    senderAddress: z.string().min(1, "Sender address is required"),
    senderWardCode: z.string().min(1, "Sender ward is required"),

    receiverName: z.string().min(1, "Receiver name is required"),
    receiverPhone: z.string().min(10, "Phone number must be at least 10 digits"),
    receiverAddress: z.string().min(1, "Receiver address is required"),
    receiverWardCode: z.string().min(1, "Receiver ward is required"),

    weightKg: z.coerce.number().min(0.1, "Weight must be greater than 0"),
    lengthCm: z.coerce.number().optional(),
    widthCm: z.coerce.number().optional(),
    heightCm: z.coerce.number().optional(),

    description: z.string().optional(),
    codAmount: z.coerce.number().min(0).default(0),
    shippingFee: z.coerce.number().min(0, "Shipping fee is required"),

    // UI Only fields
    senderProvinceCode: z.string().optional(),
    receiverProvinceCode: z.string().optional(),
})

export type CreateOrderFormValues = z.infer<typeof createOrderSchema>
