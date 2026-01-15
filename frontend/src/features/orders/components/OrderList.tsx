import type { ColumnDef } from "@tanstack/react-table"
import { OrderStatus } from "../types"
import type { Order } from "../types"
import { DataTable } from "@/components/ui/data-table"
import { Badge } from "@/components/ui/badge"
import { format } from "date-fns"
import { Button } from "@/components/ui/button"
import { ArrowUpDown, Eye } from "lucide-react"

interface OrderListProps {
    data: Order[];
    loading?: boolean;
}

export const columns: ColumnDef<Order>[] = [
    {
        accessorKey: "trackingNumber",
        header: "Tracking Number",
    },
    {
        accessorKey: "senderName",
        header: "Sender",
    },
    {
        accessorKey: "receiverName",
        header: "Receiver",
    },
    {
        accessorKey: "status",
        header: "Status",
        cell: ({ row }) => {
            const status = row.getValue("status") as OrderStatus
            let variant: "default" | "secondary" | "destructive" | "outline" = "default"

            switch (status) {
                case OrderStatus.PENDING:
                    variant = "secondary"
                    break
                case OrderStatus.DELIVERED:
                    variant = "default" // or success if we had it
                    break
                case OrderStatus.CANCELLED:
                    variant = "destructive"
                    break
                default:
                    variant = "outline"
            }

            return <Badge variant={variant}>{status}</Badge>
        },
    },
    {
        accessorKey: "shippingFee",
        header: "Fee",
        cell: ({ row }) => {
            const amount = parseFloat(row.getValue("shippingFee"))
            const formatted = new Intl.NumberFormat("vi-VN", {
                style: "currency",
                currency: "VND",
            }).format(amount)
            return <div className="font-medium">{formatted}</div>
        },
    },
    {
        accessorKey: "createdAt",
        header: ({ column }) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    Created At
                    <ArrowUpDown className="ml-2 h-4 w-4" />
                </Button>
            )
        },
        cell: ({ row }) => {
            return <div>{format(new Date(row.getValue("createdAt")), "dd/MM/yyyy HH:mm")}</div>
        },
    },
    {
        id: "actions",
        cell: () => {
            // const order = row.original
            return (
                <Button variant="ghost" size="icon">
                    <Eye className="h-4 w-4" />
                </Button>
            )
        },
    },
]

export function OrderList({ data, loading }: OrderListProps) {
    if (loading) {
        return <div>Loading orders...</div>
    }
    return <DataTable columns={columns} data={data} />
}
