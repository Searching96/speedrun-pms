package org.f3.postalmanagement.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.entity.administrative.AdministrativeRegion;
import org.f3.postalmanagement.entity.administrative.Province;
import org.f3.postalmanagement.entity.administrative.Ward;
import org.f3.postalmanagement.entity.order.Order;
import org.f3.postalmanagement.entity.order.TrackingEvent;
import org.f3.postalmanagement.entity.pricing.PricingZone;
import org.f3.postalmanagement.entity.pricing.ShippingRate;
import org.f3.postalmanagement.entity.pricing.WardZoneMapping;
import org.f3.postalmanagement.entity.unit.Office;
import org.f3.postalmanagement.enums.OfficeType;
import org.f3.postalmanagement.enums.OrderStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.enums.SubscriptionPlan;
import org.f3.postalmanagement.repository.*;
import org.f3.postalmanagement.repository.pricing.PricingZoneRepository;
import org.f3.postalmanagement.repository.pricing.ShippingRateRepository;
import org.f3.postalmanagement.repository.pricing.WardZoneMappingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OfficeRepository officeRepository;
    private final AdRegionRepository adRegionRepository;
    private final ProvinceRepository provinceRepository;
    private final EmployeeRepository employeeRepository;
    private final WardRepository wardRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final PricingZoneRepository pricingZoneRepository;
    private final ShippingRateRepository shippingRateRepository;
    private final WardZoneMappingRepository wardZoneMappingRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initSystemAdmin();
        Map<Integer, Office> hubs = initHubForEachRegion();
        initProvinceOffices(hubs);
        
        // De-fake: Seed pricing, customers and orders
        initPricingData();
        List<Customer> customers = initCustomers();
        initOrders(customers);
    }

    private void initSystemAdmin() {
        if (accountRepository.existsByRole(Role.SYSTEM_ADMIN)) {
            log.debug("Exist super admin account.");
        } else {
            Account account = new Account();
            account.setUsername("0000000000");
            account.setPassword(passwordEncoder.encode("123456"));
            account.setRole(Role.SYSTEM_ADMIN);
            account.setEmail("sadmin@f3postal.com");
            account.setActive(true);
            accountRepository.save(account);
            log.info("Created super admin account.");
        }
    }

    private Map<Integer, Office> initHubForEachRegion() {
        Map<Integer, Office> hubsByRegion = new HashMap<>();
        
        if (officeRepository.existsByOfficeType(OfficeType.HUB)) {
            log.debug("Hubs already exist.");
            // Load existing hubs into map
            List<Office> existingHubs = officeRepository.findAllByOfficeType(OfficeType.HUB);
            for (Office hub : existingHubs) {
                hubsByRegion.put(hub.getRegion().getId(), hub);
            }
        } else {
            List<AdministrativeRegion> regions = adRegionRepository.findAll();
            
            for (AdministrativeRegion region : regions) {
                Office hub = new Office();
                hub.setOfficeName("HUB " + region.getName());
                hub.setOfficeEmail("hub" + region.getId() + "@f3postal.com");
                hub.setOfficePhoneNumber("190000000" + region.getId());
                hub.setOfficeAddress("Address HUB " + region.getName());
                hub.setRegion(region);
                hub.setOfficeType(OfficeType.HUB);
                
                Office savedHub = officeRepository.save(hub);
                hubsByRegion.put(region.getId(), savedHub);
                
                // Create HUB_ADMIN for this hub
                createOfficeManager(savedHub, Role.HUB_ADMIN, "hub.admin" + region.getId(), "090000000" + region.getId());
                
                log.info("Created HUB for region: {}", region.getName());
            }
            
            log.info("Initialized {} HUBs for all regions.", regions.size());
        }
        
        return hubsByRegion;
    }

    private void initProvinceOffices(Map<Integer, Office> hubsByRegion) {
        if (officeRepository.existsByOfficeType(OfficeType.PROVINCE_WAREHOUSE)) {
            log.debug("Province offices already exist.");
            return;
        }

        List<Province> provinces = provinceRepository.findAll();
        
        for (Province province : provinces) {
            AdministrativeRegion region = province.getAdministrativeRegion();
            Office parentHub = hubsByRegion.get(region.getId());
            
            // Create PROVINCE_WAREHOUSE
            Office warehouse = new Office();
            warehouse.setOfficeName("Kho " + province.getName());
            warehouse.setOfficeEmail("warehouse." + province.getCode() + "@f3postal.com");
            warehouse.setOfficePhoneNumber("1900" + province.getCode() + "00");
            warehouse.setOfficeAddress("Địa chỉ Kho " + province.getName());
            warehouse.setRegion(region);
            warehouse.setProvince(province);
            warehouse.setParent(parentHub);
            warehouse.setOfficeType(OfficeType.PROVINCE_WAREHOUSE);
            Office savedWarehouse = officeRepository.save(warehouse);
            
            // Create WH_PROVINCE_ADMIN for this warehouse
            createOfficeManager(savedWarehouse, Role.WH_PROVINCE_ADMIN, "wh.admin." + province.getCode(), "091" + province.getCode() + "00000");
            
            // Create PROVINCE_POST
            Office postOffice = new Office();
            postOffice.setOfficeName("Bưu cục " + province.getName());
            postOffice.setOfficeEmail("post." + province.getCode() + "@f3postal.com");
            postOffice.setOfficePhoneNumber("1900" + province.getCode() + "01");
            postOffice.setOfficeAddress("Địa chỉ Bưu cục " + province.getName());
            postOffice.setRegion(region);
            postOffice.setProvince(province);
            postOffice.setParent(parentHub);
            postOffice.setOfficeType(OfficeType.PROVINCE_POST);
            Office savedPostOffice = officeRepository.save(postOffice);
            
            // Create PO_PROVINCE_ADMIN for this post office
            createOfficeManager(savedPostOffice, Role.PO_PROVINCE_ADMIN, "po.admin." + province.getCode(), "092" + province.getCode() + "00000");
            
            log.info("Created PROVINCE_WAREHOUSE and PROVINCE_POST for province: {}", province.getName());
        }
        
        log.info("Initialized province offices for {} provinces.", provinces.size());
    }

    private void createOfficeManager(Office office, Role role, String emailPrefix, String phoneNumber) {
        // Create account
        Account account = new Account();
        account.setUsername(phoneNumber);
        account.setPassword(passwordEncoder.encode("123456"));
        account.setRole(role);
        account.setEmail(emailPrefix + "@f3postal.com");
        account.setActive(true);
        Account savedAccount = accountRepository.save(account);
        
        // Create employee
        Employee employee = new Employee();
        employee.setAccount(savedAccount);
        employee.setFullName("Manager " + office.getOfficeName());
        employee.setPhoneNumber(phoneNumber);
        employee.setOffice(office);
        employeeRepository.save(employee);
        
        log.info("Created {} for office: {}", role, office.getOfficeName());
    }

    private void initPricingData() {
        if (pricingZoneRepository.count() > 0) {
            log.debug("Pricing data already exists.");
            return;
        }

        // Create Zones
        PricingZone coreZone = pricingZoneRepository.save(PricingZone.builder()
                .code("CORE")
                .name("Core City Zone")
                .description("Major cities like HCM and Hanoi")
                .build());

        PricingZone regionalZone = pricingZoneRepository.save(PricingZone.builder()
                .code("REGIONAL")
                .name("Regional Zone")
                .description("Provinces in the same region")
                .build());

        PricingZone nationalZone = pricingZoneRepository.save(PricingZone.builder()
                .code("NATIONAL")
                .name("National Zone")
                .description("Inter-region shipping")
                .build());

        // Create Rates (Bi-directional)
        // CORE <-> CORE
        shippingRateRepository.save(ShippingRate.builder()
                .fromZone(coreZone).toZone(coreZone)
                .basePrice(new BigDecimal("15000")).pricePerKg(new BigDecimal("2000"))
                .validFrom(LocalDateTime.now()).isActive(true).build());

        // CORE <-> REGIONAL
        shippingRateRepository.save(ShippingRate.builder()
                .fromZone(coreZone).toZone(regionalZone)
                .basePrice(new BigDecimal("25000")).pricePerKg(new BigDecimal("5000"))
                .validFrom(LocalDateTime.now()).isActive(true).build());
        shippingRateRepository.save(ShippingRate.builder()
                .fromZone(regionalZone).toZone(coreZone)
                .basePrice(new BigDecimal("25000")).pricePerKg(new BigDecimal("5000"))
                .validFrom(LocalDateTime.now()).isActive(true).build());

        // CORE <-> NATIONAL
        shippingRateRepository.save(ShippingRate.builder()
                .fromZone(coreZone).toZone(nationalZone)
                .basePrice(new BigDecimal("35000")).pricePerKg(new BigDecimal("8000"))
                .validFrom(LocalDateTime.now()).isActive(true).build());
        shippingRateRepository.save(ShippingRate.builder()
                .fromZone(nationalZone).toZone(coreZone)
                .basePrice(new BigDecimal("35000")).pricePerKg(new BigDecimal("8000"))
                .validFrom(LocalDateTime.now()).isActive(true).build());

        // REGIONAL <-> REGIONAL
        shippingRateRepository.save(ShippingRate.builder()
                .fromZone(regionalZone).toZone(regionalZone)
                .basePrice(new BigDecimal("20000")).pricePerKg(new BigDecimal("3000"))
                .validFrom(LocalDateTime.now()).isActive(true).build());

        // Map wards to different zones
        List<Ward> allWards = wardRepository.findAll();
        for (int i = 0; i < allWards.size(); i++) {
            PricingZone zone;
            if (i < 500) zone = coreZone;
            else if (i < 2000) zone = regionalZone;
            else zone = nationalZone;
            
            wardZoneMappingRepository.save(WardZoneMapping.builder()
                    .ward(allWards.get(i))
                    .zone(zone)
                    .build());
        }
        
        log.info("Initialized Pricing Zones, Rates (Full Matrix), and all Ward Mappings.");
    }

    private List<Customer> initCustomers() {
        if (customerRepository.count() > 0) {
            log.debug("Customers already exist.");
            return customerRepository.findAll();
        }

        List<Customer> customers = new ArrayList<>();
        String[] specificPhones = {"0912345678", "0987654321", "0900112233", "0944556677", "0966778899"};
        String[] names = {"Nguyen Van A", "Tran Thi B", "Le Van C", "Pham Thi D", "Hoang Van E"};

        for (int i = 0; i < names.length; i++) {
            Account account = new Account();
            account.setUsername(specificPhones[i]);
            account.setPassword(passwordEncoder.encode("123456"));
            account.setRole(Role.CUSTOMER);
            account.setEmail("customer" + i + "@example.com");
            account.setActive(true);
            Account savedAccount = accountRepository.save(account);

            Customer customer = new Customer();
            customer.setAccount(savedAccount);
            customer.setFullName(names[i]);
            customer.setPhoneNumber(specificPhones[i]);
            customer.setAddress("Sample Address " + (i + 1));
            customer.setSubscriptionPlan(SubscriptionPlan.BASIC);
            customers.add(customerRepository.save(customer));
        }

        log.info("Initialized 5 sample customers.");
        return customers;
    }

    private void initOrders(List<Customer> customers) {
        if (orderRepository.count() > 0) {
            log.debug("Orders already exist.");
            return;
        }

        Random random = new Random();
        List<Ward> wards = wardRepository.findAll();
        if (wards.isEmpty()) return;

        // Realistic product names
        String[] products = {"Documents", "Electronics", "Clothing", "Books", "Cosmetics", 
                           "Toys", "Food Items", "Medicines", "Stationery", "Accessories"};
        String[] senderCompanies = {"TechCorp Inc.", "FashionHub Ltd.", "BookWorld", 
                                   "MediCare Pharma", "HomeGoods Retail"};
        String[] receiverCompanies = {"Global Imports", "City Mart", "Online Retailer", 
                                     "Corporate Office", "Residential"};

        // Create more realistic distribution: more delivered orders for realistic stats
        int[] statusCounts = {3, 2, 4, 6, 5}; // PENDING, PROCESSING, IN_TRANSIT, DELIVERED, CANCELLED

        int orderCounter = 0;

        for (int statusIdx = 0; statusIdx < statusCounts.length; statusIdx++) {
            OrderStatus status = OrderStatus.values()[statusIdx];
            int countForStatus = statusCounts[statusIdx];

            for (int i = 0; i < countForStatus; i++) {
                orderCounter++;
                Customer customer = customers.get(random.nextInt(customers.size()));
                Ward senderWard = wards.get(random.nextInt(wards.size()));
                Ward receiverWard = wards.get(random.nextInt(wards.size()));

                // Calculate realistic shipping fee based on zones
                BigDecimal weight = new BigDecimal(random.nextDouble() * 9.5 + 0.5); // 0.5-10kg
                BigDecimal baseFee = new BigDecimal(15000 + random.nextInt(20000));
                BigDecimal shippingFee = baseFee.add(weight.multiply(new BigDecimal(2000)));

                Order order = Order.builder()
                        .trackingNumber("PM" + String.format("%08d", orderCounter))
                        .customer(customer)
                        .senderName(senderCompanies[random.nextInt(senderCompanies.length)])
                        .senderPhone(customer.getPhoneNumber())
                        .senderAddress(customer.getAddress())
                        .senderWardCode(senderWard.getCode())
                        .receiverName(receiverCompanies[random.nextInt(receiverCompanies.length)])
                        .receiverPhone("09" + String.format("%08d", random.nextInt(100000000)))
                        .receiverAddress("Receiver Address " + orderCounter + ", " + receiverWard.getName())
                        .receiverWardCode(receiverWard.getCode())
                        .description(products[random.nextInt(products.length)] + " - Priority Shipping")
                        .weightKg(weight)
                        .shippingFee(shippingFee)
                        // .estimatedDelivery(LocalDateTime.now().plusDays(random.nextInt(5) + 1))
                        .status(status)
                        .build();

                Order savedOrder = orderRepository.save(order);

                // Add comprehensive tracking history
                addEnhancedTrackingHistory(savedOrder);
            }
        }

        log.info("Initialized {} sample orders with realistic tracking history.", orderCounter);
    }

    private void addEnhancedTrackingHistory(Order order) {
        LocalDateTime baseTime = order.getCreatedAt().minusDays(2);
        String senderWard = "Ward " + order.getSenderWardCode().substring(0, 3);
        String receiverWard = "Ward " + order.getReceiverWardCode().substring(0, 3);
        
        // CREATED
        trackingEventRepository.save(TrackingEvent.builder()
                .order(order)
                .status("CREATED")
                .description("Order registered in system")
                .locationName("Online Portal")
                .eventTime(baseTime)
                .build());

        if (order.getStatus() != OrderStatus.PENDING) {
            // PROCESSING
            trackingEventRepository.save(TrackingEvent.builder()
                    .order(order)
                    .status("PROCESSING")
                    .description("Package processing started")
                    .locationName("Sorting Facility")
                    .eventTime(baseTime.plusHours(2))
                    .build());

            // PICKED_UP
            trackingEventRepository.save(TrackingEvent.builder()
                    .order(order)
                    .status("PICKED_UP")
                    .description("Package collected from sender")
                    .locationName(senderWard + " Collection Point")
                    .eventTime(baseTime.plusHours(4))
                    .build());

            // IN_TRANSIT events
            if (order.getStatus() != OrderStatus.CANCELLED) {
                trackingEventRepository.save(TrackingEvent.builder()
                        .order(order)
                        .status("IN_TRANSIT")
                        .description("Package en route to regional hub")
                        .locationName("Regional Transit Center")
                        .eventTime(baseTime.plusHours(8))
                        .build());

                trackingEventRepository.save(TrackingEvent.builder()
                        .order(order)
                        .status("IN_TRANSIT")
                        .description("Package arrived at destination city")
                        .locationName(receiverWard + " Distribution")
                        .eventTime(baseTime.plusDays(1))
                        .build());

                // OUT_FOR_DELIVERY for delivered orders
                if (order.getStatus() == OrderStatus.DELIVERED) {
                    trackingEventRepository.save(TrackingEvent.builder()
                            .order(order)
                            .status("OUT_FOR_DELIVERY")
                            .description("Package with delivery agent")
                            .locationName(receiverWard + " Delivery Unit")
                            .eventTime(baseTime.plusDays(1).plusHours(6))
                            .build());

                    // DELIVERED
                    trackingEventRepository.save(TrackingEvent.builder()
                            .order(order)
                            .status("DELIVERED")
                            .description("Package successfully delivered")
                            .locationName(order.getReceiverAddress())
                            .eventTime(baseTime.plusDays(1).plusHours(8))
                            .build());
                }
            } else {
                // CANCELLED status
                trackingEventRepository.save(TrackingEvent.builder()
                        .order(order)
                        .status("CANCELLED")
                        .description("Order cancelled per customer request")
                        .locationName("Customer Service")
                        .eventTime(baseTime.plusHours(3))
                        .build());
            }
        }
    }
}

