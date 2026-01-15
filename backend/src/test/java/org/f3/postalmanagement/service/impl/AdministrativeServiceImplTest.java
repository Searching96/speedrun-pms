package org.f3.postalmanagement.service.impl;

import org.f3.postalmanagement.dto.response.PageResponse;
import org.f3.postalmanagement.dto.response.administrative.ProvinceResponse;
import org.f3.postalmanagement.dto.response.administrative.WardResponse;
import org.f3.postalmanagement.entity.administrative.AdministrativeRegion;
import org.f3.postalmanagement.entity.administrative.AdministrativeUnit;
import org.f3.postalmanagement.entity.administrative.Province;
import org.f3.postalmanagement.entity.administrative.Ward;
import org.f3.postalmanagement.repository.AdRegionRepository;
import org.f3.postalmanagement.repository.ProvinceRepository;
import org.f3.postalmanagement.repository.WardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdministrativeServiceImpl Path Coverage Tests")
class AdministrativeServiceImplTest {

    @Mock
    private ProvinceRepository provinceRepository;

    @Mock
    private WardRepository wardRepository;

    @Mock
    private AdRegionRepository adRegionRepository;

    @InjectMocks
    private AdministrativeServiceImpl administrativeService;

    private Province province1;
    private Province province2;
    private Ward ward1;
    private Ward ward2;
    private AdministrativeRegion region;
    private AdministrativeUnit adminUnit;

    @BeforeEach
    void setUp() {
        region = new AdministrativeRegion();
        region.setId(1);
        region.setName("North");

        adminUnit = new AdministrativeUnit();
        adminUnit.setId(1);
        adminUnit.setName("Phường");

        province1 = new Province();
        province1.setCode("01");
        province1.setName("Hà Nội");
        province1.setAdministrativeRegion(region);

        province2 = new Province();
        province2.setCode("02");
        province2.setName("Bắc Ninh");
        province2.setAdministrativeRegion(region);

        ward1 = new Ward();
        ward1.setCode("00001");
        ward1.setName("Ba Đình");
        ward1.setProvince(province1);
        ward1.setAdministrativeUnit(adminUnit);

        ward2 = new Ward();
        ward2.setCode("00002");
        ward2.setName("An Khánh");
        ward2.setProvince(province1);
        ward2.setAdministrativeUnit(null); // No admin unit
    }

    // ==================== getProvincesByRegion Tests ====================
    @Nested
    @DisplayName("getProvincesByRegion()")
    class GetProvincesByRegionTests {

        @Test
        @DisplayName("Path 1: Success - Returns provinces sorted by name")
        void getProvincesByRegion_Success() {
            when(adRegionRepository.existsById(1)).thenReturn(true);
            when(provinceRepository.findByAdministrativeRegion_Id(1))
                    .thenReturn(List.of(province1, province2));

            List<ProvinceResponse> result = administrativeService.getProvincesByRegion(1);

            assertThat(result).hasSize(2);
            // Vietnamese sorting: Bắc Ninh before Hà Nội
            assertThat(result.get(0).getName()).isEqualTo("Bắc Ninh");
            assertThat(result.get(1).getName()).isEqualTo("Hà Nội");
        }

        @Test
        @DisplayName("Path 2: Failure - Region not found")
        void getProvincesByRegion_RegionNotFound_ThrowsException() {
            when(adRegionRepository.existsById(999)).thenReturn(false);

            assertThatThrownBy(() -> administrativeService.getProvincesByRegion(999))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Administrative region not found");
        }

        @Test
        @DisplayName("Path 3: Returns empty list for region with no provinces")
        void getProvincesByRegion_NoProvinces_ReturnsEmpty() {
            when(adRegionRepository.existsById(1)).thenReturn(true);
            when(provinceRepository.findByAdministrativeRegion_Id(1)).thenReturn(List.of());

            List<ProvinceResponse> result = administrativeService.getProvincesByRegion(1);

            assertThat(result).isEmpty();
        }
    }

    // ==================== getAllProvinces Tests ====================
    @Nested
    @DisplayName("getAllProvinces()")
    class GetAllProvincesTests {

        @Test
        @DisplayName("Path 1: Success - Returns all provinces sorted")
        void getAllProvinces_Success() {
            when(provinceRepository.findAll()).thenReturn(List.of(province1, province2));

            List<ProvinceResponse> result = administrativeService.getAllProvinces();

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Path 2: Returns empty list when no provinces exist")
        void getAllProvinces_Empty() {
            when(provinceRepository.findAll()).thenReturn(List.of());

            List<ProvinceResponse> result = administrativeService.getAllProvinces();

            assertThat(result).isEmpty();
        }
    }

    // ==================== getAllProvincesPaginated Tests ====================
    @Nested
    @DisplayName("getAllProvincesPaginated()")
    class GetAllProvincesPaginatedTests {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        @DisplayName("Path 1: Success - Returns first page of provinces")
        void getAllProvincesPaginated_FirstPage_Success() {
            when(provinceRepository.findAll()).thenReturn(new ArrayList<>(List.of(province1, province2)));

            PageResponse<ProvinceResponse> result = administrativeService.getAllProvincesPaginated(pageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.isFirst()).isTrue();
        }

        @Test
        @DisplayName("Path 2: Returns empty content when page is beyond data")
        void getAllProvincesPaginated_BeyondData_ReturnsEmpty() {
            Pageable pageRequest = PageRequest.of(5, 10); // Page 5 with 10 items each
            when(provinceRepository.findAll()).thenReturn(new ArrayList<>(List.of(province1, province2)));

            PageResponse<ProvinceResponse> result = administrativeService.getAllProvincesPaginated(pageRequest);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(2);
        }
    }

    // ==================== getWardsByProvince Tests ====================
    @Nested
    @DisplayName("getWardsByProvince()")
    class GetWardsByProvinceTests {

        @Test
        @DisplayName("Path 1: Success - Returns wards sorted by name")
        void getWardsByProvince_Success() {
            when(provinceRepository.existsById("01")).thenReturn(true);
            when(wardRepository.findByProvince_Code("01")).thenReturn(List.of(ward1, ward2));

            List<WardResponse> result = administrativeService.getWardsByProvince("01");

            assertThat(result).hasSize(2);
            // Vietnamese sorting: An Khánh before Ba Đình
            assertThat(result.get(0).getName()).contains("An Khánh");
            assertThat(result.get(1).getName()).contains("Ba Đình");
        }

        @Test
        @DisplayName("Path 2: Failure - Province not found")
        void getWardsByProvince_ProvinceNotFound_ThrowsException() {
            when(provinceRepository.existsById("99")).thenReturn(false);

            assertThatThrownBy(() -> administrativeService.getWardsByProvince("99"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Province not found");
        }

        @Test
        @DisplayName("Path 3: Ward name includes admin unit prefix")
        void getWardsByProvince_IncludesAdminUnit() {
            when(provinceRepository.existsById("01")).thenReturn(true);
            when(wardRepository.findByProvince_Code("01")).thenReturn(List.of(ward1));

            List<WardResponse> result = administrativeService.getWardsByProvince("01");

            // ward1 has adminUnit "Phường" + name "Ba Đình" = "Phường Ba Đình"
            assertThat(result.get(0).getName()).isEqualTo("Phường Ba Đình");
        }

        @Test
        @DisplayName("Path 4: Ward without admin unit uses name only")
        void getWardsByProvince_NoAdminUnit_UsesNameOnly() {
            when(provinceRepository.existsById("01")).thenReturn(true);
            when(wardRepository.findByProvince_Code("01")).thenReturn(List.of(ward2));

            List<WardResponse> result = administrativeService.getWardsByProvince("01");

            // ward2 has no adminUnit, so just "An Khánh"
            assertThat(result.get(0).getName()).isEqualTo("An Khánh");
        }
    }

    // ==================== getWardsByProvincePaginated Tests ====================
    @Nested
    @DisplayName("getWardsByProvincePaginated()")
    class GetWardsByProvincePaginatedTests {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        @DisplayName("Path 1: Success - Returns first page of wards")
        void getWardsByProvincePaginated_Success() {
            when(provinceRepository.existsById("01")).thenReturn(true);
            when(wardRepository.findByProvince_Code("01")).thenReturn(new ArrayList<>(List.of(ward1, ward2)));

            PageResponse<WardResponse> result = administrativeService.getWardsByProvincePaginated("01", pageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("Path 2: Failure - Province not found")
        void getWardsByProvincePaginated_ProvinceNotFound_ThrowsException() {
            when(provinceRepository.existsById("99")).thenReturn(false);

            assertThatThrownBy(() -> administrativeService.getWardsByProvincePaginated("99", pageable))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Province not found");
        }

        @Test
        @DisplayName("Path 3: Returns empty content when page is beyond data")
        void getWardsByProvincePaginated_BeyondData_ReturnsEmpty() {
            Pageable pageRequest = PageRequest.of(5, 10);
            when(provinceRepository.existsById("01")).thenReturn(true);
            when(wardRepository.findByProvince_Code("01")).thenReturn(new ArrayList<>(List.of(ward1, ward2)));

            PageResponse<WardResponse> result = administrativeService.getWardsByProvincePaginated("01", pageRequest);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(2);
        }
    }
}
