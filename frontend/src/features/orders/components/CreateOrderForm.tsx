import { useEffect, useState } from "react"
import { useForm, type SubmitHandler } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Loader2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select"
import { createOrderSchema, type CreateOrderFormValues } from "../schema"
import { locationApi } from "@/features/location/api"
import type { Province, Ward } from "@/features/location/types"
import { orderApi } from "../api"

interface CreateOrderFormProps {
    onSuccess?: () => void
    onCancel?: () => void
}

export function CreateOrderForm({ onSuccess, onCancel }: CreateOrderFormProps) {
    const [loading, setLoading] = useState(false)
    const [provinces, setProvinces] = useState<Province[]>([])

    const [senderWards, setSenderWards] = useState<Ward[]>([])
    const [receiverWards, setReceiverWards] = useState<Ward[]>([])

    const form = useForm<CreateOrderFormValues>({
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        resolver: zodResolver(createOrderSchema) as any,
        defaultValues: {
            senderName: "",
            senderPhone: "",
            senderAddress: "",
            senderWardCode: "",
            receiverName: "",
            receiverPhone: "",
            receiverAddress: "",
            receiverWardCode: "",
            weightKg: 1,
            shippingFee: 0,
            codAmount: 0,
        },
    })

    // Watch province codes to fetch wards
    const senderProvinceCode = form.watch("senderProvinceCode")
    const receiverProvinceCode = form.watch("receiverProvinceCode")

    // Fetch provinces on mount
    useEffect(() => {
        locationApi.getProvinces()
            .then(setProvinces)
            .catch((err) => console.error("Failed to fetch provinces", err))
    }, [])

    // Fetch wards when sender province changes
    useEffect(() => {
        if (senderProvinceCode) {
            locationApi.getWardsByProvince(senderProvinceCode)
                .then(setSenderWards)
                .catch((err) => console.error("Failed to fetch sender wards", err))
        } else {
            setSenderWards([])
        }
    }, [senderProvinceCode])

    // Fetch wards when receiver province changes
    useEffect(() => {
        if (receiverProvinceCode) {
            locationApi.getWardsByProvince(receiverProvinceCode)
                .then(setReceiverWards)
                .catch((err) => console.error("Failed to fetch receiver wards", err))
        } else {
            setReceiverWards([])
        }
    }, [receiverProvinceCode])

    const onSubmit: SubmitHandler<CreateOrderFormValues> = async (values) => {
        setLoading(true)
        try {
            await orderApi.createOrder(values)
            onSuccess?.()
        } catch (error) {
            console.error("Failed to create order", error)
            // Ideally show toast error
        } finally {
            setLoading(false)
        }
    }

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                    {/* Sender Information */}
                    <div className="space-y-4 rounded-lg border p-4">
                        <h3 className="font-semibold">Sender Information</h3>
                        <FormField
                            control={form.control}
                            name="senderName"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Name</FormLabel>
                                    <FormControl>
                                        <Input placeholder="Sender Name" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="senderPhone"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Phone</FormLabel>
                                    <FormControl>
                                        <Input placeholder="Sender Phone" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="senderProvinceCode"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Province</FormLabel>
                                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                                        <FormControl>
                                            <SelectTrigger>
                                                <SelectValue placeholder="Select province" />
                                            </SelectTrigger>
                                        </FormControl>
                                        <SelectContent>
                                            {provinces.map((province) => (
                                                <SelectItem key={province.code} value={province.code}>
                                                    {province.name}
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="senderWardCode"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Ward</FormLabel>
                                    <Select onValueChange={field.onChange} defaultValue={field.value} disabled={!senderProvinceCode}>
                                        <FormControl>
                                            <SelectTrigger>
                                                <SelectValue placeholder="Select ward" />
                                            </SelectTrigger>
                                        </FormControl>
                                        <SelectContent>
                                            {senderWards.map((ward) => (
                                                <SelectItem key={ward.code} value={ward.code}>
                                                    {ward.name}
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="senderAddress"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Detailed Address</FormLabel>
                                    <FormControl>
                                        <Input placeholder="123 Street..." {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                    </div>

                    {/* Receiver Information */}
                    <div className="space-y-4 rounded-lg border p-4">
                        <h3 className="font-semibold">Receiver Information</h3>
                        <FormField
                            control={form.control}
                            name="receiverName"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Name</FormLabel>
                                    <FormControl>
                                        <Input placeholder="Receiver Name" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="receiverPhone"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Phone</FormLabel>
                                    <FormControl>
                                        <Input placeholder="Receiver Phone" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="receiverProvinceCode"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Province</FormLabel>
                                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                                        <FormControl>
                                            <SelectTrigger>
                                                <SelectValue placeholder="Select province" />
                                            </SelectTrigger>
                                        </FormControl>
                                        <SelectContent>
                                            {provinces.map((province) => (
                                                <SelectItem key={province.code} value={province.code}>
                                                    {province.name}
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="receiverWardCode"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Ward</FormLabel>
                                    <Select onValueChange={field.onChange} defaultValue={field.value} disabled={!receiverProvinceCode}>
                                        <FormControl>
                                            <SelectTrigger>
                                                <SelectValue placeholder="Select ward" />
                                            </SelectTrigger>
                                        </FormControl>
                                        <SelectContent>
                                            {receiverWards.map((ward) => (
                                                <SelectItem key={ward.code} value={ward.code}>
                                                    {ward.name}
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="receiverAddress"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Detailed Address</FormLabel>
                                    <FormControl>
                                        <Input placeholder="456 Street..." {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                    </div>
                </div>

                {/* Package Information */}
                <div className="space-y-4 rounded-lg border p-4">
                    <h3 className="font-semibold">Package Details</h3>
                    <div className="grid grid-cols-2 gap-4">
                        <FormField
                            control={form.control}
                            name="weightKg"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Weight (kg)</FormLabel>
                                    <FormControl>
                                        <Input type="number" step="0.1" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="codAmount"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>COD Amount (VND)</FormLabel>
                                    <FormControl>
                                        <Input type="number" {...field} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                    </div>
                    <FormField
                        control={form.control}
                        name="shippingFee"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Shipping Fee (VND)</FormLabel>
                                <FormControl>
                                    <Input type="number" {...field} />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="description"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Description</FormLabel>
                                <FormControl>
                                    <Input {...field} />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                </div>

                <div className="flex justify-end space-x-2">
                    <Button variant="outline" type="button" onClick={onCancel} disabled={loading}>
                        Cancel
                    </Button>
                    <Button type="submit" disabled={loading}>
                        {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                        Create Order
                    </Button>
                </div>
            </form>
        </Form>
    )
}
