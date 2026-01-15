import { useState } from "react";
import type { ColumnDef } from "@tanstack/react-table";
import { OrderStatus } from "../types";
import type { Order } from "../types"
import { DataTable } from "@/components/ui/data-table"
import { Badge } from "@/components/ui/badge"
import { format } from "date-fns"
import { Button } from "@/components/ui/button"
import { ArrowUpDown, Eye, Star } from "lucide-react"
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/features/auth/AuthProvider";
import { ROLES } from "@/features/auth/roles";
import { RatingModal } from "@/features/ratings/components/RatingModal"

interface OrderListProps {
    data: Order[];
    loading?: boolean;
}

export function OrderList({ data, loading }: OrderListProps) {
    const [ratingModalOpen, setRatingModalOpen] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);

    const handleRateClick = (order: Order) => {
        setSelectedOrder(order);
        setRatingModalOpen(true);
    };

    const columns: ColumnDef<Order>[] = [
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
            cell: ({ row }) => {
                const order = row.original
                const isDelivered = order.status === OrderStatus.DELIVERED
                const { user } = useAuth();
                const navigate = useNavigate();

                const handleViewDetails = () => {
                    if (!user) return;

                    let basePath = "";
                    const userRole = user.role;

                    if (ROLES.ADMIN_GROUP.includes(userRole as any) || ROLES.MANAGER_GROUP.includes(userRole as any)) {
                        basePath = "/admin";
                    } else if (ROLES.STAFF_GROUP.includes(userRole as any)) {
                        basePath = "/staff";
                    }

                    navigate(`${basePath}/orders/${order.trackingNumber}`);
                };

                return (
                    <div className="flex gap-2">
                        <Button variant="ghost" size="icon" onClick={handleViewDetails} title="View Details">
                            <Eye className="h-4 w-4" />
                        </Button>
                        {isDelivered && (
                            <Button
                                variant="ghost"
                                size="icon"
                                onClick={() => handleRateClick(order)}
                                title="Rate Service"
                            >
                                <Star className="h-4 w-4" />
                            </Button>
                        )}
                    </div>
                )
            },
        },
    ]

    if (loading) {
        return <div>Loading orders...</div>
    }

    return (
        <>
            <DataTable columns={columns} data={data} />
            {selectedOrder && (
                <RatingModal
                    open={ratingModalOpen}
                    onClose={() => {
                        setRatingModalOpen(false);
                        setSelectedOrder(null);
                    }}
                    orderId={selectedOrder.id}
                    trackingNumber={selectedOrder.trackingNumber}
                />
            )}
        </>
    )
}
