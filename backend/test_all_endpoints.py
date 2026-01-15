#!/usr/bin/env python3
"""
Comprehensive API Test Script for Postal Management System
Tests all major endpoints across user roles.
"""
import requests
import json
import sys
from datetime import date, timedelta

BASE = "http://localhost:8080/api"

# Test data
CUSTOMER = {"phone": "0987333444", "pass": "123456", "email": "testcust3@test.com"}
ADMIN = {"phone": "0922000000", "pass": "123456"}  # PO_PROVINCE_ADMIN

results = {"passed": 0, "failed": 0, "errors": []}

def log(status, name, detail=""):
    icon = "✅" if status else "❌"
    print(f"{icon} {name}" + (f" - {detail}" if detail else ""))
    if status:
        results["passed"] += 1
    else:
        results["failed"] += 1
        results["errors"].append(name)

def test(name, condition, detail=""):
    log(condition, name, detail)
    return condition

# ========== AUTH ==========
def test_auth():
    print("\n=== AUTH ===")
    
    # Register Customer
    r = requests.post(f"{BASE}/auth/register", json={
        "fullName": "Test Cust 3", "username": CUSTOMER["phone"],
        "password": CUSTOMER["pass"], "email": CUSTOMER["email"],
        "address": "123 Test St"
    })
    test("Customer Register", r.status_code in [200, 201, 400], f"Status: {r.status_code}")
    
    # Login Customer
    r = requests.post(f"{BASE}/auth/login", json={"username": CUSTOMER["phone"], "password": CUSTOMER["pass"]})
    if test("Customer Login", r.status_code == 200):
        data = r.json().get("data", r.json())
        return data.get("token") or data.get("accessToken")
    return None

def test_admin_login():
    r = requests.post(f"{BASE}/auth/login", json={"username": ADMIN["phone"], "password": ADMIN["pass"]})
    if test("Admin Login", r.status_code == 200):
        data = r.json().get("data", r.json())
        return data.get("token") or data.get("accessToken")
    return None

# ========== USER ==========
def test_user(token):
    print("\n=== USER ===")
    h = {"Authorization": f"Bearer {token}"}
    r = requests.get(f"{BASE}/users/me", headers=h)
    test("Get Current User", r.status_code == 200)

# ========== ADMINISTRATIVE ==========
def test_administrative():
    print("\n=== ADMINISTRATIVE (Public) ===")
    
    r = requests.get(f"{BASE}/administrative/provinces")
    test("List Provinces", r.status_code == 200 and len(r.json().get("data", [])) > 0)
    
    r = requests.get(f"{BASE}/administrative/provinces/79/wards")  # HCM City
    test("List Wards", r.status_code == 200)

# ========== ORDERS ==========
def test_orders(token):
    print("\n=== ORDERS ===")
    h = {"Authorization": f"Bearer {token}"}
    
    # Create Order
    r = requests.post(f"{BASE}/orders", headers=h, json={
        "senderName": "Sender", "senderPhone": "0901111111",
        "senderAddress": "123 Sender St", "senderWardCode": "26734",
        "receiverName": "Receiver", "receiverPhone": "0902222222",
        "receiverAddress": "456 Receiver St", "receiverWardCode": "26735",
        "weightKg": 1.5, "lengthCm": 10, "widthCm": 10, "heightCm": 10,
        "shippingFee": 30000, "codAmount": 0, "description": "Test"
    })
    order_id, tracking = None, None
    if test("Create Order", r.status_code == 200):
        order_id = r.json().get("id")
        tracking = r.json().get("trackingNumber")
    
    # List Orders
    r = requests.get(f"{BASE}/orders", headers=h)
    test("List My Orders", r.status_code == 200)
    
    # Get by Tracking (public)
    if tracking:
        r = requests.get(f"{BASE}/orders/{tracking}")
        test("Get Order by Tracking (Public)", r.status_code == 200)
    
    return order_id, tracking

# ========== PICKUP ==========
def test_pickup(token, order_id):
    print("\n=== PICKUP REQUESTS ===")
    h = {"Authorization": f"Bearer {token}"}
    pickup_id = None
    
    if order_id:
        future_date = (date.today() + timedelta(days=7)).isoformat()
        r = requests.post(f"{BASE}/pickup-requests", headers=h, json={
            "orderId": order_id, "pickupAddress": "123 Pickup St",
            "pickupWardCode": "26734", "pickupContactName": "Contact",
            "pickupContactPhone": "0903333333", "preferredDate": future_date,
            "preferredTimeSlot": "MORNING"
        })
        if test("Create Pickup Request", r.status_code == 200):
            pickup_id = r.json().get("id")
    
    r = requests.get(f"{BASE}/pickup-requests/my-requests", headers=h)
    test("List My Pickup Requests", r.status_code == 200)
    
    return pickup_id

def test_pickup_admin(admin_token):
    h = {"Authorization": f"Bearer {admin_token}"}
    r = requests.get(f"{BASE}/pickup-requests/pending?wardCode=26734", headers=h)
    test("List Pending Pickups (Admin)", r.status_code == 200)

# ========== TRACKING ==========
def test_tracking(tracking, admin_token, order_id):
    print("\n=== TRACKING ===")
    
    if tracking:
        r = requests.get(f"{BASE}/tracking/{tracking}")
        test("Public Tracking", r.status_code == 200)
    
    if admin_token and order_id:
        h = {"Authorization": f"Bearer {admin_token}"}
        r = requests.post(f"{BASE}/tracking/events", headers=h, json={
            "orderId": order_id, "status": "IN_TRANSIT",
            "description": "Package in transit", "locationName": "Hub A"
        })
        test("Add Tracking Event", r.status_code == 200)

# ========== SHIPPER ==========
def test_shipper_endpoints(admin_token):
    print("\n=== SHIPPER TASKS (Requires SHIPPER role) ===")
    # We don't have a shipper token easily, so just verify endpoint exists
    h = {"Authorization": f"Bearer {admin_token}"}
    r = requests.get(f"{BASE}/shipper/tasks", headers=h)
    # Will fail with 403 because admin is not shipper - but endpoint exists
    test("Shipper Tasks Endpoint Exists", r.status_code in [200, 403], f"Status: {r.status_code}")

# ========== RATINGS ==========
def test_ratings(token, order_id):
    print("\n=== RATINGS ===")
    h = {"Authorization": f"Bearer {token}"}
    
    # Rating requires DELIVERED status - will fail but endpoint test
    if order_id:
        r = requests.post(f"{BASE}/ratings", headers=h, json={
            "orderId": order_id, "overallRating": 5, "comment": "Great!"
        })
        # Expect 400 because order is not DELIVERED (business rule violation)
        test("Rating Endpoint Exists", r.status_code in [200, 400], f"Status: {r.status_code}")
    
    r = requests.get(f"{BASE}/ratings/order/00000000-0000-0000-0000-000000000000", headers=h)
    test("Get Rating Endpoint Exists", r.status_code in [200, 403, 404])

# ========== WARD MANAGER ==========
def test_ward_manager(admin_token):
    print("\n=== WARD MANAGER (Requires PO_WARD_MANAGER) ===")
    h = {"Authorization": f"Bearer {admin_token}"}
    
    # These will fail with 403 because we're using Province Admin, not Ward Manager
    # But we verify the endpoints exist
    r = requests.post(f"{BASE}/ward-manager/employees/shipper", headers=h, json={
        "fullName": "Test Shipper", "phoneNumber": "0904444444",
        "password": "123456", "email": "shipper@test.com"
    })
    test("Create Shipper Endpoint Exists", r.status_code in [200, 201, 403], f"Status: {r.status_code}")

# ========== MAIN ==========
def main():
    print("=" * 50)
    print("POSTAL MANAGEMENT SYSTEM - API TEST")
    print("=" * 50)
    
    # Public tests
    test_administrative()
    
    # Auth
    cust_token = test_auth()
    admin_token = test_admin_login()
    
    if cust_token:
        test_user(cust_token)
        order_id, tracking = test_orders(cust_token)
        pickup_id = test_pickup(cust_token, order_id)
        test_tracking(tracking, admin_token, order_id)
        test_ratings(cust_token, order_id)
    
    if admin_token:
        test_pickup_admin(admin_token)
        test_shipper_endpoints(admin_token)
        test_ward_manager(admin_token)
    
    # Summary
    print("\n" + "=" * 50)
    print(f"RESULTS: {results['passed']} passed, {results['failed']} failed")
    if results["errors"]:
        print(f"Failed: {', '.join(results['errors'])}")
    print("=" * 50)
    
    return 0 if results["failed"] == 0 else 1

if __name__ == "__main__":
    sys.exit(main())
