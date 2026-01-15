import { useState } from "react"
import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { CreateOrderForm } from "./CreateOrderForm"
import { Plus } from "lucide-react"

interface CreateOrderDialogProps {
    onOrderCreated?: () => void
}

export function CreateOrderDialog({ onOrderCreated }: CreateOrderDialogProps) {
    const [open, setOpen] = useState(false)

    const handleSuccess = () => {
        setOpen(false)
        onOrderCreated?.()
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button>
                    <Plus className="mr-2 h-4 w-4" />
                    Create Order
                </Button>
            </DialogTrigger>
            <DialogContent className="max-h-[90vh] overflow-y-auto max-w-4xl">
                <DialogHeader>
                    <DialogTitle>Create New Order</DialogTitle>
                    <DialogDescription>
                        Fill in the sender and receiver details to create a shipment.
                    </DialogDescription>
                </DialogHeader>
                <CreateOrderForm onSuccess={handleSuccess} onCancel={() => setOpen(false)} />
            </DialogContent>
        </Dialog>
    )
}
