import { useEffect, useState } from "react"
import { OrderList } from "@/features/orders/components/OrderList"
import { CreateOrderDialog } from "@/features/orders/components/CreateOrderDialog"
import { orderApi } from "@/features/orders/api"
import type { Order } from "@/features/orders/types"

export function OrdersPage() {
    const [orders, setOrders] = useState<Order[]>([])
    const [loading, setLoading] = useState(true)

    const fetchOrders = async () => {
        setLoading(true)
        try {
            const response = await orderApi.getMyOrders(0, 20)
            // response might be PageOrderResponse or { content: ... }
            // Based on API impl, it returns PageOrderResponse directly
            setOrders(response.content || [])
        } catch (error) {
            console.error("Failed to fetch orders", error)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        fetchOrders()
    }, [])

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">My Orders</h1>
                    <p className="text-muted-foreground">
                        Manage your shipments and track their status.
                    </p>
                </div>
                <CreateOrderDialog onOrderCreated={fetchOrders} />
            </div>

            <OrderList data={orders} loading={loading} />
        </div>
    )
}
